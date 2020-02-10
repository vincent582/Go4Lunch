package com.pdv.go4lunch.ui.activities;

import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
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
import com.pdv.go4lunch.R;
import com.pdv.go4lunch.ui.ViewModel.FirestoreViewModel;
import com.pdv.go4lunch.ui.ViewModel.PlacesViewModel;
import com.pdv.go4lunch.utils.Permission;

import static com.pdv.go4lunch.utils.Permission.PERMISSIONS_REQUEST_CALL_PHONE;
import static com.pdv.go4lunch.utils.Permission.PERMISSIONS_REQUEST_FINE_LOCATION;
import static com.pdv.go4lunch.utils.Utils.getCurrentUser;
import static com.pdv.go4lunch.utils.Utils.isCurrentUserLogged;

public class MainActivity extends BaseActivity {

    //For Navigation
    private NavController mNavController;
    private AppBarConfiguration mAppBarConfiguration;
    private Toolbar toolbar;
    private BottomNavigationView bottomNavigationView;

    //For Localisation
    private FusedLocationProviderClient mFusedLocationProviderClient;

    //For DATA
    private PlacesViewModel mPlacesViewModel;
    private FirestoreViewModel mFirestoreViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        mPlacesViewModel = ViewModelProviders.of(this).get(PlacesViewModel.class);
        mFirestoreViewModel = ViewModelProviders.of(this).get(FirestoreViewModel.class);
        mNavController = Navigation.findNavController(this, R.id.nav_host_fragment);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.activity_main_layout_drawer);
        NavigationView navView = findViewById(R.id.navigation_drawer);
        bottomNavigationView = findViewById(R.id.bottom_navigation_bar);

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.mapFragment, R.id.listViewFragment, R.id.workMatesFragment)
                .setDrawerLayout(drawer)
                .build();
        NavigationUI.setupActionBarWithNavController(this, mNavController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navView, mNavController);
        NavigationUI.setupWithNavController(bottomNavigationView, mNavController);
        updateNavigationDrawerUI(navView);

        getLocation();

        addDestinationChangeListenerToNavController();
    }

    private void addDestinationChangeListenerToNavController() {
        mNavController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination, @Nullable Bundle arguments) {
                if (destination.getId() == R.id.settingsFragment) {
                    toolbar.setTitle(getResources().getString(R.string.settings_title_fragment));
                    bottomNavigationView.setVisibility(View.GONE);
                } else if (destination.getId() == R.id.logoutFragment) {
                    toolbar.setTitle(getResources().getString(R.string.logout_title_fragment));
                    bottomNavigationView.setVisibility(View.GONE);
                } else if (destination.getId() == R.id.yourLunchFragment) {
                    toolbar.setTitle(getResources().getString(R.string.your_lunch_title_fragment));
                    bottomNavigationView.setVisibility(View.GONE);
                } else if (destination.getId() == R.id.workMatesFragment){
                    toolbar.setTitle(getResources().getString(R.string.workmates_title_fragment));
                    bottomNavigationView.setVisibility(View.VISIBLE);
                } else {
                    toolbar.setTitle(getResources().getString(R.string.hungry_title_fragment));
                    bottomNavigationView.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return NavigationUI.onNavDestinationSelected(item, mNavController)
                || super.onOptionsItemSelected(item);
    }

    /**
     * Handles the Up button by delegating its behavior to the given NavController.
     */
    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(mNavController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
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
     * Get Location of the user if permission granted and save in application and request places.
     * Else ask request permission.
     */
    private void getLocation() {
        if (Permission.checkIfLocationPermissionGranted(this)) {
            mFusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    ((Go4LunchApplication) getApplication()).setMyLocation(location);
                    mPlacesViewModel.init(location);
                    mNavController.navigate(R.id.mapFragment);
                }
            });
        } else {
            Permission.requestLocationPermissions(this);
        }
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
                Toast.makeText(this,String.valueOf(R.string.permission_granted),Toast.LENGTH_SHORT).show();
            }
        }
        else if (requestCode == PERMISSIONS_REQUEST_CALL_PHONE){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this,String.valueOf(R.string.permission_call_granted),Toast.LENGTH_SHORT).show();
            }
        }
    }
}