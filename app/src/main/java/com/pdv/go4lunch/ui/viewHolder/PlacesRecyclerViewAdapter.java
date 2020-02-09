package com.pdv.go4lunch.ui.viewHolder;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pdv.go4lunch.Model.GooglePlacesApiModel.Results;
import com.pdv.go4lunch.Model.Restaurant;
import com.pdv.go4lunch.R;

import java.util.List;

public class PlacesRecyclerViewAdapter extends RecyclerView.Adapter<PlacesViewHolder> {

    //FOR DATA
    private List<Results> mRestaurant;
    private List<Restaurant> mRestaurantListFromFirestore;

    //CONSTRUCTOR
    public PlacesRecyclerViewAdapter(){}

    @NonNull
    @Override
    public PlacesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_restaurant,parent,false);
        return new PlacesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlacesViewHolder viewHolder , int position) {
        viewHolder.updateWithPlaces(this.mRestaurant.get(position),mRestaurantListFromFirestore);
    }

    @Override
    public int getItemCount() {
        if (mRestaurant != null) {
            return mRestaurant.size();
        } else {
            return 0;
        }
    }

    /**
     * Update restaurant list
     * @param places
     * @param restaurantListFromFirestore
     */
    public void updatedPlaces(List<Results> places, List<Restaurant> restaurantListFromFirestore){
        this.mRestaurant = places;
        this.mRestaurantListFromFirestore = restaurantListFromFirestore;
        notifyDataSetChanged();
    }
}
