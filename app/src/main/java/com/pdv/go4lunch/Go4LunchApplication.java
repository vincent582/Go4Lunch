package com.pdv.go4lunch;

import android.app.Application;
import android.location.Location;

public class Go4LunchApplication extends Application {

    public Location myLocation;

    public Location getMyLocation() {
        return myLocation;
    }

    public void setMyLocation(Location myLocation) {
        this.myLocation = myLocation;
    }
}
