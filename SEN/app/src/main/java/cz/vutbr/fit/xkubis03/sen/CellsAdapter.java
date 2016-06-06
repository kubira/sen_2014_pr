/**
 * Author: Radim KUBIŠ, xkubis03
 * Name: CellsAdapter.java
 *
 * Class for BTS cell adapter.
 */

package cz.vutbr.fit.xkubis03.sen;

import android.content.Context;
import android.location.Location;
import android.telephony.CellIdentityCdma;
import android.telephony.CellIdentityGsm;
import android.telephony.CellIdentityLte;
import android.telephony.CellInfo;
import android.telephony.CellInfoCdma;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellLocation;
import android.telephony.CellSignalStrengthCdma;
import android.telephony.CellSignalStrengthGsm;
import android.telephony.CellSignalStrengthLte;
import android.telephony.NeighboringCellInfo;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;

import java.util.ArrayList;
import java.util.Iterator;

public class CellsAdapter {

    // Attributes **********************************************************************************

    /* List of available cells */
    protected ArrayList<Cell> cells;
    /* App context */
    protected Context context;
    /* Database adapter */
    protected CellsDataSource db;

    // Constructors ********************************************************************************

    public CellsAdapter() {
        setCells(new ArrayList<Cell>());
        setContext(null);
        db = new CellsDataSource(context);
        db.open();
    }

    public CellsAdapter(Context context) {
        setCells(new ArrayList<Cell>());
        setContext(context);
        db = new CellsDataSource(context);
        db.open();
    }

    // Getters and setters *************************************************************************

    public ArrayList<Cell> getCells() {
        return cells;
    }

    public void setCells(ArrayList<Cell> cells) {
        this.cells = cells;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    // Adapter operations **************************************************************************

    public void addCell(Cell cell) {
        cells.add(cell);
    }

    private void clearCells() {
        this.cells.clear();
    }

    /* Update list of cells */
    public void updateCells() {
        /* Clear all cells from list */
        clearCells();
        /* Get telephony manager of app */
        TelephonyManager tm = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);

        /* Get list of neighboring cells */
        ArrayList<NeighboringCellInfo> nciList = (ArrayList<NeighboringCellInfo>)tm.getNeighboringCellInfo();
        /* Get list of all available cells */
        ArrayList<CellInfo> aciList = (ArrayList<CellInfo>)tm.getAllCellInfo();
        /* Get registered cell */
        CellLocation cl = tm.getCellLocation();

        /* Process neighboring cells */
        if(nciList != null) {
            /* Get list iterator */
            Iterator<NeighboringCellInfo> nciIt = nciList.iterator();

            /* For every cell in list */
            while (nciIt.hasNext()) {
                /* Get cell */
                NeighboringCellInfo nci = nciIt.next();
                /* Create new cell */
                Cell c = new Cell();

                /* Set LAC */
                c.setLac(nci.getLac());
                /* Set CID */
                c.setCid(nci.getCid());
                /* Convert ASU level to 0-100 interval */
                c.setStrength((int)((100.0 / 31.0) * nci.getRssi()));
                /* Get location of cell from DB */
                c.setLocation(db.getLocation(nci.getLac(), nci.getCid()));

                /* Set network type of cell */
                switch(nci.getNetworkType()) {
                    case TelephonyManager.NETWORK_TYPE_UNKNOWN: {
                        c.setType(CellType.UNKNOWN);
                        break;
                    }
                    case TelephonyManager.NETWORK_TYPE_GPRS: {
                        c.setType(CellType.GPRS);
                        break;
                    }
                    case TelephonyManager.NETWORK_TYPE_EDGE: {
                        c.setType(CellType.EDGE);
                        break;
                    }
                    case TelephonyManager.NETWORK_TYPE_UMTS: {
                        c.setType(CellType.UMTS);
                        break;
                    }
                    case TelephonyManager.NETWORK_TYPE_HSPA: {
                        c.setType(CellType.HSPA);
                        break;
                    }
                    case TelephonyManager.NETWORK_TYPE_HSDPA: {
                        c.setType(CellType.HSDPA);
                        break;
                    }
                    case TelephonyManager.NETWORK_TYPE_HSUPA: {
                        c.setType(CellType.HSUPA);
                        break;
                    }
                }

                /* Add cell to list */
                addCell(c);
            }
        }

        /* Process AllCellInfo cells */
        if(aciList != null) {
            /* Get list iterator */
            Iterator<CellInfo> aciIt = aciList.iterator();

            /* For every cell in list */
            while (aciIt.hasNext()) {
                /* Get cell */
                CellInfo aci = aciIt.next();
                /* New cell */
                Cell c = null;

                /* If cell is CDMA */
                if(aci instanceof CellInfoCdma) {
                    /* Create new cell */
                    c = new Cell();
                    /* Re-type cell */
                    CellInfoCdma cellInfoCdma = (CellInfoCdma)aci;
                    /* Get cell identity */
                    CellIdentityCdma cellIdentityCdma = cellInfoCdma.getCellIdentity();
                    /* Get cell signal info */
                    CellSignalStrengthCdma cellSignalStrengthCdma = cellInfoCdma.getCellSignalStrength();
                    /* Convert coordinates */
                    c.setLocation((cellIdentityCdma.getLatitude() * 0.25 / 3600.0), (cellIdentityCdma.getLongitude() * 0.25 / 3600.0));
                    /* Convert ASU level to 0-100 interval */
                    c.setStrength((int)((100.0 / 97.0) * cellSignalStrengthCdma.getAsuLevel()));
                    /* Set network type */
                    c.setType(CellType.CDMA);

                /* If cell is GSM */
                } else if(aci instanceof CellInfoGsm) {
                    /* Create new cell */
                    c = new Cell();
                    /* Re-type cell */
                    CellInfoGsm cellInfoGsm = (CellInfoGsm)aci;
                    /* Get cell identity */
                    CellIdentityGsm cellIdentityGsm = cellInfoGsm.getCellIdentity();
                    /* Get cell signal info */
                    CellSignalStrengthGsm cellSignalStrengthGsm = cellInfoGsm.getCellSignalStrength();
                    /* Set LAC */
                    c.setLac(cellIdentityGsm.getLac());
                    /* Set CID */
                    c.setCid(cellIdentityGsm.getCid());
                    /* Convert ASU level to 0-100 interval */
                    c.setStrength((int)((100.0 / 31.0) * cellSignalStrengthGsm.getAsuLevel()));
                    /* Set location from DB */
                    c.setLocation(db.getLocation(cellIdentityGsm.getLac(), cellIdentityGsm.getCid()));
                    /* Set network type */
                    c.setType(CellType.GSM);

                /* If cell is LTE */
                } else if(aci instanceof CellInfoLte) {
                    /* Create new cell */
                    c = new Cell();
                    /* Re-type cell */
                    CellInfoLte cellInfoLte = (CellInfoLte)aci;
                    /* Get cell identity */
                    CellIdentityLte cellIdentityLte = cellInfoLte.getCellIdentity();
                    /* Get cell signal info */
                    CellSignalStrengthLte cellSignalStrengthLte = cellInfoLte.getCellSignalStrength();
                    /* Set LAC */
                    c.setLac(cellIdentityLte.getTac());
                    /* Set CID */
                    c.setCid(cellIdentityLte.getCi());
                    /* Convert ASU level to 0-100 interval */
                    c.setStrength((int)((100.0 / 97.0) * cellSignalStrengthLte.getAsuLevel()));
                    /* Get location from DB */
                    c.setLocation(db.getLocation(cellIdentityLte.getTac(), cellIdentityLte.getCi()));
                    /* Set network type */
                    c.setType(CellType.LTE);
                } else {
                    // Unknown cell type - do nothing
                }

                /* If cell is created */
                if(c != null) {
                    /* Add cell to list */
                    addCell(c);
                }
            }
        }

        /* If current active cell is not null*/
        if(cl != null) {
            /* New cell */
            Cell c = null;

            /* If cell is CDMA */
            if(cl instanceof CdmaCellLocation) {
                /* Create new cell */
                c = new Cell();
                /* Get cell info */
                CdmaCellLocation cdmaCellLocation = (CdmaCellLocation)cl;

                /* Set CID */
                c.setCid(cdmaCellLocation.getBaseStationId());
                /* Convert coordinates */
                c.setLocation((cdmaCellLocation.getBaseStationLatitude() * 0.25 / 3600.0), (cdmaCellLocation.getBaseStationLongitude() * 0.25 / 3600.0));
                /* Set strength to 100 - active cell*/
                c.setStrength(100);
                /* Set cell typ */
                c.setType(CellType.CDMA);

            /*If cell is GSM */
            } else if(cl instanceof GsmCellLocation) {
                /* Create new cell */
                c = new Cell();
                /* Get cell info */
                GsmCellLocation gsmCellLocation = (GsmCellLocation)cl;
                /* Set LAC */
                c.setLac(gsmCellLocation.getLac());
                /* Set CID */
                c.setCid(gsmCellLocation.getCid());
                /* Get location from DB */
                c.setLocation(db.getLocation(gsmCellLocation.getLac(), gsmCellLocation.getCid()));
                /* Set strength to 100 - active cell*/
                c.setStrength(100);
                /* Set cell typ */
                c.setType(CellType.GSM);
            } else {
                // Unknown cell type
            }

            /* If cell is not null */
            if(c != null) {
                /* Add cell to list */
                addCell(c);
            }
        }
    }

    /* Get location of device from cells list */
    public Location getLocation() {
        /* Create new location */
        Location location = new Location("xkubis03");

        /* Number of cells with location */
        int numberOfCells = 0;
        /* Sum of latitude */
        double lat = 0.0;
        /* Sum of longitude */
        double lon = 0.0;
        /* Sum of strength */
        double strength = 0.0;

        /* Get cells iterator */
        Iterator<Cell> it = cells.iterator();

        /* Process cell list */
        while(it.hasNext()) {
            /* Get next cell */
            Cell c = it.next();

            /* If cell has location */
            if (c.getLocation().getLatitude() != 0) {
                /* Add latitude */
                lat += c.getLocation().getLatitude() * c.getStrength();
                /* Add longitude */
                lon += c.getLocation().getLongitude() * c.getStrength();
                /* Add strength */
                strength += c.getStrength();
                /* Increment number of cells */
                numberOfCells++;
            }
        }

        /* Get latitude */
        location.setLatitude(lat /= strength);
        /* Get longitude */
        location.setLongitude(lon /= strength);

        /* Return location */
        return location;
    }
}
