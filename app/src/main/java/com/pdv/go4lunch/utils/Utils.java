package com.pdv.go4lunch.utils;

import android.location.Location;

import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.pdv.go4lunch.Model.GooglePlacesApiModel.Results;
import com.pdv.go4lunch.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public abstract class Utils {

    @Nullable
    public static FirebaseUser getCurrentUser(){ return FirebaseAuth.getInstance().getCurrentUser();}

    public static Boolean isCurrentUserLogged(){ return (getCurrentUser() != null); }

    public static int getDistanceBetweenLocation(Location location1, Results place){
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
            String result = String.valueOf(R.string.opening_time);
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
            return result;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void sortByDistance(List<Results> results) {
        Collections.sort(results, new Comparator<Results>() {
            @Override
            public int compare(Results o1, Results o2) {
                return o1.getDistance() - o2.getDistance();
            }
        });
    }

    public static int getNumberOfStars(int nbrOfUsers, int nbrOfLikes){
        int percent = (nbrOfLikes * 100) / nbrOfUsers;
        if (percent < 30){
            return 1;
        }else if (percent >= 30 && percent <= 60){
            return 2;
        }else if (percent > 60){
            return 3;
        }
        return 0;
    }
}
