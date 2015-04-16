
//from http://android-er.blogspot.com/2011/07/display-flickr-photos-in-gallery-using.html
//tried to learn from the example but ran into network error described in scroll_list.java comments
package rcorona.worldclock;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class AndroidFlickrActivity extends Activity {

/*
* FlickrQuery = FlickrQuery_url
* + FlickrQuery_per_page
* + FlickrQuery_nojsoncallback
* + FlickrQuery_format
* + FlickrQuery_tag + q
* + FlickrQuery_key + FlickrApiKey
*/

    String FlickrQuery_url = "http://api.flickr.com/services/rest/?method=flickr.photos.search";
    String FlickrQuery_per_page = "&per_page=1";
    String FlickrQuery_nojsoncallback = "&nojsoncallback=1";
    String FlickrQuery_format = "&format=json";
    String FlickrQuery_tag = "&tags=";
    String FlickrQuery_key = "&api_key=";

    // Apply your Flickr API:
// www.flickr.com/services/apps/create/apply/?
    String FlickrApiKey = "45ec5d09b32ac3951539d14e93939057";

    EditText searchText;
    Button searchButton;
    TextView textQueryResult;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.flickr_layot);

        searchText = (EditText) findViewById(R.id.searchtext);
        searchButton = (Button) findViewById(R.id.searchbutton);
        textQueryResult = (TextView) findViewById(R.id.queryresult);

        searchButton.setOnClickListener(searchButtonOnClickListener);
    }

    private Button.OnClickListener searchButtonOnClickListener
            = new Button.OnClickListener() {

        public void onClick(View arg0) {
            // TODO Auto-generated method stub
            String searchQ = searchText.getText().toString();
            String searchResult = QueryFlickr(searchQ);
            textQueryResult.setText(searchResult);
        }
    };


    private String QueryFlickr(String q) {


        String qResult = null;

        String qString =
                FlickrQuery_url
                        + FlickrQuery_per_page
                        + FlickrQuery_nojsoncallback
                        + FlickrQuery_format
                        + FlickrQuery_tag + q
                        + FlickrQuery_key + FlickrApiKey;

        //syncTaskFlickr flickr = new AsyncTaskFlickr();
        //flickr.execute(qString);

        HttpClient httpClient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(qString);

        try {
            HttpEntity httpEntity = httpClient.execute(httpGet).getEntity();

            if (httpEntity != null) {
                InputStream inputStream = httpEntity.getContent();
                Reader in = new InputStreamReader(inputStream);
                BufferedReader bufferedreader = new BufferedReader(in);
                StringBuilder stringBuilder = new StringBuilder();

                String stringReadLine = null;

                while ((stringReadLine = bufferedreader.readLine()) != null) {
                    stringBuilder.append(stringReadLine + "\n");
                }

                qResult = stringBuilder.toString();

            }


        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return qResult;
    }
}






    /*
    private class AsyncTaskFlickr extends AsyncTask<String, String, String> {

        private String result;
        @Override
        protected String doInBackground(String... params) {
            publishProgress("Sleeping..."); // Calls onProgressUpdate()



            HttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(params[0]);

            try {
                // Do your long operations here and return the result

                HttpEntity httpEntity = httpClient.execute(httpGet).getEntity();

                if (httpEntity != null) {
                    InputStream inputStream = httpEntity.getContent();
                    Reader in = new InputStreamReader(inputStream);
                    BufferedReader bufferedreader = new BufferedReader(in);
                    StringBuilder stringBuilder = new StringBuilder();

                    String stringReadLine = null;

                    while ((stringReadLine = bufferedreader.readLine()) != null) {
                        stringBuilder.append(stringReadLine + "\n");
                    }

                    result = stringBuilder.toString();
                    return result;

                }
                } catch (InterruptedException e) {
                e.printStackTrace();
                result = e.getMessage();
                } catch (Exception e) {
                e.printStackTrace();
                result = e.getMessage();
                }
                return result;
        }




    }
}

*/



