package com.bubblegray.eatordie;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.Object;

public class ResultList extends ActionBarActivity {

    private String location = "24.790864,121.004105";
    private String radius = "500";
    private String type = "food";
    private String key = "AIzaSyBbgk2WwE9CC8JIHlJ4_NtLXOTIu7foOVE";
    private String url = "https://maps.googleapis.com/maps/api/place/search/json?location=" + location + "&radius=" + radius + "&types=" + type + "&sensor=false&key=" + key;
    private Handler UIHr;
    private GetData getData;
    private ListView listView;
    private String[] show_text;
    private ArrayAdapter<String> listAdapter;
    private ResultList myself;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_list);

        listView=(ListView)findViewById(R.id.listView);

        UIHr = new Handler();
        getData = new GetData();
        Thread getDataThread = new Thread(getData);
        getDataThread.start();
        myself=this;

        Intent it = getIntent();
        int numOfQuestions = it.getIntExtra("numOfQuestions", 0);
        for(int i=0; i<numOfQuestions; i++){
            type+="&"+it.getStringExtra(""+i);
        }
    }

    public Runnable refreshUI = new Runnable() {
        public void run() {
            listAdapter = new ArrayAdapter<String>(myself,android.R.layout.simple_list_item_1,getData.getResult());
            listView.setAdapter(listAdapter);
        }
    };

    public class GetData implements Runnable {

        private String[] result;
        private int len;
        @Override
        public void run() {
            String jsonString = getJSON(url);
            JSONArray tmp = parseJSON(jsonString);
            try {
                len=tmp.length();
                result=new String[len];
                for(int i=0;i<len;++i)
                {
                    result[i] = tmp.getJSONObject(i).getString("name");
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            UIHr.post(refreshUI);
        }

        public String[] getResult() {
            return result;
        }

        public String getJSON(String address) {
            StringBuilder builder = new StringBuilder();
            HttpClient client = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(address);
            try {
                HttpResponse response = client.execute(httpGet);
                StatusLine statusLine = response.getStatusLine();
                int statusCode = statusLine.getStatusCode();
                if (statusCode == 200) {
                    HttpEntity entity = response.getEntity();
                    InputStream content = entity.getContent();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        builder.append(line);
                    }
                    content.close();
                }
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return builder.toString();
        }

        /**
         * https://maps.googleapis.com/maps/api/place/search/json?location=24.790864,121.004105&radius=500&types=food&sensor=false&key=AIzaSyBbgk2WwE9CC8JIHlJ4_NtLXOTIu7foOVE
         * {
         * "geometry" : {
         * "location" : {
         * "lat" : 24.791365,
         * "lng" : 121.004452
         * }
         * },
         * "icon" : "http://maps.gstatic.com/mapfiles/place_api/icons/restaurant-71.png",
         * "id" : "3d920b8a494ad3fa6c506d0f28ac3bb868acd98c",
         * "name" : "原味燉品屋",
         * "opening_hours" : {
         * "open_now" : true,
         * "weekday_text" : []
         * },
         * "photos" : [
         * {
         * "height" : 488,
         * "html_attributions" : [ "由 Google 使用者上載" ],
         * "photo_reference" : "CrQBqgAAAHDkBUZmQ9hGTWeoHF_qEUevSkpTjDawrrRu1rpy-wofMzfppXyEJ4Br6Tgkh5GdY6MpNpWsvMcrlXgaMZSAaBmqqRAncVYsPlZ73Q1sBsuaFEis95oo2MTfowFd07yr9XUwS2OPN2GFhvZ8TYukQeo0fTHUXEsduybbtDYLa0ss7ztWgAphHJSmsHLIcHhUgzOnRoeVR-v1GHZfUm8eLFBMcAk612EcQfauSEaxlcYaEhBXa067mCK_bCXgNP07IP3lGhTR6O3GDg0r9R5nSxKtF7scp3kr8w",
         * "width" : 816
         * }
         * ],
         * "place_id" : "ChIJcZgpjxM2aDQRCCOCg790EaU",
         * "rating" : 2.8,
         * "reference" : "CoQBcQAAAEKUx5awgCm2Br8MXINVp7O_5A2nAzRlN8kwVXPOeSAGIrHqDfzkxb-ig2XjTVdK54PF96WeCVoYmPddCMazkfcjiKRCU7D9s2K2p3rVmM7oSck72ygQIgqppcdZKN-2DrCAbv8qixg2ZbdbXbkvO0r4RecfwFIbAXZbl4iNCtWiEhAKP4aZKs9mwqfFO-PUJ3YoGhRlE-cXfpyi714FAm9Gz6rslhPBuA",
         * "scope" : "GOOGLE",
         * "types" : [ "restaurant", "food", "establishment" ],
         * "vicinity" : "新竹市光復路二段14號(交流道旁)"
         * }
         *
         * @param jsonString the json stgring from api
         * @return
         */
        private JSONArray parseJSON(String jsonString) {
            try {
                JSONObject jsonObject = new JSONObject(jsonString);
                String status = jsonObject.getString("status");
                if (!"OK".equals(status)) {
                    throw new IllegalStateException();
                }
                JSONArray results = jsonObject.getJSONArray("results");

                return results;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_result_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
