package com.pdv.go4lunch.ui.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.pdv.go4lunch.Model.Place.Photo;
import com.pdv.go4lunch.Model.Place.Result;
import com.pdv.go4lunch.R;

import java.io.Serializable;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailsActivity extends AppCompatActivity {

    @BindView(R.id.details_name)
    TextView mName;
    @BindView(R.id.details_adress)
    TextView mAdress;
    @BindView(R.id.details_picture_restaurant)
    ImageView mPictureRestaurant;

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
    }
}
