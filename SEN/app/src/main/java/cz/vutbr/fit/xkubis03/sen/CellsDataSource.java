/**
 * Author: Radim KUBIŠ, xkubis03
 * Name: CellsDataSource.java
 *
 * Class for database of cells.
 */

package cz.vutbr.fit.xkubis03.sen;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;

public class CellsDataSource {

    // Attributes **********************************************************************************

    /* Database object */
    private SQLiteDatabase database;
    /* Database helper object */
    private SQLiteHelper dbHelper;
    /* Database columns */
    private String[] allColumns = {
            SQLiteHelper.COLUMN_ID,
            SQLiteHelper.COLUMN_LAC,
            SQLiteHelper.COLUMN_CID,
            SQLiteHelper.COLUMN_LAT,
            SQLiteHelper.COLUMN_LON
    };

    // Constructors ********************************************************************************

    public CellsDataSource(Context context) {
        dbHelper = new SQLiteHelper(context);
    }

    // Database operations *************************************************************************

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    /* Insert new cell to database */
    public Cell createCell(int lac, int cid, double lat, double lon) {
        /* Create values object */
        ContentValues values = new ContentValues();
        /* Insert LAC */
        values.put(SQLiteHelper.COLUMN_LAC, lac);
        /* Insert CID */
        values.put(SQLiteHelper.COLUMN_CID, cid);
        /* Insert LAT */
        values.put(SQLiteHelper.COLUMN_LAT, lat);
        /* Insert LON */
        values.put(SQLiteHelper.COLUMN_LON, lon);
        /* Insert values to DB and get ID */
        long insertId = database.insert(SQLiteHelper.TABLE_CELLS, null, values);
        /* Get cursor to new cell */
        Cursor cursor = database.query(
                SQLiteHelper.TABLE_CELLS,
                allColumns,
                SQLiteHelper.COLUMN_ID + "=" + insertId,
                null, null, null, null);
        /* Move cursor to cell */
        cursor.moveToFirst();
        /* Make cell object from cursor */
        Cell newCell = cursorToCell(cursor);
        /* Close cursor */
        cursor.close();
        /* Return new cell */
        return newCell;
    }

    /* Delete cell from database */
    public void deleteCell(Cell cell) {
        /* Get cell ID */
        long id = cell.getId();
        /* Print info */
        System.out.println("Cell deleted with id: " + id);
        /* Delete cell */
        database.delete(SQLiteHelper.TABLE_CELLS, SQLiteHelper.COLUMN_ID + " = " + id, null);
    }

    /* Get number of rows in database */
    public int numberOfRows() {
        /* Get number of rows */
        Cursor cursor = database.rawQuery("SELECT Count(*) FROM " + SQLiteHelper.TABLE_CELLS, null);
        /* Move cursor to number */
        cursor.moveToFirst();
        /* Get number */
        int number = cursor.getInt(0);
        /* Close cursor */
        cursor.close();
        /* Return number of rows */
        return number;
    }

    /* Get list of all cells in DB */
    public List<Cell> getAllCells() {
        /* Create list for cells */
        List<Cell> cells = new ArrayList<Cell>();

        /* Get all cell from DB */
        Cursor cursor = database.query(SQLiteHelper.TABLE_CELLS,
                allColumns, null, null, null, null, null);
        /* Move cursor to first cell */
        cursor.moveToFirst();
        /* For every cell in list */
        while (!cursor.isAfterLast()) {
            /* Create cell from DB */
            Cell cell = cursorToCell(cursor);
            /* Add cell to list */
            cells.add(cell);
            /* Move cursor */
            cursor.moveToNext();
        }

        /* Close cursor */
        cursor.close();
        /* Return list */
        return cells;
    }

    /* Get one cell by LAC and CID */
    public Cell getCell(int lac, int cid) {
        /* Cell to return */
        Cell cell = null;

        /* Get cell from DB */
        Cursor cursor = database.query(SQLiteHelper.TABLE_CELLS,
                allColumns, SQLiteHelper.COLUMN_LAC + "=" + lac + " and " + SQLiteHelper.COLUMN_CID + "=" + cid,
                null, null, null, null);
        /* Move cursor to cell */
        cursor.moveToFirst();
        /* Create cell from DB */
        cell = cursorToCell(cursor);
        /* Close cursor */
        cursor.close();
        /* Return cell */
        return cell;
    }

    /* Get location from DB by LAC and CID */
    public Location getLocation(int lac, int cid) {
        /* Create new location object */
        Location location = new Location("xkubis03");

        /* Columns to query */
        String[] cols = {SQLiteHelper.COLUMN_LAT, SQLiteHelper.COLUMN_LON};
        /* Get location from DB */
        Cursor cursor = database.query(
                SQLiteHelper.TABLE_CELLS,
                cols,
                SQLiteHelper.COLUMN_LAC + " = " + lac + " and " + SQLiteHelper.COLUMN_CID + " = " + cid,
                null, null, null, null
        );
        /* Move cursor to location */
        cursor.moveToFirst();
        /* If cursor is not empty */
        if(cursor.getCount() > 0) {
            /* Set location */
            location = cursorToLocation(cursor);
        }
        /* Close cursor */
        cursor.close();
        /* Return location of cell */
        return location;
    }

    /* Convert cursor to Cell object */
    private Cell cursorToCell(Cursor cursor) {
        /* Create new cell */
        Cell cell = new Cell();
        /* Set ID */
        cell.setId(cursor.getLong(0));
        /* Set LAC */
        cell.setLac(cursor.getInt(1));
        /* Set CID */
        cell.setCid(cursor.getInt(2));
        /* Set location */
        cell.setLocation(cursor.getDouble(3), cursor.getDouble(4));
        /* Return cell */
        return cell;
    }

    /* Convert cursor to location */
    private Location cursorToLocation(Cursor cursor) {
        /* Create new location */
        Location location = new Location("xkubis03");
        /* Set latitude */
        location.setLatitude(cursor.getDouble(0));
        /* Set longitude */
        location.setLongitude(cursor.getDouble(1));
        /* Return location */
        return location;
    }

}
