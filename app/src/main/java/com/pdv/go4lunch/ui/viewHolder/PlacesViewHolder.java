package com.pdv.go4lunch.ui.viewHolder;

import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.pdv.go4lunch.API.RestaurantHelper;
import com.pdv.go4lunch.API.UserHelper;
import com.pdv.go4lunch.BuildConfig;
import com.pdv.go4lunch.Model.GooglePlacesApiModel.Results;
import com.pdv.go4lunch.Model.Place.Result;
import com.pdv.go4lunch.Model.Restaurant;
import com.pdv.go4lunch.R;
import com.pdv.go4lunch.ui.activities.DetailsActivity;
import com.pdv.go4lunch.utils.Utils;

import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.pdv.go4lunch.ui.fragment.MapFragment.PLACE_ID;

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
    @BindView(R.id.ratingRestaurant)
    public RatingBar mRatingRestaurant;

    //CONSTRUCTOR
    public PlacesViewHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    /**
     * Update view with item restaurant
     * @param restaurant
     * @param restaurantListFromFirestore
     */
    public void updateWithPlaces(Results restaurant, List<Restaurant> restaurantListFromFirestore){

        for (Restaurant resto: restaurantListFromFirestore) {
            if (resto.getId().equals(restaurant.getPlaceId()) && resto.getLikes() > 0){
                UserHelper.getAllUsers().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        int nbrOfStars = Utils.getNumberOfStars(queryDocumentSnapshots.size(),resto.getLikes());
                        mRatingRestaurant.setVisibility(View.VISIBLE);
                        mRatingRestaurant.setNumStars(nbrOfStars);
                        mRatingRestaurant.setRating(nbrOfStars);
                    }
                });
            } else if (resto.getId().equals(restaurant.getPlaceId()) && resto.getLikes() == 0){
                mRatingRestaurant.setVisibility(View.INVISIBLE);
            }

            if (resto.getId().equals(restaurant.getPlaceId()) && resto.getNbrPeopleEatingHere() > 0){
                mPeopleLinearLayout.setVisibility(View.VISIBLE);
                mNumberOfPeople.setText("("+resto.getNbrPeopleEatingHere()+")");
            } else if (resto.getId().equals(restaurant.getPlaceId()) && resto.getNbrPeopleEatingHere() == 0){
                mPeopleLinearLayout.setVisibility(View.INVISIBLE);
            }
        }

        mTitleRestaurant.setText(restaurant.getName());
        mAdressRestaurant.setText(restaurant.getVicinity());
        mDistanceRestaurant.setText(restaurant.getDistance()+"m");

        if (restaurant.getOpeningHours() != null) {
            if (restaurant.getOpeningHours().getOpenNow()) {
                mOpeningRestaurant.setText(R.string.open);
            } else {
                mOpeningRestaurant.setText(R.string.closed);
                mOpeningRestaurant.setTextColor(Color.RED);
            }
        }

        setRestaurantPicture(restaurant);

        mItemRestaurant.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), DetailsActivity.class);
            intent.putExtra(PLACE_ID, restaurant.getPlaceId());
            v.getContext().startActivity(intent);
        });
    }

    /**
     * Update picture of the Item
     * @param restaurant
     */
    private void setRestaurantPicture(Results restaurant) {
        if (restaurant.getPhotos() != null){
            String url = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference="+restaurant.getPhotos().get(0).getPhotoReference()+"&key="+ BuildConfig.GOOGLE_API_KEY;
            Glide.with(mPictureRestaurant.getContext())
                    .load(url)
                    .centerCrop()
                    .into(mPictureRestaurant);
        }
    }
}
