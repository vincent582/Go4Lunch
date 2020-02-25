package com.pdv.go4lunch.ui.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnSuccessListener;
import com.pdv.go4lunch.API.UserHelper;
import com.pdv.go4lunch.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.pdv.go4lunch.utils.Utils.getCurrentUser;
import static com.pdv.go4lunch.utils.Utils.isCurrentUserLogged;

public class LogoutFragment extends Fragment {

    //FOR UI
    @BindView(R.id.picture_user_logout_fragment)
    ImageView mPictureUser;
    @BindView(R.id.user_name_logout_fr)
    TextView mNameUser;
    @BindView(R.id.user_email_logout_fr)
    TextView mEmailUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_logout, container, false);
        ButterKnife.bind(this,view);
        updateUI();
        return view;
    }

    /**
     * Set UI with User information.
     */
    private void updateUI() {
        if (isCurrentUserLogged() != null){
            //Get picture URL from Firebase
            if (getCurrentUser().getPhotoUrl() != null) {
                Glide.with(this)
                        .load(getCurrentUser().getPhotoUrl())
                        .apply(RequestOptions.circleCropTransform())
                        .into(mPictureUser);
            }else {
                Glide.with(this)
                        .load(R.drawable.no_picture_user)
                        .circleCrop()
                        .into(mPictureUser);
            }

            //Get email & username
            String email = TextUtils.isEmpty(getCurrentUser().getEmail()) ? getString(R.string.info_no_email_found) : getCurrentUser().getEmail();
            String name = TextUtils.isEmpty(getCurrentUser().getDisplayName()) ? getString(R.string.info_no_username_found) : getCurrentUser().getDisplayName();
            //Update views with data
            this.mEmailUser.setText(email);
            this.mNameUser.setText(name);
        }
    }

    @OnClick(R.id.logout_btn)
    public void onClickSignOutButton() { this.signOutUserFromFirebase(); }

    /**
     * On click delete user, open dialog window to confirm deleting.
     */
    @OnClick(R.id.delette_btn)
    public void onClickDeleteButton() {
        new AlertDialog.Builder(getContext())
                .setMessage(getResources().getString(R.string.delete_account))
                .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deleteUserFromFirebase();
                    }
                })
                .setNegativeButton(getResources().getString(R.string.no), null)
                .show();
    }

    /**
     * Sign out User from Firebase
     */
    private void signOutUserFromFirebase(){
        AuthUI.getInstance()
                .signOut(getContext())
                .addOnSuccessListener(this.killActivity());
    }

    /**
     * Delete User From Firebase.
     */
    private void deleteUserFromFirebase(){
        if (isCurrentUserLogged() != null) {
            UserHelper.deleteUser(getCurrentUser().getUid());
            AuthUI.getInstance()
                    .delete(getContext())
                    .addOnSuccessListener(this.killActivity());
            UserHelper.deleteUser(getCurrentUser().getUid());
        }
    }

    /**
     * If Sign out or delete user succeeded kill MainActivity
     * @return
     */
    private OnSuccessListener<Void> killActivity(){
        return new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                getActivity().finish();
            }
        };
    }
}
