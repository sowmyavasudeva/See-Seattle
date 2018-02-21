package com.example.sowmya.weatherapplication;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import org.json.JSONException;
import org.json.JSONObject;


public class WeatherActivity extends AppCompatActivity{


    // we"ll make HTTP request to this URL to retrieve weather conditions
    String weatherWebserviceURL = "http://api.openweathermap.org/data/2.5/weather?q=seattle,usa&appid=06325b4431f5cc9197ce91ac380affbe&units=metric";
    //the loading Dialog
    ProgressDialog pDialog;
    // Textview to show temperature and description
    TextView temperature, description;
    // background image
    ImageView weatherBackground;
    // JSON object that contains weather information
    JSONObject jsonObj;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);


        //link graphical items to variables
        temperature = (TextView) findViewById(R.id.temperature);
        description = (TextView) findViewById(R.id.description);
        weatherBackground = (ImageView) findViewById(R.id.weatherbackground);

        // prepare and show the loading Dialog
        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Please wait while retrieving the weather condition ...");
        pDialog.setCancelable(false);
        pDialog.show();

        // make HTTP request to retrieve the weather
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, weatherWebserviceURL, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {
                    // Parsing json object response
                    // response will be a json object


                    jsonObj = (JSONObject) response.getJSONArray("weather").get(0);
                    // display weather description into the "description textview"
                    description.setText(jsonObj.getString("description"));
                    // display the temperature
                    temperature.setText(response.getJSONObject("main").getString("temp") + " °C");

                    String backgroundImage = "";

                    //choose the image to set as background according to weather condition
                    if (jsonObj.getString("main").equals("Clouds")) {
                        backgroundImage = "https://marwendoukh.files.wordpress.com/2017/01/clouds-wallpaper2.jpg";
                    } else if (jsonObj.getString("main").equals("Rain")) {
                        backgroundImage = "https://marwendoukh.files.wordpress.com/2017/01/rainy-wallpaper1.jpg";
                    } else if (jsonObj.getString("main").equals("Snow")) {
                        backgroundImage = "https://marwendoukh.files.wordpress.com/2017/01/snow-wallpaper1.jpg";
                    }

                    // load image from link and display it on background
                    // We'll use the Glide library
                    Glide
                            .with(getApplicationContext())
                            .load(backgroundImage)
                            .centerCrop()
                            .crossFade()
                            .listener(new RequestListener<String, GlideDrawable>() {
                                @Override
                                public boolean onException(Exception e, String model, Target target, boolean isFirstResource) {
                                    System.out.println(e.toString());
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(GlideDrawable resource, String model, Target target, boolean isFromMemoryCache, boolean isFirstResource) {
                                    return false;
                                }
                            })
                            .into(weatherBackground);

                    // hide the loading Dialog
                    pDialog.dismiss();


                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Error , try again ! ", Toast.LENGTH_LONG).show();
                    pDialog.dismiss();

                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("tag", "Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(), "Error while loading ... ", Toast.LENGTH_SHORT).show();
                // hide the progress dialog
                pDialog.dismiss();
            }
        });

        // Adding request to request queue
        AppController.getInstance(this).addToRequestQueue(jsonObjReq);



    }

}
