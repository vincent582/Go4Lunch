package com.pdv.go4lunch.ui.activities;

import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.pdv.go4lunch.Go4LunchApplication;
import com.pdv.go4lunch.Model.GooglePlacesApiModel.Results;
import com.pdv.go4lunch.R;
import com.pdv.go4lunch.ui.ViewModel.PlacesViewModel;
import com.pdv.go4lunch.utils.Permission;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

import static com.pdv.go4lunch.utils.Permission.PERMISSIONS_REQUEST_CALL_PHONE;
import static com.pdv.go4lunch.utils.Permission.PERMISSIONS_REQUEST_FINE_LOCATION;
import static com.pdv.go4lunch.utils.Utils.getCurrentUser;
import static com.pdv.go4lunch.utils.Utils.isCurrentUserLogged;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    //For Navigation
    private NavController mNavController;
    private AppBarConfiguration appBarConfiguration;

    //For Localisation
    private FusedLocationProviderClient mFusedLocationProviderClient;

    //For DATA
    private PlacesViewModel mPlacesViewModel;
    private List<Results> mPlaces = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        mNavController = Navigation.findNavController(this, R.id.nav_host_fragment);

        mPlacesViewModel = ViewModelProviders.of(this).get(PlacesViewModel.class);

        configureNavControllerWithNavigationView();
        configureNavControllerWithToolbarAndAppBarConfig();
        configureBottomNavigationView();

        getLocation();
    }

    /**
     * Setup navigationView to the navController.
     */
    public void configureNavControllerWithNavigationView() {
        NavigationView navView = findViewById(R.id.navigation_drawer);
        NavigationUI.setupWithNavController(navView, mNavController);
        updateNavigationDrawerUI(navView);
    }

    /**
     * Update NavigationView design with currentUser information
     * @param navView
     */
    private void updateNavigationDrawerUI(NavigationView navView) {
        //Get navigation drawer header and UI component.
        View headerNavigation = navView.inflateHeaderView(R.layout.header_nav);
        ImageView user_image = headerNavigation.findViewById(R.id.picture_user_drawer);
        TextView name = headerNavigation.findViewById(R.id.name_user_drawer);
        TextView mail = headerNavigation.findViewById(R.id.mail_user_drawer);

        //Then if user is authenticated with Firebase, display information.
        if (isCurrentUserLogged() != null) {
            //Get picture URL from Firebase
            if (getCurrentUser().getPhotoUrl() != null) {
                Glide.with(this)
                        .load(getCurrentUser().getPhotoUrl())
                        .circleCrop()
                        .into(user_image);
            }
            //Get email & username from Firebase
            String email = TextUtils.isEmpty(getCurrentUser().getEmail()) ? getString(R.string.info_no_email_found) : getCurrentUser().getEmail();
            String username = TextUtils.isEmpty(getCurrentUser().getDisplayName()) ? getString(R.string.info_no_username_found) : getCurrentUser().getDisplayName();
            //Update views with data
            name.setText(username);
            mail.setText(email);
        }
    }

    /**
     * Setup onNavigationItemSelected in bottomNavigationView.
     */
    private void configureBottomNavigationView() {
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation_bar);
        bottomNav.setOnNavigationItemSelectedListener(this::onNavigationItemSelected);
    }

    /**
     * Setup NavController with Toolbar and config.
     */
    private void configureNavControllerWithToolbarAndAppBarConfig() {
        DrawerLayout mDrawerLayout = findViewById(R.id.activity_main_layout_drawer);
        //Configure appBar to have hamburger icon on fragments
        appBarConfiguration = new AppBarConfiguration.Builder(R.id.mapFragment, R.id.listViewFragment
                        , R.id.workMatesFragment, R.id.yourLunchFragment, R.id.settingsFragment, R.id.logoutFragment)
                        .setDrawerLayout(mDrawerLayout)
                        .build();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        NavigationUI.setupWithNavController(toolbar,mNavController,appBarConfiguration);
    }

    /**
     * Inflate toolbar menu
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_toolbar,menu);
        return true;
    }

    /**
     * Do something on click to a toolbar menu item.
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        Log.e("TAG", "onOptionsItemSelected: "+item.getItemId());
        return true;
    }

    /**
     * Handles the Up button by delegating its behavior to the given NavController.
     */
    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(mNavController, appBarConfiguration) || super.onSupportNavigateUp();
    }

    /**
     * Get Location of the user if permission granted and save in application and request places.
     * Else ask request permission.
     */
    private void getLocation() {
        if (Permission.checkIfLocationPermissionGranted(this)) {
            mFusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    ((Go4LunchApplication) getApplication()).setMyLocation(location);
                    initPlaces(location);
                }
            });
        } else {
            Permission.requestLocationPermissions(this);
        }
    }

    /**
     * Get All places from ViewModel and add in attribute mPlaces.
     * @param location
     */
    private void initPlaces(Location location){
            mPlacesViewModel.getNearestPlaces(location).observe(this, new Observer<List<Results>>() {
                @Override
                public void onChanged(List<Results> results) {
                    mPlaces.addAll(results);
                    passArgumentToHostFragment();
                }
            });
    }

    /**
     * Add bundle to the Host Fragment of the navGraph.
     */
    private void passArgumentToHostFragment() {
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("PLACES", (ArrayList<? extends Parcelable>) mPlaces);
        mNavController.setGraph(R.navigation.nav_graph, bundle);

    }

    /**
     * Result of request Permissions
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_FINE_LOCATION) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getLocation();
                Toast.makeText(this,"Location Permission Granted",Toast.LENGTH_SHORT).show();
            }
        }
        else if (requestCode == PERMISSIONS_REQUEST_CALL_PHONE){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this,"Permission Granted",Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * On click on bottom navigation menu,
     * Put Bundle as argument and navigate to the menuItem destination
     * @param menuItem
     * @return
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("PLACES", (ArrayList<? extends Parcelable>) mPlaces);
        mNavController.navigate(menuItem.getItemId(),bundle);
        return true;
    }
}