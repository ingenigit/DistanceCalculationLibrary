package com.google.distance.calculation.finddistance;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;

public class getAddLL {
    public static LatLng GetAddressLL(Context context, String fulladdress) {
        Geocoder coder = new Geocoder(context);
        List<Address> address;
        LatLng p1 = null;
        try{
            address = coder.getFromLocationName(fulladdress,5);
            if (address == null){
                return null;
            }
            Address location = address.get(0);
            location.getLatitude();
            location.getLongitude();
            p1 = new LatLng((double)(location.getLatitude()), (double)(location.getLongitude()));
            return p1;
        }catch(IOException e){
            e.printStackTrace();
        }
        return null;
    }
}
