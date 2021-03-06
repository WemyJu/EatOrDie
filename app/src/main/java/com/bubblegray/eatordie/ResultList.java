package com.bubblegray.eatordie;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import java.util.ArrayList;
import java.util.HashMap;

public class ResultList extends ActionBarActivity {

    private String location = "25.043800,121.557223";
    private String radius = "500";
    private String type = "food";
    private String key = "AIzaSyBbgk2WwE9CC8JIHlJ4_NtLXOTIu7foOVE";
    private String url;
    private ListView listView;
    private SimpleAdapter listAdapter;
    private ArrayList<String> arrayList;
    private ArrayList<String> storeGps;
    private ArrayList<String> storeAddress;
    private ArrayList<HashMap<String,String>> list2;
    private ResultList myself;
    private int countNoResult;
    private  int numOfKeyWord;
    private double lat_b,lng_b;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_list);

        listView=(ListView)findViewById(R.id.listView);
        myself=this;

        Intent it = getIntent();
        numOfKeyWord = it.getIntExtra("numOfKeyWord",0);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);

        LocationManager locmgr = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        String provider = locmgr.getBestProvider(new Criteria(), true);
        if(provider != null) {
            Location l_net = locmgr.getLastKnownLocation(provider);
            lat_b=l_net.getLatitude();
            lng_b=l_net.getLongitude();
            location = String.format("%.6f,%.6f", lat_b, lng_b);
            Log.e("position", location);
        }

        list2 = new ArrayList<HashMap<String,String>>();
        arrayList = new ArrayList<String>();
        storeGps = new ArrayList<String>();
        storeAddress = new ArrayList<String>();
        listAdapter = new SimpleAdapter(ResultList.this,list2,android.R.layout.simple_list_item_2,new String[] { "TITLE", "SUBTITLE" },new int[] { android.R.id.text1, android.R.id.text2 });

        listView.setAdapter(listAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent it = new Intent(ResultList.this, GoogleMap.class);
                it.putExtra("Name",arrayList.get((int)id));
                it.putExtra("GPS",storeGps.get((int)id));
                it.putExtra("Address",storeAddress.get((int)id));
                startActivity(it);
            }
        });
        if(numOfKeyWord == 0)
        {
            Intent it2=new Intent(this,Die.class);
            startActivity(it2);
        }
        else
        {
            String keyWord;
            countNoResult=0;
            for(int i=0; i<numOfKeyWord; ++i)
            {
                //keyWord+=i==0?it.getStringExtra(""+i):","+it.getStringExtra(""+i);
                keyWord=it.getStringExtra(""+i);
                url = "https://maps.googleapis.com/maps/api/place/search/json?location=" + location + "&radius=" + radius + "&types=" + type + "&sensor=false&key=" + key+"&keyword="+keyWord;
                loadAPI(url,keyWord);
            }
        }

    }
    public void loadAPI(String address, final String keyWord)
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

                            isOver=true;
                            countNoResult++;
                            if(countNoResult==numOfKeyWord)
                            {
                                Intent it2=new Intent(myself,Die.class);
                                startActivity(it2);
                            }
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
                            String tmpName,tmpLat,tmpLng,tmpGps,tmpAddress;
                            len = tmp.size();
                            for (int i = 0; i < len; ++i) {
                                tmpName=tmp.get(i).getAsJsonObject().get("name").getAsString();
                                arrayList.add(tmpName);

                                tmpLat=tmp.get(i).getAsJsonObject().get("geometry").getAsJsonObject().get("location").getAsJsonObject().get("lat").getAsString();
                                tmpLng=tmp.get(i).getAsJsonObject().get("geometry").getAsJsonObject().get("location").getAsJsonObject().get("lng").getAsString();
                                tmpGps=tmpLat+","+tmpLng;

                                storeGps.add(tmpGps);
                                tmpAddress=tmp.get(i).getAsJsonObject().get("vicinity").getAsString();
                                storeAddress.add(tmpAddress);
                                HashMap<String,String> item = new HashMap<String,String>();

                                item.put("TITLE",tmpName);
                                item.put("SUBTITLE",countDis(tmpLat,tmpLng)+"\n"+tmpAddress);
                                list2.add(item);
                            }


                        }
                        if(!isOver)
                        {
                            url = "https://maps.googleapis.com/maps/api/place/search/json?location=" + location + "&radius=" + radius + "&types=" + type + "&sensor=false&key=" + key+"&keyword="+keyWord+"&pagetoken="+nextPage;
                            loadAPI(url,keyWord);
                        }
                        else
                            listAdapter.notifyDataSetChanged();
                    }
                });
    }

    private final double EARTH_RADIUS = 6378137.0;
    private String countDis(String lat,String lng)
    {
        String dis="距離 ";
        double lat_a=Double.parseDouble(lat);
        double lng_a=Double.parseDouble(lng);

        double radLat1 = (lat_a * Math.PI / 180.0);
        double radLat2 = (lat_b * Math.PI / 180.0);
        double a = radLat1 - radLat2;
        double b = (lng_a - lng_b) * Math.PI / 180.0;
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
                + Math.cos(radLat1) * Math.cos(radLat2)
                * Math.pow(Math.sin(b / 2), 2)));
        s = s * EARTH_RADIUS;
        s = Math.round(s * 10000) / 10000.0;

        dis+=String.format("%.3f", s);

        dis+=" 公尺";
        return dis;
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            Intent it = new Intent(this, MainActivity.class);
            startActivity(it);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }
}
