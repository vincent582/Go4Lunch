package com.pdv.go4lunch.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.pdv.go4lunch.R;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity{

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

    private void configureNavigationView() {
        NavigationView navView = findViewById(R.id.navigation_view);
        NavigationUI.setupWithNavController(navView, mNavController);
        //get header and display information.
        View headerNavigation = navView.inflateHeaderView(R.layout.header_nav);
        ImageView user_image = headerNavigation.findViewById(R.id.picture_user_drawer);
        TextView name = headerNavigation.findViewById(R.id.name_user_drawer);
        TextView mail = headerNavigation.findViewById(R.id.mail_user_drawer);
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

    private void configureDrawerLayout() {
        DrawerLayout mDrawerLayout = findViewById(R.id.activity_main_layout_drawer);
        appBarConfiguration =
                new AppBarConfiguration.Builder(R.id.mapFragment,R.id.listViewFragment
                        ,R.id.workMatesFragment, R.id.yourLunchFragment, R.id.settingsFragment)
                        .setDrawerLayout(mDrawerLayout)
                        .build();

        NavigationUI.setupActionBarWithNavController(this,mNavController,appBarConfiguration);
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
                        .setLogo(R.drawable.ic_room_service_black_24dp)
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

    @Nullable
    protected FirebaseUser getCurrentUser(){ return FirebaseAuth.getInstance().getCurrentUser(); }

    protected Boolean isCurrentUserLogged(){ return (this.getCurrentUser() != null); }


}
