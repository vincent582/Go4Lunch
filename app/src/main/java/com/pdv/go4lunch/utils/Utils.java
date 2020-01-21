package com.pdv.go4lunch.utils;

import android.location.Location;
import android.util.Log;

import com.pdv.go4lunch.Model.Place.Result;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public abstract class Utils {

    public static int getDistanceBetweenLocation(Location location1, Result place){
        Location location = new Location("Location");
        location.setLatitude(place.getGeometry().getLocation().getLat());
        location.setLongitude(place.getGeometry().getLocation().getLng());

        float floatDistance = location1.distanceTo(location);
        int distance = Math.round(floatDistance);
        return distance;
    }

    public static String formatTimeFromOpenningHours(String time) {
        Calendar calendar = Calendar.getInstance();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(time);
        stringBuilder.insert(2,":");

        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        try {
            Date date = format.parse(String.valueOf(stringBuilder));
            calendar.setTime(date);
            String result = "Open until ";
            if(calendar.get(Calendar.HOUR) == 0){
                result += "00";
            }else{
                result += calendar.get(Calendar.HOUR);
            }
            if(calendar.get(Calendar.MINUTE) > 0){
                result += "." + calendar.get(Calendar.MINUTE);
            }
            if(calendar.get(Calendar.AM_PM)==0){
                result = result +"am";
            }
            else{
                result = result +"pm";
            }
            Log.e("TAG", "updateWithPlaces: " + result);
            return result;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void sortByDistance(List<Result> results) {
        Collections.sort(results, new Comparator<Result>() {
            @Override
            public int compare(Result o1, Result o2) {
                return o1.getDistance() - o2.getDistance();
            }
        });
    }
}
