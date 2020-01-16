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
        mPlacesRepository = new PlacesRepository();
    }

    public MutableLiveData<List<Results>> getNearestPlaces(Location myLocation){
        return mPlacesRepository.getNearestPlaces(myLocation);
    }

    public MutableLiveData<List<Result>> getPlace(String id){
        return mPlacesRepository.getPlace(id);
    }
}
