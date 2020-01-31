package com.pdv.go4lunch.ui.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.Query;
import com.pdv.go4lunch.API.RestaurantHelper;
import com.pdv.go4lunch.API.UserHelper;
import com.pdv.go4lunch.Model.Place.Result;
import com.pdv.go4lunch.Model.User;
import com.pdv.go4lunch.R;
import com.pdv.go4lunch.ui.viewHolder.UserRecyclerViewAdapter;
import com.pdv.go4lunch.utils.Permission;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.pdv.go4lunch.utils.Utils.getCurrentUser;

public class DetailsActivity extends AppCompatActivity {

    //FOR UI
    @BindView(R.id.details_name)
    TextView mName;
    @BindView(R.id.details_adress)
    TextView mAdress;
    @BindView(R.id.details_picture_restaurant)
    ImageView mPictureRestaurant;
    @BindView(R.id.phone_icon_iv)
    ImageView mCallIcon;
    @BindView(R.id.calling_tv)
    TextView mCallTextView;
    @BindView(R.id.website_icon_iv)
    ImageView mWebsiteIcon;
    @BindView(R.id.website_textview)
    TextView mWebsiteTextView;
    @BindView(R.id.floatingActionButton)
    FloatingActionButton mSelectRestaurantButton;
    @BindView(R.id.details_item_workmates_rv)
    RecyclerView mRecyclerView;

    //FOR DATA
    public static String DETAILS_PLACES = "DETAILS_PLACES";
    private Result mRestaurant;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        ButterKnife.bind(this);

        if(getIntent().hasExtra(DETAILS_PLACES)){
            mRestaurant = getIntent().getParcelableExtra(DETAILS_PLACES);
        }

        UserRecyclerViewAdapter adapter = new UserRecyclerViewAdapter(generateOptionForAdapter(UserHelper.getAllUserForRestaurant(mRestaurant.getPlace_id())));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(adapter);

        updateView();
    }

    /**
     * Generation Option adapter for query request on Firebase.
     * @param query
     * @return
     */
    private FirestoreRecyclerOptions<User> generateOptionForAdapter(Query query) {
        return new FirestoreRecyclerOptions.Builder<User>()
                .setQuery(query,User.class)
                .setLifecycleOwner(this)
                .build();
    }

    /**
     * Set view with restaurant information got from Intent
     */
    private void updateView() {
        mName.setText(mRestaurant.getName());
        mAdress.setText(mRestaurant.getVicinity());
        Log.e("TAG", "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference="+ mRestaurant.getPhotos().get(0).getPhotoReference()+"&key=AIzaSyDGFBPIUVLpd36GZCrt1LQVL4zCaSbMzxU");
        if (mRestaurant.getPhotos() != null){
        String url = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference="+ mRestaurant.getPhotos().get(0).getPhotoReference()+"&key=AIzaSyDGFBPIUVLpd36GZCrt1LQVL4zCaSbMzxU";
        Glide.with(mPictureRestaurant.getContext())
                .load(url)
                .centerCrop()
                .into(mPictureRestaurant);
        }
        setUpPhoneIcon();
        setUpWebsiteIcon();
        setUpSelectRestaurantButton();
    }


    /**
     * Set up click on Website icon if restaurant have one.
     */
    private void setUpWebsiteIcon() {
        if (mRestaurant.getWebsite() != null) {
            mWebsiteIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goToWebsiteDialog();
                }
            });
        }
        else {
            mWebsiteIcon.setColorFilter(ContextCompat.getColor(this, R.color.colorGrey), PorterDuff.Mode.MULTIPLY);
            mWebsiteTextView.setTextColor(getResources().getColor(R.color.colorGrey));
        }
    }

    /**
     * Open Dialog alert to ask to go on the website by intent.
     */
    private void goToWebsiteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Go to website ?").setMessage(mRestaurant.getWebsite());
        builder.setPositiveButton("Go", (dialog, id) -> {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.addCategory(Intent.CATEGORY_BROWSABLE);
            intent.setData(Uri.parse(mRestaurant.getWebsite()));
            startActivity(intent);
        });
        builder.setNegativeButton("Cancel", (dialog, id) -> dialog.cancel());
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * Set up click on phone icon if restaurant have phone number.
     */
    private void setUpPhoneIcon() {
        if (mRestaurant.getFormattedPhoneNumber() != null){
            mCallIcon.setOnClickListener(v -> checkPermissionCall());
        }
        else {
            mCallIcon.setColorFilter(ContextCompat.getColor(this, R.color.colorGrey),PorterDuff.Mode.MULTIPLY);
            mCallTextView.setTextColor(getResources().getColor(R.color.colorGrey));
        }
    }

    /**
     * Asking for permissionCalling if ok,
     * Open Dialog alert to ask to go on make phone call by intent.
     */
    public void checkPermissionCall(){
        if (Permission.checkIfCallingPermissionGranted(this)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Call the restaurant ?").setMessage(mRestaurant.getFormattedPhoneNumber());
            builder.setPositiveButton("Call", (dialog, id) -> {
                Intent callingIntent = new Intent(Intent.ACTION_CALL);
                callingIntent.setData(Uri.parse("tel:" + mRestaurant.getFormattedPhoneNumber()));
                startActivity(callingIntent);
            });
            builder.setNegativeButton("Cancel", (dialog, id) -> dialog.cancel());
            AlertDialog dialog = builder.create();
            dialog.show();
        }
        else{
            Permission.requestCallingPermissions(this);
        }
    }

    /**
     * Add click listener to ActionButton to select restaurant
     */
    private void setUpSelectRestaurantButton() {
        mSelectRestaurantButton.setOnClickListener(v -> updateRestaurantInFirebase());
    }

    /**
     * Save therestaurant chosen in firebase
     */
    private void updateRestaurantInFirebase() {
        RestaurantHelper.createRestaurant(mRestaurant.getName(),mRestaurant.getPlace_id(),true);
        UserHelper.updateUserRestaurantId(mRestaurant.getPlace_id(), getCurrentUser().getUid());
        Toast.makeText(this, "Vous avez choisi ce restaurant", Toast.LENGTH_SHORT).show();
    }
}
