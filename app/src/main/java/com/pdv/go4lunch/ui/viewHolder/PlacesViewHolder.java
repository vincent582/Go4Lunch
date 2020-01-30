package com.pdv.go4lunch.ui.viewHolder;

import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QuerySnapshot;
import com.pdv.go4lunch.API.UserHelper;
import com.pdv.go4lunch.Model.Place.Result;
import com.pdv.go4lunch.R;
import com.pdv.go4lunch.ui.activities.DetailsActivity;
import com.pdv.go4lunch.utils.Utils;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.pdv.go4lunch.ui.activities.DetailsActivity.DETAILS_PLACES;

public class PlacesViewHolder extends RecyclerView.ViewHolder {

    //FOR UI
    @BindView(R.id.item_restaurant)
    public ConstraintLayout mItemRestaurant;
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
    @BindView(R.id.number_of_people_linear_layout)
    public LinearLayout mPeopleLinearLayout;
    @BindView(R.id.number_of_people_text_view)
    public TextView mNumberOfPeople;

    //CONSTRUCTOR
    public PlacesViewHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    /**
     * Update view with item restaurant
     * @param restaurant
     */
    public void updateWithPlaces(Result restaurant){

        UserHelper.getAllUserForRestaurant(restaurant.getName())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (!task.getResult().isEmpty()){
                            Log.e("TAG", "Number Of people eating here : "+task.getResult().size());
                            mPeopleLinearLayout.setVisibility(View.VISIBLE);
                            mNumberOfPeople.setText("("+task.getResult().size()+")");
                        }
                    }
                });

        mTitleRestaurant.setText(restaurant.getName());
        mAdressRestaurant.setText(restaurant.getVicinity());
        mDistanceRestaurant.setText(restaurant.getDistance()+"m");

        setRestaurantSchedules(restaurant);
        setRestaurantPicture(restaurant);

        mItemRestaurant.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), DetailsActivity.class);
            intent.putExtra(DETAILS_PLACES, restaurant);
            v.getContext().startActivity(intent);
        });
    }

    /**
     * Update picture of the Item
     * @param restaurant
     */
    private void setRestaurantPicture(Result restaurant) {
        if (restaurant.getPhotos() != null){
            String url = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference="+restaurant.getPhotos().get(0).getPhotoReference()+"&key=AIzaSyDGFBPIUVLpd36GZCrt1LQVL4zCaSbMzxU";
            Glide.with(mPictureRestaurant.getContext())
                    .load(url)
                    .centerCrop()
                    .into(mPictureRestaurant);
        }
    }

    /**
     * Set schedules of the restaurant
     * @param restaurant
     */
    private void setRestaurantSchedules(Result restaurant) {
        if (restaurant.getOpeningHours() != null) {
            if (restaurant.getOpeningHours().getOpenNow()) {
                Calendar calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_WEEK);
                //Api give array size 1 if place open 24/7
                if (restaurant.getOpeningHours().getPeriods().size() > 1){
                    String time = restaurant.getOpeningHours().getPeriods().get(day-1).getClose().getTime();
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
    }
}
