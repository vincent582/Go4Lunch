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

import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.pdv.go4lunch.Model.Place.Result;
import com.pdv.go4lunch.R;
import com.pdv.go4lunch.utils.Permission;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailsActivity extends BaseActivity {

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

    public static String INTENT_PLACE = "INTENT_PLACE";
    private Result place;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        ButterKnife.bind(this);

        if(getIntent().hasExtra(INTENT_PLACE)){
            place = (Result) getIntent().getSerializableExtra(INTENT_PLACE);
        }
        updateView();
    }

    private void updateView() {
        mName.setText(place.getName());
        mAdress.setText(place.getVicinity());
        if (place.getPhotos() != null){
        String url = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference="+place.getPhotos().get(0).getPhotoReference()+"&key=AIzaSyDGFBPIUVLpd36GZCrt1LQVL4zCaSbMzxU";
        Log.e("TAG", "URL : " + url);
        Glide.with(mPictureRestaurant.getContext())
                .load(url)
                .centerCrop()
                .into(mPictureRestaurant);
        }
        setUpPhoneIcon();
        setUpWebsiteIcon();
    }

    private void setUpWebsiteIcon() {
        if (place.getWebsite() != null) {
            mWebsiteIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goToWebsiteDialog();
                }
            });
        }
        else {
            mWebsiteIcon.setColorFilter(ContextCompat.getColor(this, R.color.colorGrey),
                    PorterDuff.Mode.MULTIPLY);
            mWebsiteTextView.setTextColor(getResources().getColor(R.color.colorGrey));
        }
    }

    private void goToWebsiteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Go to website ?")
                .setMessage(place.getWebsite());
        builder.setPositiveButton("Go", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse(place.getWebsite()));
                startActivity(intent);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void setUpPhoneIcon() {
        if (place.getFormattedPhoneNumber() != null){
            mCallIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkPermissionCall();
                }
            });
        }
        else {
            mCallIcon.setColorFilter(ContextCompat.getColor(this, R.color.colorGrey),
                    PorterDuff.Mode.MULTIPLY);
            mCallTextView.setTextColor(getResources().getColor(R.color.colorGrey));
        }
    }

    public void checkPermissionCall(){
        if (Permission.checkIfCallingPermissionGranted(this)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Call this number ?")
                    .setMessage(place.getFormattedPhoneNumber());
            builder.setPositiveButton("Call", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    Intent callingIntent = new Intent(Intent.ACTION_CALL);
                    callingIntent.setData(Uri.parse("tel:" + place.getFormattedPhoneNumber()));
                    startActivity(callingIntent);
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
        else{
            Permission.requestCallingPermissions(this);
        }
    }
}
