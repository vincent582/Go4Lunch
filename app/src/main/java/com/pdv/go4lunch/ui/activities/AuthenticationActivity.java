package com.pdv.go4lunch.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.material.snackbar.Snackbar;
import com.pdv.go4lunch.API.UserHelper;
import com.pdv.go4lunch.R;

import java.util.Arrays;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class AuthenticationActivity extends BaseActivity {

    private static final int RC_SIGN_IN = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);
        ButterKnife.bind(this);
        checkIfUserLoggedAndStartActivity();
    }

    public void checkIfUserLoggedAndStartActivity(){
        if(isCurrentUserLogged()){
            startMainActivity();
        }
    }

    @OnClick(R.id.connexion_btn)
    public void onClickConnectionButton(){
        startSignInActivity();
    }

    // Create and launch sign-in intent
    private void startSignInActivity(){
        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build(),
                new AuthUI.IdpConfig.FacebookBuilder().build()
        );

        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .setTheme(R.style.LoginTheme)
                        .setLogo(R.drawable.ic_logo_auth)
                        .setIsSmartLockEnabled(false,true)
                        .build(),
                RC_SIGN_IN);
    }

    //Get Result of authentication
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IdpResponse response = IdpResponse.fromResultIntent(data);

        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) { // SUCCESS
                createUserInFirebase();
                startMainActivity();
            } else { // ERRORS
                if (response == null) {
                    showSnackBar(this.findViewById(R.id.authentication_layout), "Identification interrompu !");
                } else if (response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
                    showSnackBar(this.findViewById(R.id.authentication_layout), "Pas de connection internet !");
                } else if (response.getError().getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                    showSnackBar(this.findViewById(R.id.authentication_layout), "Une erreur inconu c'est produite !");
                }
            }
        }
    }

    private void createUserInFirebase() {
        if(this.getCurrentUser() != null){
            String Uid = getCurrentUser().getUid();
            String userName = getCurrentUser().getDisplayName();
            String urlPicture = (getCurrentUser().getPhotoUrl() != null) ? getCurrentUser().getPhotoUrl().toString() : null;
            UserHelper.createUser(Uid,userName,urlPicture).addOnFailureListener(this.onFailureListener());
        }
    }

    private void startMainActivity() {
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
    }

    private void showSnackBar(View view, String message) {
        Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show();
    }
}
