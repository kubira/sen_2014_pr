/**
 * Author: Radim KUBIŠ, xkubis03
 * Name: MainActivity.java
 *
 * Class for main activity of application.
 */

package cz.vutbr.fit.xkubis03.sen;

import android.app.AlertDialog;
import android.location.Location;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;


public class MainActivity extends ActionBarActivity {

    // Attributes **********************************************************************************

    /* CellsAdapter object */
    private CellsAdapter ca;
    /* Cell list layout */
    private LinearLayout layout;
    /* Database controller */
    private CellsDataSource db;
    /* Update button */
    private Button btn;
    /* Fragment for map */
    private MapFragment mapFragment;
    /* List of cell markers */
    private ArrayList<Marker> markers;
    /* View with latitude */
    private TextView latView;
    /* View with longitude */
    private TextView lonView;
    /* Scroll view with cell list */
    private ScrollView scroll;

    // Activity operations *************************************************************************

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /* Set layout */
        setContentView(R.layout.activity_main);
        /* Create markers */
        markers = new ArrayList<Marker>();
        /* Get BTS list layout */
        layout = (LinearLayout)findViewById(R.id.BTSList);
        /* Get latitude view */
        latView = (TextView)findViewById(R.id.lat);
        /* Get longitude view */
        lonView = (TextView)findViewById(R.id.lon);
        /* Get scroll view */
        scroll = (ScrollView)findViewById(R.id.scrollView1);

        /* Get update button */
        btn = (Button)findViewById(R.id.updateBtn);
        /* Disable button */
        btn.setEnabled(false);
        /* Get map fragment */
        mapFragment = (MapFragment)getFragmentManager().findFragmentById(R.id.map);
        /* Create cell adapter */
        ca = new CellsAdapter(this);
        /* Create database */
        db = new CellsDataSource(this);
        /* Open database */
        db.open();

        /* If database of BTS in device is empty */
        if(db.numberOfRows() == 0) {
            /* Create info view */
            TextView tv = new TextView(this);
            /* Set info text */
            tv.setText(getResources().getString(R.string.creatingDB));
            /* Add view to layout */
            layout.addView(tv);

            /* Index of BTS in file */
            int btsIndex = 0;
            /* File reader */
            BufferedReader reader = null;

            try {
                /* Fill database from asset file */

                /* Create new file reader */
                reader = new BufferedReader(new InputStreamReader(getAssets().open(getResources().getString(R.string.cellFile)), getResources().getString(R.string.cellFileEncoding)));

                /* Read first line with column names */
                String mLine = reader.readLine();
                /* Read first data line */
                mLine = reader.readLine();
                /* While next line */
                while (mLine != null) {
                    /* Split line by comma */
                    String[] parts = mLine.split(",");

                    /* Insert new cell to DB */
                    db.createCell(
                            Integer.parseInt(parts[0]),
                            Integer.parseInt(parts[1]),
                            Double.parseDouble(parts[3]),
                            Double.parseDouble(parts[2])
                    );

                    /* Get next line from file */
                    mLine = reader.readLine();
                    /* Increment BTS index */
                    btsIndex++;

                    /* Update info text */
                    if(btsIndex % 500 == 0) {
                        /* Add dot to info text */
                        tv.setText(tv.getText() + ".");
                    }
                }
            } catch (IOException e) {
                /* If IO exception */
                System.err.println("Exception: " + e.toString());
            } finally {
                /* File is read */
                if (reader != null) {
                    try {
                        /* Close file reader */
                        reader.close();
                    } catch (IOException e) {
                        /* If IO exception */
                        System.err.println("Exception: " + e.toString());
                    }
                }
            }
        }

        /* Print info about BTS DB size */
        System.out.println("BTS DB size: " + db.numberOfRows());

        /* Process first BTS localization */
        updateLocation(null);

        /* Enable update button */
        btn.setEnabled(true);

        /* Hide cell list */
        scroll.removeView(layout);
    }

    /* Update location of device */
    public void updateLocation(View view) {
        /* Google map for markers */
        GoogleMap map = mapFragment.getMap();
        /* Remove all BTS info views */
        layout.removeAllViews();
        /* Remove all markers from map */
        Iterator<Marker> mit = markers.iterator();
        while(mit.hasNext()) {
            mit.next().remove();
        }
        markers.clear();
        /* Get new list of available cells */
        ca.updateCells();

        /* Add header for cells */
        TextView tv = new TextView(this);
        tv.setText("[type,lac,cid,sStrength,lat,lon]");
        layout.addView(tv);

        /* Get available cells */
        ArrayList<Cell> arr = ca.getCells();
        /* Get cells iterator */
        Iterator<Cell> it = arr.iterator();

        /* Add info about every available cell */
        while(it.hasNext()) {
            /* Get next cell */
            Cell c = it.next();
            /* Add text view with info to layout */
            TextView tvx = new TextView(this);
            tvx.setText(c.toString());
            layout.addView(tvx);

            /* If cell has location */
            if(c.getLocation().getLatitude() != 0) {
                /* Insert marker to map */
                markers.add(map.addMarker(new MarkerOptions()
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.vez))
                        .title(c.getType().toString())
                        .snippet(c.getLac() + ":" + c.getCid() + ":" + c.getStrength())
                        .position(new LatLng(c.getLocation().getLatitude(), c.getLocation().getLongitude()))));
            }
        }

        /* If some cells with location exists */
        if(markers.size() > 0) {
            /* Get device location */
            Location loc = ca.getLocation();

            /* Show latitude */
            latView.setText("Latitude:\t\t" + loc.getLatitude());
            /* Show longitude */
            lonView.setText("Longitude:\t" + loc.getLongitude());

            /* Create location for marker */
            LatLng me = new LatLng(loc.getLatitude(), loc.getLongitude());

            /* Add marker to map */
            markers.add(map.addMarker(new MarkerOptions()
                    .title("My device")
                    .snippet("I am here")
                    .position(me)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_launcher))));

            /* Move camera to device marker */
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(me, 14));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_showHide) {
            /* If show/hide list */
            if(scroll.findViewById(R.id.BTSList) != null) {
                /* Hide */
                scroll.removeView(layout);
            } else {
                /* Show */
                scroll.addView(layout);
            }
            return true;
        } else if(id == R.id.action_about) {
            /* If about */

            /* Instantiate alert dialog builder */
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            /* Set message and title */
            builder.setMessage(R.string.message)
                    .setTitle(R.string.title);
            /* Create dialog */
            AlertDialog dialog = builder.create();
            /* Show dialog */
            dialog.show();
        }

        return super.onOptionsItemSelected(item);
    }
}
