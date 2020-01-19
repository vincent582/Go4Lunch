package com.pdv.go4lunch.utils;

import android.location.Location;
import android.util.Log;

public abstract class Utils {

    public static String getDistanceBetweenLocation(Location location1, Location location2){
        float floatDistance = location1.distanceTo(location2);
        int distance = Math.round(floatDistance);
        Log.e("TAG", "getDistanceBetweenLocation: "+distance+"m");
        return distance+"m";
    }
}
