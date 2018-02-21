package com.example.sowmya.foodapplication;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class FoodActivity extends AppCompatActivity {

    private String TAG = FoodActivity.class.getSimpleName();

    private ProgressDialog pDialog;
    private ListView lv;

    // URL to get near-by restaurants Seattle JSON
    private static String url = "https://developers.zomato.com/api/v2.1/geocode?lat=47.606209&lon=-122.332069&apikey=97bbb59a1a5ccc65f8a7ec692d213cd3";

    ArrayList<HashMap<String, String>> restaurantList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food);

        restaurantList = new ArrayList<>();
        lv = (ListView) findViewById(R.id.list);

        new GetRestaurants().execute();
    }


    private class GetRestaurants extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(FoodActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url);

            Log.e(TAG, "Response from url: " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    // Getting JSON Array node
                    JSONArray restaurants = jsonObj.getJSONArray("nearby_restaurants");

                    // looping through All Nearby Restaurants
                    for (int i = 0; i < restaurants.length(); i++) {
                        JSONObject c = restaurants.getJSONObject(i);

                        // Getting JSON object of all restaurants
                        JSONObject getrestaurantdetail = c.getJSONObject("restaurant");
                        String id = getrestaurantdetail.getString("id");
                        String name = getrestaurantdetail.getString("name");

                        // Getting JSON object of the location of a restaurant
                        JSONObject getLocation = getrestaurantdetail.getJSONObject("location");
                        String address = getLocation.getString("address");
                        String locality = getLocation.getString("locality");




                        // tmp hash map for single restaurant
                        HashMap<String, String> restaurant = new HashMap<>();

                        // adding each child node to HashMap key => value
                        restaurant.put("id", id);
                        restaurant.put("name", name);
                        restaurant.put("address", address);
                        restaurant.put("locality", locality);

                        // adding contact to contact list
                        restaurantList.add(restaurant);
                    }
                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    });

                }
            } else {
                Log.e(TAG, "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();
            /**
             * Updating parsed JSON data into ListView
             * */
            ListAdapter adapter = new SimpleAdapter(
                    FoodActivity.this, restaurantList,
                    R.layout.list_item, new String[]{"name", "address",
                    "locality"}, new int[]{R.id.name,
                    R.id.address, R.id.locality});

            lv.setAdapter(adapter);
        }
    }
}
