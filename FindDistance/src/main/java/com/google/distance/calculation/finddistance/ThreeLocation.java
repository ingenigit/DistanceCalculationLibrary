package com.google.distance.calculation.finddistance;

import android.content.Context;
import android.net.Uri;
import android.widget.Toast;

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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ThreeLocation implements Runnable{
    Context mContext;
    public String firstGeo, secondGeo, thirdGeo;
    public String firstAdd, secondAdd, thirdAdd;
    String URI, APIKEY;
    String sensor, mode;
    String finalFirst, finalSecond, finalThird;
    private getDistance getDistance;

    public ThreeLocation(Context context, String firstGeo, String secondGeo, String thirdGeo, String firstAdd, String secondAdd, String thirdAdd,
                         String uri, String sencor, String mode, String apikey, getDistance getDistance) {
        this.mContext = context;
        this.firstGeo = firstGeo;
        this.secondGeo = secondGeo;
        this.thirdGeo = thirdGeo;
        this.firstAdd = firstAdd;
        this.secondAdd = secondAdd;
        this.thirdAdd = thirdAdd;
        this.URI = uri;
        this.sensor = sencor;
        this.mode = mode;
        this.APIKEY = apikey;
        this.getDistance = getDistance;
    }

    @Override
    public void run() {
        if (firstGeo.isEmpty()){
            LatLng latLng = getAddLL.GetAddressLL(mContext, firstAdd);
            finalFirst = ((latLng != null ? latLng.latitude + ","+ latLng.longitude : "0"));
        }else if (secondGeo.isEmpty()){
            LatLng latLng = getAddLL.GetAddressLL(mContext,secondAdd);
            finalSecond = ((latLng != null ? latLng.latitude + ","+ latLng.longitude : "0"));
        }else if (thirdGeo.isEmpty()) {
            LatLng latLng = getAddLL.GetAddressLL(mContext, thirdAdd);
            finalThird = ((latLng != null ? latLng.latitude + "," + latLng.longitude : "0"));
        }else if (firstAdd.isEmpty()){
            finalFirst = firstGeo;
        }else if (secondAdd.isEmpty()){
            finalSecond = secondGeo;
        }else if (thirdAdd.isEmpty()){
            finalThird = secondGeo;
        }

        String url = Uri.parse(URI)
                .buildUpon()
                .appendQueryParameter("origins", finalFirst+"|"+finalSecond)
                .appendQueryParameter("destinations", finalSecond+"|"+finalThird)
                .appendQueryParameter("sensor", sensor)
                .appendQueryParameter("mode", mode)
                .appendQueryParameter("key", APIKEY)
                .toString();
        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String status = response.getString("status");
                    if (status.equals("OK")) {
                        JSONArray jsonArray = response.getJSONArray("rows");
                        //for 0 index distance only 1 to 2
                        JSONObject jsonObject = jsonArray
                                .getJSONObject(0)
                                .getJSONArray("elements")
                                .getJSONObject(0)
                                .getJSONObject("distance");
                        JSONObject jsonObjectTime = jsonArray
                                .getJSONObject(0)
                                .getJSONArray("elements")
                                .getJSONObject(0)
                                .getJSONObject("duration");
                        //for 1 index distance only 2 to 3
                        JSONObject jsonObject1 = jsonArray
                                .getJSONObject(1)
                                .getJSONArray("elements")
                                .getJSONObject(1)
                                .getJSONObject("distance");
                        JSONObject jsonObjectTime1 = jsonArray
                                .getJSONObject(1)
                                .getJSONArray("elements")
                                .getJSONObject(1)
                                .getJSONObject("duration");
                        String firstDistance = jsonObject.getString("text").toString();
                        String secondDistance = jsonObject1.getString("text").toString();
                        String firstTime = jsonObjectTime.getString("text").toString();
                        String secondTime = jsonObjectTime1.getString("text").toString();
                        String[] splited1 = firstDistance.split(" ", 2);
                        String[] splited2 = secondDistance.split(" " , 2);
                        System.out.println(splited2[0].length());
                        String sp1 = "", sp2 = "";
                        sp1 = splited1[0];
                        sp2 = splited2[0];
                        if (splited1[0].length() > 3)
                            sp1 = splited1[0].replace(",", "");
                        if(splited2[0].length() > 3)
                            sp2 = splited2[0].replace(",", "");

                        if (splited1[1].equals("m")) {
                            float i = Float.parseFloat(splited1[0])/1000;
                            sp1 = String.valueOf(i);
                        }
                        if(splited2[1].equals("m")){
                            float j = Float.parseFloat(splited2[0])/1000;
                            sp2 = String.valueOf(j);
                        }
                        System.out.println(sp1 + sp2 + "KKML" + splited1[0] + splited1[1] +"fghjkl"+ splited2[0] + splited2[1]);
                        String distanceValue = (String.valueOf(Double.parseDouble(sp1)+Double.parseDouble(sp2)));
                        //setValue
                        DateTimeFormatter dtf = null;
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                            dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
                            LocalDateTime now = LocalDateTime.now();
                            System.out.println(dtf.format(now));
                        }

                        System.out.println("Threas" + distanceValue);
                        getDistance.GetTotalDistance(distanceValue,Double.parseDouble(sp1),Double.parseDouble(sp2), firstTime, secondTime);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    getDistance.GetTotalDistance("0.00",0.00,0.00, "","");
                    Toast.makeText(mContext, "Distance cannot be calculate", Toast.LENGTH_SHORT).show();
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
