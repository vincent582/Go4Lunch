package com.pdv.go4lunch.ui.ViewModel;

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

    public MutableLiveData<List<Results>> getNearestPlaces(){
        return mPlacesRepository.getNearestPlaces();
    }

    public MutableLiveData<List<Result>> getPlace(String id){
        return mPlacesRepository.getPlace(id);
    }
}
