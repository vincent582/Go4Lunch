package com.pdv.go4lunch.ui.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.pdv.go4lunch.API.UserHelper;
import com.pdv.go4lunch.Go4LunchApplication;
import com.pdv.go4lunch.Model.User;
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

    private static final int SIGN_OUT_TASK = 10;
    private static final int DELETE_USER_TASK = 20;
    private FirebaseUser currentUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_logout, container, false);
        ButterKnife.bind(this,view);

        currentUser = ((Go4LunchApplication)getActivity().getApplication()).getCurrentUser();

        updateUI();
        return view;
    }

    private void updateUI() {
        if (currentUser != null){
            //Get picture URL from Firebase
            if (currentUser.getPhotoUrl() != null) {
                Glide.with(this)
                        .load(currentUser.getPhotoUrl())
                        .apply(RequestOptions.circleCropTransform())
                        .into(mPictureUser);
            }

            //Get email & username
            String email = TextUtils.isEmpty(currentUser.getEmail()) ? getString(R.string.info_no_email_found) : currentUser.getEmail();
            String name = TextUtils.isEmpty(currentUser.getDisplayName()) ? getString(R.string.info_no_username_found) : currentUser.getDisplayName();

            //Update views with data
            this.mEmailUser.setText(email);
            this.mNameUser.setText(name);
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
                .setNegativeButton("non", null)
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
            UserHelper.deleteUser(currentUser.getUid());
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
