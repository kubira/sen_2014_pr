/**
 * Author: Radim KUBIŠ, xkubis03
 * Name: SQLiteHelper.java
 *
 * Class for SQLite helper.
 */

package cz.vutbr.fit.xkubis03.sen;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SQLiteHelper extends SQLiteOpenHelper {

    // Constants ***********************************************************************************

    public static final String TABLE_CELLS = "cells";
    public static final String COLUMN_ID  = "id";
    public static final String COLUMN_LAC = "lac";
    public static final String COLUMN_CID = "cid";
    public static final String COLUMN_LAT = "lat";
    public static final String COLUMN_LON = "lon";

    private static final String DATABASE_NAME = "cells.db";
    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_CREATE =
            "create table " + TABLE_CELLS + "("
                    + COLUMN_ID +  " integer primary key autoincrement, "
                    + COLUMN_LAC + " integer, "
                    + COLUMN_CID + " integer, "
                    + COLUMN_LAT + " real, "
                    + COLUMN_LON + " real);";

    // Constructors ********************************************************************************

    public SQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Helper operations ***************************************************************************

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(SQLiteHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CELLS);
        onCreate(db);
    }

}