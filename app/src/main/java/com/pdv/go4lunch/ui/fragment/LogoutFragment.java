package com.pdv.go4lunch.ui.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.pdv.go4lunch.R;
import com.pdv.go4lunch.ui.activities.MainActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class LogoutFragment extends Fragment {

    @BindView(R.id.picture_user_logout_fragment)
    ImageView mPictureUser;
    @BindView(R.id.user_name_logout_fr)
    TextView mNameUser;
    @BindView(R.id.user_email_logout_fr)
    TextView mEmailUser;

    private FirebaseUser currentUser;
    private static final int SIGN_OUT_TASK = 10;
    private static final int DELETE_USER_TASK = 20;

    public LogoutFragment() {
        // Required empty public constructor
    }

    @Nullable
    public FirebaseUser getCurrentUser(){ return FirebaseAuth.getInstance().getCurrentUser(); }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_logout, container, false);
        ButterKnife.bind(this,view);

        currentUser = getCurrentUser();

        Log.e("TAG", "onCreateView: "+ currentUser );

        UpdateUI();
        return view;
    }

    private void UpdateUI() {
        if (currentUser != null){
            //Get picture URL from Firebase
            if (currentUser.getPhotoUrl() != null) {
                Glide.with(this)
                        .load(currentUser.getPhotoUrl())
                        .apply(RequestOptions.circleCropTransform())
                        .into(mPictureUser);
            }

            //Get email & username from Firebase
            String email = TextUtils.isEmpty(currentUser.getEmail()) ? getString(R.string.info_no_email_found) : currentUser.getEmail();
            String username = TextUtils.isEmpty(currentUser.getDisplayName()) ? getString(R.string.info_no_username_found) : currentUser.getDisplayName();

            //Update views with data
            this.mNameUser.setText(username);
            this.mEmailUser.setText(email);
        }
    }


    @OnClick(R.id.logout_btn)
    public void onClickSignOutButton() { this.signOutUserFromFirebase(); }

    @OnClick(R.id.delette_btn)
    public void onClickDeleteButton() {
        new AlertDialog.Builder(getContext())
                .setMessage("Vous allez supprimer votre compte, êtes vous sure ?")
                .setPositiveButton("oui", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deleteUserFromFirebase();
                    }
                })
                .setNegativeButton("no", null)
                .show();
    }

    // --------------------
    // REST REQUESTS
    // --------------------
    // 1 - Create http requests (SignOut & Delete)

    private void signOutUserFromFirebase(){
        AuthUI.getInstance()
                .signOut(getContext())
                .addOnSuccessListener(this.updateUIAfterRESTRequestsCompleted(SIGN_OUT_TASK));
    }

    private void deleteUserFromFirebase(){
        if (currentUser != null) {
            AuthUI.getInstance()
                    .delete(getContext())
                    .addOnSuccessListener(this.updateUIAfterRESTRequestsCompleted(DELETE_USER_TASK));
        }
    }


    private OnSuccessListener<Void> updateUIAfterRESTRequestsCompleted(final int origin){
        return new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                switch (origin){
                    case SIGN_OUT_TASK:
                        getActivity().finish();
                        break;
                    case DELETE_USER_TASK:
                        getActivity().finish();
                        break;
                    default:
                        break;
                }
            }
        };
    }
}
