package com.pdv.go4lunch;

import android.app.Application;
import android.location.Location;

import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Go4LunchApplication extends Application {

    @Nullable
    public FirebaseUser getCurrentUser(){ return FirebaseAuth.getInstance().getCurrentUser();}

    public Boolean isCurrentUserLogged(){ return (this.getCurrentUser() != null); }

    public Location myLocation;

    public Location getMyLocation() {
        return myLocation;
    }

    public void setMyLocation(Location myLocation) {
        this.myLocation = myLocation;
    }
}
