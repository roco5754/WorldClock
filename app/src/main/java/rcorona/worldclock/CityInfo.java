package rcorona.worldclock;

/**
 * Defines the database used to store all of the information needed to display user's cities
 * created by CityDBHelper
 */

import android.provider.BaseColumns;

public class CityInfo {

   //fields
    public static final String DB_NAME = "rcorona.worldclock.db.cities";
    public static final int DB_VERSION = 1;
    public static final String TABLE = "cities";


    public class Columns {
        public static final String CITYNAME = "cityName";
        public static final String _ID = BaseColumns._ID;
        public static final String GMTOFFSET = "gmtOffset";
        public static final String CITYTIME = "cityTime";
        public static final String ZONE = "zone";
    }

//thought I would use these, but I didnt need them since this is a database
/*
    //constructors
    public CityInfo(){}

    public CityInfo(String newName, int newOffset) {
        super();
        this.cityName = newName;
        this.GMT_offset = newOffset;
    }

    //methods
    public void setCityName(String newName) {
        cityName = newName;
    }

    public void setGMT_offset(int newOffset) {
        GMT_offset = newOffset;
    }

    public String getCityName() {return cityName; }

    public int getOffset() {return GMT_offset; }

*/
}