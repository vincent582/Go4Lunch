package com.pdv.go4lunch;

import android.app.Application;
import android.location.Location;

public class Go4LunchApplication extends Application {

    private static Go4LunchApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static Go4LunchApplication getInstance() {
        return instance;
    }

    public Location myLocation;

    public Location getMyLocation() {
        return myLocation;
    }

    public void setMyLocation(Location myLocation) {
        this.myLocation = myLocation;
    }
}
