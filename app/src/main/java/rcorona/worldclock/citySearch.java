package rcorona.worldclock;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Array;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.TimeZone;

import au.com.bytecode.opencsv.CSVReader;


//class for the city searching activity, comes when you press the + button
public class citySearch extends Activity {

    //public final static String EXTRA_MESSAGE = "rcorona.worldclock.MESSAGE";
    //public static List<String[]> list;

    //call up the city_search xml layout
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_city_search);


        getActionBar().setDisplayHomeAsUpEnabled(true);


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_city_search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.


       //if user presses action bar back button, go to parent/ app's home screen
        switch(item.getItemId()){
            //Go to home/main screen
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    //evoked when the user presses search button
    //I could not get a "search as you type" to work, so I used a button instead
    public void searchCity(View view){

        //get the text typed in by user
        EditText editText = (EditText) findViewById(R.id.enter_city);
        String citySearch = editText.getText().toString();

        //load the csv file containing all of the data about cities
        List<String[]> list = loadCSV();
        //Log.d("CSVLoad", list.get(3)[0]);

        //create a sting array to be searched, i'm sure its possible to directly search a list, but this works for now
        String[] cityNames = new String[list.size()];
        //Log.d("I got here",Integer.toString(list.size()));

        //fill the array with city names
        for (int i=0;i<list.size();i++) cityNames[i] = list.get(i)[0];
        //Log.d("string", cityNames[2]);

        //list to hold the matches from the search
        ArrayList cityMatch = new ArrayList<CityIndex>();
        Log.d("made array list","yes");

        //create a list view adaptor to eventually display the matches
        ListView listView = (ListView) findViewById(R.id.listCitySearch);
        ArrayAdapter<CityIndex> adapter = new ArrayAdapter<CityIndex>(this, R.layout.city_search_list, R.id.city_name,cityMatch);
        listView.setAdapter(adapter);


        //run through the list comparing the beginning of each city name with the users search
        //Would have added two features if I had the time
        //1: ability to search regardless of uppercase/lowercase. Currently only lowercase works as .csv is in lowercase
        //2: feedback for user if their search did not return results
        int index = 0;
        for(String s: cityNames){
            if (s.startsWith(citySearch)) {
                Log.d("Match",s);

                int newOffset = Integer.parseInt(list.get(index)[1]);

                CityIndex newMatch = new CityIndex(s, newOffset, list.get(index)[2]);

                //put the matches in the cityMatch adapter
                cityMatch.add(newMatch);

                //tell the adapter theres updated data and refresh the UI
                adapter.notifyDataSetChanged();

            }
            index++;
        }




    }

    //Called when the user selects the button next to the city of their choice
    public void addCity(View view){

        //determine which search result was selected
        View v = (View) view.getParent();
        TextView taskTextView = (TextView) v.findViewById(R.id.city_name);
        String inputInfo = taskTextView.getText().toString();

        //because of the way I had to output the class, parse the string based on [.]
        String delims = "[.]";
        String[] cityInfo = inputInfo.split(delims);
        String cityName = cityInfo[0];
        String zone = cityInfo[2];
        int gmtOffset = Integer.parseInt(cityInfo[1]);
        //DateFormat time = DateFormat.getTimeInstance();

        //for my debugging
        Log.d("Added City", cityName);

        //Put the city's info into an intent to be passed back to the main activity
        Intent intent = new Intent();
        intent.putExtra("cityName", cityName);
        intent.putExtra("gmtOffset", gmtOffset);
        intent.putExtra("zone",zone);
        //intent.putExtra("cityTime", time.format(System.currentTimeMillis()));

        setResult(RESULT_OK, intent);

        finish();
    }

    //Method to load in the CSV file, help from http://www.theappguruz.com/tutorial/parse-csv-file-in-android-example-sample-code/
    public List<String[]> loadCSV(){
        String next[] = {};
        List<String[]> list = new ArrayList<String[]>();

        try {
            CSVReader reader = new CSVReader(new InputStreamReader(getAssets().open("citydata.csv")));
            while(true) {
                next = reader.readNext();
                if(next != null) {
                    list.add(next);
                } else {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }





}
