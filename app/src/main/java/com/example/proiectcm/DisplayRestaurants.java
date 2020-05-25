package com.example.proiectcm;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class DisplayRestaurants extends AppCompatActivity {
    private ListView mListView;
    private static final String API_KEY = "AIzaSyDmBi1CP19lMBjXGVogVYBnWVZF9Y2Cxcs";

    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";

    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String TYPE_DETAILS = "/details";
    private static final String TYPE_SEARCH = "/nearbysearch";
    private static final String OUT_JSON = "/json?";
    private static final String LOG_TAG = "ListRest";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_restaurants);
        Intent intent = getIntent();
        String longitude = intent.getStringExtra("long");
        String latitude = intent.getStringExtra("lat");

        //permitem ca Api-ul sa lucreze pe thred-ul principal
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Double lng = Double.parseDouble(longitude);
        Double lat = Double.parseDouble(latitude);
        int radius = 1000; //distanta de la telefon la restaurant (in metri)

        ArrayList<Place> list = search(lat, lng, radius);

        if (list != null)
        {
            mListView = (ListView) findViewById(R.id.listView);
            ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_expandable_list_item_1, list);
            mListView.setAdapter(adapter);
        }
    }

    public static ArrayList<Place> search(double lat, double lng, int radius) {
        ArrayList<Place> resultList = null;

        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        try {
            StringBuilder sb = new StringBuilder(PLACES_API_BASE);
            sb.append(TYPE_SEARCH);
            sb.append(OUT_JSON);
            sb.append("location=" + String.valueOf(lat) + "," + String.valueOf(lng));
            sb.append("&radius=" + String.valueOf(radius));
           sb.append("&type=restaurant");
           sb.append("&key=" + API_KEY);

            URL url = new URL(sb.toString());
            conn = (HttpURLConnection) url.openConnection();
           InputStreamReader in = new InputStreamReader(conn.getInputStream());

           int read;
           char[] buff = new char[1024];
           while ((read = in.read(buff)) != -1) {
               jsonResults.append(buff, 0, read);
            }
       } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Eroare de procesare a URL-ului", e);
           return resultList;
       } catch (IOException e) {
            Log.e(LOG_TAG, "Eroare de conectare cu API-ul", e);
            return resultList;
       } finally {
           if (conn != null) {
               conn.disconnect();
            }
       }

       try {
           // Cream un nou obiect de tip JSON cu datele obtinute
            JSONObject jsonObj = new JSONObject(jsonResults.toString());
            JSONArray predsJsonArray = jsonObj.getJSONArray("results");

            // Extract descrierea din rezultate
           resultList = new ArrayList<Place>(predsJsonArray.length());
            for (int i = 0; i < predsJsonArray.length(); i++) {
                Place place = new Place();
                place.reference = predsJsonArray.getJSONObject(i).getString("reference");
                place.name = predsJsonArray.getJSONObject(i).getString("name");
                resultList.add(place);
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Eroare de procesare a rezultatelor Json-ului", e);
        }

        return resultList;
    }



    public static class Place {
        private String reference;
        private String name;

       public Place(){
            super();
        }
        @Override
       public String toString(){
            return this.name; //returanam numele fiecarui restaurant
        }
    }
}

