package rcorona.worldclock;

/**
 * database helper to create a database based on the structre defined in CityInfo object
 * basically creates a string to define the table and calls .execSQL to create it
 * the db it helps create is populated with the user's city's
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class CityDBHelper extends SQLiteOpenHelper {


    public CityDBHelper(Context context) {
        super(context, CityInfo.DB_NAME, null, CityInfo.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqlDB) {
        String sqlQuery =
                String.format("CREATE TABLE %s (" +
                                "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                "%s TEXT," + "%s TEXT," + "%s TEXT," + "%s TEXT)" , CityInfo.TABLE,
                        CityInfo.Columns.CITYNAME,CityInfo.Columns.GMTOFFSET, CityInfo.Columns.CITYTIME, CityInfo.Columns.ZONE);

        Log.d("TaskDBHelper","Query to form table: "+sqlQuery);
        sqlDB.execSQL(sqlQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqlDB, int i, int i2) {
        sqlDB.execSQL("DROP TABLE IF EXISTS "+CityInfo.TABLE);
        onCreate(sqlDB);
    }
}



