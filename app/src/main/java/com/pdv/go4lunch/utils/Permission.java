package com.pdv.go4lunch.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public abstract class Permission {

    //For Permission
    public static int PERMISSIONS_REQUEST_FINE_LOCATION = 1;
    public static int PERMISSIONS_REQUEST_CALL_PHONE = 2;

    public static boolean checkIfLocationPermissionGranted(Context context) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    public static void requestLocationPermissions(Activity activity){
        ActivityCompat.requestPermissions(activity,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSIONS_REQUEST_FINE_LOCATION);
    }

    public static boolean checkIfCallingPermissionGranted(Context context) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE)
                == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    public static void requestCallingPermissions(Activity activity){
        ActivityCompat.requestPermissions(activity,
                new String[]{Manifest.permission.CALL_PHONE},
                PERMISSIONS_REQUEST_CALL_PHONE);
    }
}
