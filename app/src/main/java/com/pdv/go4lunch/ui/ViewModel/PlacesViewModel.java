package com.pdv.go4lunch.ui.ViewModel;

import android.location.Location;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.pdv.go4lunch.Model.GooglePlacesApiModel.Results;
import com.pdv.go4lunch.Model.Place.Result;
import com.pdv.go4lunch.repository.PlacesRepository;

import java.util.List;

public class PlacesViewModel extends ViewModel {

    private PlacesRepository mPlacesRepository;

    public PlacesViewModel() {
        mPlacesRepository = PlacesRepository.getInstance();
    }

    public MutableLiveData<List<Results>> getNearestPlaces(Location location){
        return mPlacesRepository.getNearestRestaurants(location);
    }

    public MutableLiveData<Result> getPlace(String id){
        return mPlacesRepository.getRestaurantDetails(id);
    }


}
