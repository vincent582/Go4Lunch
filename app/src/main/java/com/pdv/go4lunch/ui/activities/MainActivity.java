package com.pdv.go4lunch.ui.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.pdv.go4lunch.R;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private static final int RC_SIGN_IN = 123;
    private NavController mNavController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startSignInActivity();
        mNavController = Navigation.findNavController(this, R.id.nav_host_fragment);

        configureNavigationView();
        configureToolbar();
        configureDrawerLayout();
        configureBottomNavigationView();
    }


    private void configureBottomNavigationView() {
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation_bar);
        NavigationUI.setupWithNavController(bottomNav, mNavController);
    }

    private void configureDrawerLayout() {
        DrawerLayout mDrawerLayout = findViewById(R.id.activity_main_layout_drawer);
        appBarConfiguration =
                new AppBarConfiguration.Builder(R.id.mapFragment,R.id.listViewFragment
                        ,R.id.workMatesFragment, R.id.yourLunchFragment, R.id.settingsFragment)
                        .setDrawerLayout(mDrawerLayout)
                        .build();

        NavigationUI.setupActionBarWithNavController(this,mNavController,appBarConfiguration);
    }

    private void configureNavigationView() {
        NavigationView navView = findViewById(R.id.navigation_view);
        NavigationUI.setupWithNavController(navView, mNavController);
    }

    private void configureToolbar() {
        Toolbar mToolbar = findViewById(R.id.toolbar_drawer);
        setSupportActionBar(mToolbar);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, appBarConfiguration) || super.onSupportNavigateUp();
    }







    // Create and launch sign-in intent
    private void startSignInActivity(){
        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.FacebookBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build()
        );

        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .setTheme(R.style.LoginTheme)
                        .setIsSmartLockEnabled(false,true)
                        .build(),
                RC_SIGN_IN);
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
            }
        }
    }
}
