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
    private MutableLiveData<List<Results>> nearestPlaces;

    public PlacesViewModel() {
        mPlacesRepository = PlacesRepository.getInstance();
    }

    public void init(Location myLocation){
        if (nearestPlaces != null) {
            return;
        }
        nearestPlaces = mPlacesRepository.getNearestPlaces(myLocation);
    }

    public MutableLiveData<List<Results>> getNearestPlaces(){
        return nearestPlaces;
    }

    public MutableLiveData<Result> getPlace(String id){
        return mPlacesRepository.getPlace(id);
    }
}
