package com.pdv.go4lunch.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.pdv.go4lunch.R;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.google.android.material.snackbar.Snackbar.LENGTH_SHORT;

public class AuthenticationActivity extends AppCompatActivity {

    //FOR DESIGN
    @BindView(R.id.progress_bar_auth)
    ProgressBar mProgressBar;

    //FOR DATA
    private static final int RC_SIGN_IN = 123;
    private FirebaseAuth mAuth;
    private CallbackManager mCallbackManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);
        ButterKnife.bind(this);
        mAuth = FirebaseAuth.getInstance();
    }

    /**
     * onStart check if user already Sign In
     * and start MainActivity
     */
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) startMainActivity();
    }

    /**
     * Start Main Activity with intent
     */
    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        mProgressBar.setVisibility(View.INVISIBLE);
    }

    /**
     * Handle onclick Google Button SignIn
     */
    @OnClick(R.id.sign_in_google_btn)
    public void signInWithGoogle(){
        mProgressBar.setVisibility(View.VISIBLE);
        configureSignInGoogle();
    }

    /**
     * Handle onclick Facebook button SignIn
     */
    @OnClick(R.id.sign_in_facebook_btn)
    public void signInWithFacebook(){
        configureSignInFacebook();
    }

    /**
     * Configure sign-in Google to request the User's ID, email adress, and basic profile
     * include in DEFAULT_SIGN_IN.
     */
    private void configureSignInGoogle(){
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("399758132941-cfspiafk22cke87galdbdaft959v4kn4.apps.googleusercontent.com")
                .requestEmail()
                .build();
        // Build a GoogleSignInClient with option specified above
        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        //Then start activity for result with sign in intent
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    /**
     * Configure sign-in Facebook to request the User's photos, email adress, birthday and public profile.
     */
    private void configureSignInFacebook() {
        List<String> permission = Arrays.asList("user_photos", "email", "user_birthday", "public_profile");
        mCallbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().logInWithReadPermissions(this, permission);
        LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookAccessToken(loginResult.getAccessToken());
            }
            @Override
            public void onCancel() {
                Snackbar.make(findViewById(R.id.authentication_layout), "You canceled the authentication!", LENGTH_SHORT).show();
            }
            @Override
            public void onError(FacebookException error) {
                Snackbar.make(findViewById(R.id.authentication_layout), "An error occurred Authentication canceled!", LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN){
            //Google signIn result
            if (resultCode == RESULT_OK) {
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                try {
                    GoogleSignInAccount account = task.getResult(ApiException.class);
                    fireBaseAuthWithGoogle(account);
                } catch (ApiException e) {
                    Log.w("TAG", "handleSignInResult:failed code= " + e.getStatusCode());
                }
            }else{
                Snackbar.make(findViewById(R.id.authentication_layout), "An error occurred Authentication canceled!", LENGTH_SHORT).show();
                mProgressBar.setVisibility(View.INVISIBLE);
            }
        }

        if(mCallbackManager != null){
            //Facebook signIn callback
            mCallbackManager.onActivityResult(requestCode,resultCode,data);
        }
    }

    /**
     * Authentication in firebase with Google
     * @param account
     */
    private void fireBaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //TODO put current user in database firestore
                            //FirebaseUser user = mAuth.getCurrentUser();
                            startMainActivity();
                        } else {
                            Snackbar.make(findViewById(R.id.authentication_layout), "Authentication Failed !", LENGTH_SHORT).show();
                            mProgressBar.setVisibility(View.INVISIBLE);
                        }
                    }
                });
    }

    /**
     * Authentication in firebase with Facebook
     * @param accessToken
     */
    private void handleFacebookAccessToken(AccessToken accessToken) {
        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            //TODO put current user in database firestore
                            startMainActivity();
                        } else {
                            Log.w("TAG", "signInWithCredential: failure "+task.getException());
                            Snackbar.make(findViewById(R.id.authentication_layout), "Authentication Failed !", LENGTH_SHORT).show();
                        }
                    }
                });
    }
}