package com.google.distance.calculation.finddistance;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

public class TwoLocation implements Runnable{

    public Context context;
    public String firstGeo, secondGeo;
    public String firstAdd, secondAdd;
    public getDistance getDistance;
    String URI, APIKEY;
    String sensor, mode;
    String finalFirst, finalSecond;

    public TwoLocation(Context context, String firstGeo, String secondGeo, String firstAdd, String secondAdd, String sencor, String apikey, String uri, String mode
            , getDistance getDistance) {
        this.context = context;
        this.firstGeo = firstGeo;
        this.secondGeo = secondGeo;
        this.firstAdd = firstAdd;
        this.secondAdd = secondAdd;
        this.sensor = sencor;
        this.mode = mode;
        this.APIKEY = apikey;
        this.URI = uri;
        this.getDistance = getDistance;
    }

    @Override
    public void run() {
        if (firstGeo.isEmpty()){
            LatLng latLng = getAddLL.GetAddressLL(context, firstAdd);
            finalFirst = ((latLng != null ? latLng.latitude + ","+ latLng.longitude : "0"));
        }
        if (secondGeo.isEmpty()){
            LatLng latLng = getAddLL.GetAddressLL(context,secondAdd);
            finalSecond = ((latLng != null ? latLng.latitude + ","+ latLng.longitude : "0"));
        }
        if (firstAdd.isEmpty()){
            finalFirst = firstGeo;
        }
        if (secondAdd.isEmpty()){
            finalSecond = secondGeo;
        }
        System.out.println(finalFirst + "sdsdsdsds" + finalSecond);
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        String url = Uri.parse(URI)
                .buildUpon()
                .appendQueryParameter("origins", finalFirst)
                .appendQueryParameter("destinations", finalSecond)
                .appendQueryParameter("sensor", sensor)
                .appendQueryParameter("mode", mode)
                .appendQueryParameter("key", APIKEY)
                .toString();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String status = response.getString("status");
                    if (status.equals("OK")) {
                        JSONArray jsonArray = response.getJSONArray("rows");
                        JSONObject jsonObjectDistance = jsonArray
                                .getJSONObject(0)
                                .getJSONArray("elements")
                                .getJSONObject(0)
                                .getJSONObject("distance");
                        JSONObject jsonObjectTime = jsonArray
                                .getJSONObject(0)
                                .getJSONArray("elements")
                                .getJSONObject(0)
                                .getJSONObject("duration");
                        String tDistance = jsonObjectDistance.getString("text").toString();
                        String tTime = jsonObjectTime.getString("text").toString();
                        System.out.println(tDistance + "sdsdsdsds" + tTime);
                        getDistance.GetTotalDistance(tDistance, 0.00, 0.00, tTime, "");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        requestQueue.add(jsonObjectRequest);
    }
}
