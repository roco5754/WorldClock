package rcorona.worldclock;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.provider.SyncStateContract;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import rcorona.worldclock.CityInfo;
import rcorona.worldclock.CityDBHelper;


public class scroll_list extends ListActivity {

    //database helper to be used in this activity
    private CityDBHelper helper;

    //return code from action_add_city
    private int mRequestCode = 100;
    //private int flickrRequestCode = 101; //couldn't get flickr api to work

    //declare one intent to be reused
    private Intent intent;

    //This loads when the activity launches
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        //show the scroll_list layout on screen
        setContentView(R.layout.activity_scroll_list);



        //updateUI();


        //set up handler to "live" refresh clocks
        final Handler handler=new Handler();
        handler.post(new Runnable(){

            @Override
            public void run() {

                //update all of the users selected city times
                updateTime();

                //update UI's list independent of other functions in case they are ever replaced
                updateUI();


                handler.postDelayed(this,1000); // 1 second

            }

        });




    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_scroll_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {

            //call this if the + button is pressed to allow user to search cities
            case R.id.action_add_city:

                //launch citySearch and wait suspend for result
                intent = new Intent(this, citySearch.class);
                startActivityForResult(intent, mRequestCode);
                return true;

/*
            //Button to launch a sample flicker api (found here: http://android-er.blogspot.com/2011/07/access-flickr-api-using-httpclient.html)
            //I ran into errors launching a network request, and unsuccessfully attempted to use an AsyncTask to launch this in the background
            //If I had been able to launch this, my next step would have been to impliment my own flickr api to load an image
            case R.id.action_Flickr:
                Log.d("In action flicker", "success1");
                //intent = new Intent(this, AndroidFlickrActivity.class);
                //startActivityForResult(intent, flickrRequestCode);
                intent = new Intent(this, AsyncTaskFlickr.class);
                startActivityForResult(intent, 101);
                Log.d("In action flicker", "success1");
                return true;
            //http://api.flickr.com/services/rest/?&method=flickr.people.getPublicPhotos&api_key=[your api key here]&user_id=[your user id here]
*/
            default:
                return false;


        }

    }

    //This is called when another activity returns control to this activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //if the activity returns a code that equals the one given by citySearch, enter this
        if(requestCode == mRequestCode && resultCode == RESULT_OK){

            //get city name, GMT offset and zone identifier from intent
            String cityNameString = data.getStringExtra("cityName");
            int gmtOffset = data.getIntExtra("gmtOffset", 0);
            String zone = data.getStringExtra("zone");

            //create a DateFormat object to hold the time
            DateFormat time = DateFormat.getTimeInstance();

            //Determine android's time zone
            TimeZone currentZone = TimeZone.getDefault();

            //get the offset between androids time zone and GMT/UTC
            int currentUTCOffset = currentZone.getRawOffset();

            //Convert the time zones GMT offset into seconds, store in long for time.format
            long zoneOffset = 3600000*Long.valueOf(gmtOffset);

            //The city's time = SystemTime + TimeZone's offset - Androids current offset
            String cityTime = time.format(System.currentTimeMillis() + zoneOffset - currentUTCOffset);

            //debug log to show which city name is added
            Log.d("got_string_in_scroll", cityNameString);


            //sqlite database helper to find next writable position
            CityDBHelper helper = new CityDBHelper(scroll_list.this);
            SQLiteDatabase db = helper.getWritableDatabase();

            //Content Values are what is being put into database, clear to be safe
            ContentValues values = new ContentValues();
            values.clear();

            //Put all of the data into the contentvalue
            values.put(CityInfo.Columns.CITYNAME,cityNameString);
            values.put(CityInfo.Columns.GMTOFFSET,gmtOffset);
            values.put(CityInfo.Columns.CITYTIME,cityTime);
            values.put(CityInfo.Columns.ZONE,zone);

            //Logs from debug process
            //Log.d("got_string_in_scroll", cityNameString);
            //Log.d("got_string_in_scroll2",CityInfo.Columns.CITYNAME);

            //put the content values into the table/database, finally!!
            db.insertWithOnConflict(CityInfo.TABLE,null,values,SQLiteDatabase.CONFLICT_IGNORE);

            //restart the activity that we've returned from
            Intent intent = new Intent(this, scroll_list.class);
            startActivity(intent);
        }
    }

    //function to update the list's user interface
    private void updateUI() {

        //helps to navigate the database
        helper = new CityDBHelper(scroll_list.this);

        //grab the database
        SQLiteDatabase sqlDB = helper.getReadableDatabase();

        //a cursor to help scroll throgh all of the data matching columns
        Cursor cursor = sqlDB.query(CityInfo.TABLE,
                new String[]{CityInfo.Columns._ID, CityInfo.Columns.CITYNAME, CityInfo.Columns.GMTOFFSET, CityInfo.Columns.CITYTIME, CityInfo.Columns.ZONE},
                null,null,null,null,null);

        //Adaptor to fill the XML with data pointed to by the cursor
        SimpleCursorAdapter listAdapter = new SimpleCursorAdapter(
                this,
                R.layout.list_layout,
                cursor,
                new String[] { CityInfo.Columns.CITYNAME, CityInfo.Columns.GMTOFFSET, CityInfo.Columns.CITYTIME, CityInfo.Columns.ZONE},
                new int[] { R.id.display_city, R.id.display_gmtOffset, R.id.display_time, R.id.display_zone},
                0

        );

        //update the list
        this.setListAdapter(listAdapter);

        //close the db to avoid leaks
        sqlDB.close();

    }

    //function to update each time that the user has chosen
    private void updateTime() {

        //set up the database
        helper = new CityDBHelper(scroll_list.this);
        SQLiteDatabase sqlDB = helper.getReadableDatabase();

        //set up Dateformat to hold the time
        DateFormat time = DateFormat.getTimeInstance();


        //object to hold stuff to put into the time column
        ContentValues contentValues = new ContentValues();

        //Cursor to go through the values we care about, name to search the table and offset to compute time
        Cursor cursor = sqlDB.query(CityInfo.TABLE,
                new String[]{CityInfo.Columns.CITYNAME, CityInfo.Columns.GMTOFFSET},
                null,null,null,null,null);

        //Find current time zone to use in time calculation
        TimeZone currentZone = TimeZone.getDefault();
        int currentUTCOffset = currentZone.getRawOffset();

        //declare variable for zone offset
        long offset;

        //if there are things being pointed to by the cursor, enter
        if(cursor.moveToFirst())
        {

            do {
                //string array of the city name to be used in .put
                String[] cityName = new String[]{cursor.getString(0)};

                //compute the timezone offset in seconds
                offset = 3600000*Long.valueOf(cursor.getString(1));

                //put the city's new time into the table at when the city name matches
                //potential error down the road: if two cities share a name but in different time zones
                contentValues.put("cityTime", time.format(System.currentTimeMillis()+offset-Long.valueOf(currentUTCOffset)));

                //update the database
                sqlDB.update("cities", contentValues, CityInfo.Columns.CITYNAME + "= ?", cityName);

                //if the cursor is still on anything, repeat
            }while(cursor.moveToNext());
        }
        //close the database
        sqlDB.close();

    }

    //method to delete the city from the db if the user desires
    public void deleteCity(View view){

        //determine the city name of the one chosen to be deleted
        View v = (View) view.getParent();
        TextView taskTextView = (TextView) v.findViewById(R.id.display_city);
        String task = taskTextView.getText().toString();

        //string for execSQL to delete the row
        String sql = String.format("DELETE FROM %s WHERE %s = '%s'",
                CityInfo.TABLE,
                CityInfo.Columns.CITYNAME,
                task);


        //delete the row, update the UI
        helper = new CityDBHelper(scroll_list.this);
        SQLiteDatabase sqlDB = helper.getWritableDatabase();
        sqlDB.execSQL(sql);
        updateUI();

    }
}



