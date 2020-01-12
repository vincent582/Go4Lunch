package com.pdv.go4lunch.ui.activities;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavArgs;
import androidx.navigation.NavArgument;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.bumptech.glide.Glide;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.pdv.go4lunch.Go4LunchApplication;
import com.pdv.go4lunch.R;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity{

    //For Navigation
    private NavController mNavController;
    private AppBarConfiguration appBarConfiguration;

    //For Permission
    private int PERMISSIONS_REQUEST_FINE_LOCATION = 1;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Location mLastKnownLocation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        mNavController = Navigation.findNavController(this,R.id.nav_host_fragment);

        configureNavigationDrawer();
        configureToolbar();
        configureLayoutDrawer();
        configureBottomNavigationView();
        checkPermission();
    }

/* **************************************************************
 * FIREBASE
 ****************************************************************/

    @Nullable
    public FirebaseUser getCurrentUser(){ return FirebaseAuth.getInstance().getCurrentUser(); }

    public Boolean isCurrentUserLogged(){ return (this.getCurrentUser() != null); }


/* **************************************************************
 * Configure Navigation Layout && UI component
 ****************************************************************/

    public void configureNavigationDrawer() {
        NavigationView navView = findViewById(R.id.navigation_drawer);
        NavigationUI.setupWithNavController(navView, mNavController);
        //Get navigation drawer header and UI component.
        View headerNavigation = navView.inflateHeaderView(R.layout.header_nav);
        ImageView user_image = headerNavigation.findViewById(R.id.picture_user_drawer);
        TextView name = headerNavigation.findViewById(R.id.name_user_drawer);
        TextView mail = headerNavigation.findViewById(R.id.mail_user_drawer);
        //Then if user is authenticated with firebase, display information.
        if (this.getCurrentUser() != null){
            //Get picture URL from Firebase
            if (this.getCurrentUser().getPhotoUrl() != null) {
                Glide.with(this)
                        .load(this.getCurrentUser().getPhotoUrl())
                        .circleCrop()
                        .into(user_image);
            }
            //Get email & username from Firebase
            String email = TextUtils.isEmpty(this.getCurrentUser().getEmail()) ? getString(R.string.info_no_email_found) : this.getCurrentUser().getEmail();
            String username = TextUtils.isEmpty(this.getCurrentUser().getDisplayName()) ? getString(R.string.info_no_username_found) : this.getCurrentUser().getDisplayName();
            //Update views with data
            name.setText(username);
            mail.setText(email);
        }
    }

    private void configureBottomNavigationView() {
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation_bar);
        NavigationUI.setupWithNavController(bottomNav, mNavController);
    }

    private void configureLayoutDrawer() {
        DrawerLayout mDrawerLayout = findViewById(R.id.activity_main_layout_drawer);
        //Configure appBar to have hamburger icon on fragments
        appBarConfiguration =
                new AppBarConfiguration.Builder(R.id.mapFragment,R.id.listViewFragment
                        ,R.id.workMatesFragment, R.id.yourLunchFragment, R.id.settingsFragment,R.id.logoutFragment)
                        .setDrawerLayout(mDrawerLayout)
                        .build();
        NavigationUI.setupActionBarWithNavController(this,mNavController,appBarConfiguration);
    }

    private void configureToolbar() {
        Toolbar mToolbar = findViewById(R.id.toolbar_drawer);
        setSupportActionBar(mToolbar);
    }

    //Handles the Up button by delegating its behavior to the given NavController.
    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(mNavController, appBarConfiguration) || super.onSupportNavigateUp();
    }


/* **************************************************************
 * PERMISSION
 ****************************************************************/

    public void checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            Task locationResult = mFusedLocationProviderClient.getLastLocation();
            locationResult.addOnCompleteListener(this, (OnCompleteListener<Location>) task -> {
                if (task.isSuccessful()) {
                    mLastKnownLocation = task.getResult();
                }
            });
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                Toast.makeText(this,"We need your location to show you the map !",Toast.LENGTH_LONG).show();
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSIONS_REQUEST_FINE_LOCATION);
            }
            else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSIONS_REQUEST_FINE_LOCATION);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_FINE_LOCATION) {
            if (permissions.length == 1 &&
                    permissions[0] == Manifest.permission.ACCESS_FINE_LOCATION &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this,"Permission Granted",Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this,"Permission Denied",Toast.LENGTH_SHORT).show();
            }
        }
    }
}
