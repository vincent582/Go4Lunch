package com.pdv.go4lunch.utils;

import android.content.Intent;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.pdv.go4lunch.Model.GooglePlacesApiModel.Results;
import com.pdv.go4lunch.Model.Place.Photo;
import com.pdv.go4lunch.Model.Place.Result;
import com.pdv.go4lunch.R;
import com.pdv.go4lunch.ui.activities.DetailsActivity;

import java.io.Serializable;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.pdv.go4lunch.ui.activities.DetailsActivity.INTENT_PLACE;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private List<Result> mPlaces;

    public RecyclerViewAdapter(){}

    public void updatedPlaces(List<Result> places){
        this.mPlaces = places;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_restaurant,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Result place = mPlaces.get(position);
        Log.e("TAG", "Photos : " + place.getPhotos());
        if (place.getPhotos() != null){
            String url = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference="+place.getPhotos().get(0).getPhotoReference()+"&key=AIzaSyDGFBPIUVLpd36GZCrt1LQVL4zCaSbMzxU";
            Log.e("TAG", "URL : " + url);
            Glide.with(holder.mPictureRestaurant.getContext())
                    .load(url)
                    .centerCrop()
                    .into(holder.mPictureRestaurant);
        }

            holder.mTitleRestaurant.setText(place.getName());
            holder.mAdressRestaurant.setText(place.getVicinity());
            holder.mStatusRestaurant.setText(place.getFormattedPhoneNumber());

            holder.mItemRestaurant.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(),DetailsActivity.class);
                    intent.putExtra(INTENT_PLACE, (Serializable) place);
                    v.getContext().startActivity(intent);
                }
            });
        }

    @Override
    public int getItemCount() {
        if (mPlaces != null) {
            return mPlaces.size();
        } else {
            return 0;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.item_restaurant)
        public LinearLayout mItemRestaurant;
        @BindView(R.id.item_title_restaurant)
        public TextView mTitleRestaurant;
        @BindView(R.id.item_adress_restaurant)
        public TextView mAdressRestaurant;
        @BindView(R.id.item_distance_restaurant)
        public TextView mDistanceRestaurant;
        @BindView(R.id.item_picture_restaurant)
        public ImageView mPictureRestaurant;
        @BindView(R.id.item_status_restaurant)
        public TextView mStatusRestaurant;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
