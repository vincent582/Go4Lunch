package com.pdv.go4lunch.ui.viewHolder;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.pdv.go4lunch.Model.Place.Result;
import com.pdv.go4lunch.R;
import com.pdv.go4lunch.ui.activities.DetailsActivity;
import com.pdv.go4lunch.utils.Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.pdv.go4lunch.ui.activities.DetailsActivity.INTENT_PLACE;

public class PlacesViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.item_restaurant)
    public LinearLayout mItemRestaurant;
    @BindView(R.id.item_title_restaurant)
    public TextView mTitleRestaurant;
    @BindView(R.id.item_adress_restaurant)
    public TextView mAdressRestaurant;
    @BindView(R.id.item_distance_restaurant)
    public TextView mDistanceRestaurant;
    @BindView(R.id.item_picture_restaurent)
    public ImageView mPictureRestaurant;
    @BindView(R.id.item_opening_restaurant)
    public TextView mOpeningRestaurant;

    public PlacesViewHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    @SuppressLint("ResourceAsColor")
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void updateWithPlaces(Result place, Location myLocation){

        Log.e("TAG", "Photos : " + place.getPhotos());
        if (place.getPhotos() != null){
            String url = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference="+place.getPhotos().get(0).getPhotoReference()+"&key=AIzaSyDGFBPIUVLpd36GZCrt1LQVL4zCaSbMzxU";
            Log.e("TAG", "URL : " + url);
            Glide.with(mPictureRestaurant.getContext())
                    .load(url)
                    .centerCrop()
                    .into(mPictureRestaurant);
        }

        mTitleRestaurant.setText(place.getName());
        mAdressRestaurant.setText(place.getVicinity());

        if (place.getOpeningHours() != null) {
            if (place.getOpeningHours().getOpenNow()) {
                Calendar calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_WEEK);
                //Api give array size 1 if place open 24/7
                if (place.getOpeningHours().getPeriods().size() > 1){
                    String time = place.getOpeningHours().getPeriods().get(day-1).getClose().getTime();
                    mOpeningRestaurant.setText(Utils.formatTimeFromOpenningHours(time));
                }else {
                    mOpeningRestaurant.setText("Open 24/7");
                }
            }else{
                mOpeningRestaurant.setText("Closed");
                mOpeningRestaurant.setTextColor(Color.RED);
            }
        }else {
            mOpeningRestaurant.setText("");
        }

        mDistanceRestaurant.setText(Utils.getDistanceBetweenLocation(myLocation,place)+"m");

        mItemRestaurant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), DetailsActivity.class);
                intent.putExtra(INTENT_PLACE, place);
                v.getContext().startActivity(intent);
            }
        });
    }
}
