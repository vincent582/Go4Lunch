package com.pdv.go4lunch.ui.viewHolder;

import android.location.Location;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.pdv.go4lunch.Model.Place.Result;
import com.pdv.go4lunch.R;
import com.pdv.go4lunch.ui.viewHolder.PlacesViewHolder;

import java.util.List;

public class PlacesRecyclerViewAdapter extends RecyclerView.Adapter<PlacesViewHolder> {

    private List<Result> mPlaces;
    private Location myLocation;

    public PlacesRecyclerViewAdapter(){}

    public void updatedPlaces(List<Result> places, Location myLocation){
        this.mPlaces = places;
        this.myLocation = myLocation;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PlacesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_restaurant,parent,false);
        return new PlacesViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull PlacesViewHolder viewHolder , int position) {
        viewHolder.updateWithPlaces(this.mPlaces.get(position),myLocation);
    }

    @Override
    public int getItemCount() {
        if (mPlaces != null) {
            return mPlaces.size();
        } else {
            return 0;
        }
    }

}