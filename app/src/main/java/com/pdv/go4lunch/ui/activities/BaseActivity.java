package com.pdv.go4lunch.ui.activities;

import android.content.pm.PackageManager;
import android.location.Location;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.pdv.go4lunch.Go4LunchApplication;
import com.pdv.go4lunch.utils.Permission;

import static com.pdv.go4lunch.utils.Permission.PERMISSIONS_REQUEST_CALL_PHONE;
import static com.pdv.go4lunch.utils.Permission.PERMISSIONS_REQUEST_FINE_LOCATION;

public abstract class BaseActivity extends AppCompatActivity {

    protected FusedLocationProviderClient mFusedLocationProviderClient;

    @Nullable
    public FirebaseUser getCurrentUser(){ return FirebaseAuth.getInstance().getCurrentUser();}

    public Boolean isCurrentUserLogged(){ return (this.getCurrentUser() != null); }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_FINE_LOCATION) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this,"Permission Granted",Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this,"Permission Denied",Toast.LENGTH_SHORT).show();
            }
        }
        else if (requestCode == PERMISSIONS_REQUEST_CALL_PHONE){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this,"Permission Granted",Toast.LENGTH_SHORT).show();
            }
        }
    }

    public OnFailureListener onFailureListener(){
        return new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(),"Erreur :"+ e,Toast.LENGTH_SHORT).show();
            }
        };
    }
}
