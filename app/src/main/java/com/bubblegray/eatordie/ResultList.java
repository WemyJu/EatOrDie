package com.bubblegray.eatordie;

import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class ResultList extends ActionBarActivity {

    private String location = "25.043800,121.557223";
    private String radius = "500";
    private String type = "food";
    private String key = "AIzaSyBbgk2WwE9CC8JIHlJ4_NtLXOTIu7foOVE";
    private String keyWord="";
    private String url;
    private ListView listView;
    private ArrayAdapter<String> listAdapter;
    private ArrayList<String> arrayList;
    private ResultList myself;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_list);

        listView=(ListView)findViewById(R.id.listView);
        myself=this;

        Intent it = getIntent();
        int numOfKeyWord = it.getIntExtra("numOfKeyWord",0);
        if(numOfKeyWord == 0)
        {
            Intent it2=new Intent(this,Die.class);
            startActivity(it2);
        }
        else
        {
            for(int i=0; i<numOfKeyWord; ++i)
            {
                keyWord+=i==0?it.getStringExtra(""+i):","+it.getStringExtra(""+i);
            }
        }

        LocationManager locmgr = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        String provider = locmgr.getBestProvider(new Criteria(), true);
        if(provider != null) {
            Location l_net = locmgr.getLastKnownLocation(provider);
            location = String.format("%.6f,%.6f", l_net.getLatitude(), l_net.getLongitude());
            Log.e("position", location);
        }

        url = "https://maps.googleapis.com/maps/api/place/search/json?location=" + location + "&radius=" + radius + "&types=" + type + "&sensor=false&key=" + key+"&keyword="+keyWord;

        arrayList = new ArrayList<String>();
        listAdapter = new ArrayAdapter<String>(ResultList.this,android.R.layout.simple_list_item_1,arrayList);
        listView.setAdapter(listAdapter);

        loadAPI(url);
    }
    public void loadAPI(String address)
    {
        Log.d("Eat or die","Address: " + address);
        Ion.with(ResultList.this)
                .load(address)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        Log.d("API Test", "Test Result" + result.toString());
                        String nextPage="";
                        boolean isOver=false;
                        if("ZERO_RESULTS".equals(result.get("status").getAsString()))
                        {
                            Intent it2=new Intent(myself,Die.class);
                            startActivity(it2);
                            isOver=true;
                        }
                        if(result.has("next_page_token"))
                        {
                            nextPage=result.get("next_page_token").getAsString();
                            isOver=false;
                        }
                        else
                        {
                            isOver=true;
                        }
                        JsonArray tmp = result.getAsJsonArray("results");

                        if(tmp!=null) {
                            int len;
                            len = tmp.size();
                            for (int i = 0; i < len; ++i) {
                                arrayList.add(tmp.get(i).getAsJsonObject().get("name").getAsString());
                            }


                        }
                        if(!isOver)
                        {
                            url = "https://maps.googleapis.com/maps/api/place/search/json?location=" + location + "&radius=" + radius + "&types=" + type + "&sensor=false&key=" + key+"&keyword="+keyWord+"&pagetoken="+nextPage;
                            loadAPI(url);
                        }
                        else
                            // UIHr.post(refreshUI);
                            listAdapter.notifyDataSetChanged();
                    }
                });
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
