package com.pdv.go4lunch.ui.ViewModel;

import android.location.Location;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.pdv.go4lunch.Model.AutoComplete.Prediction;
import com.pdv.go4lunch.Model.GooglePlacesApiModel.Results;
import com.pdv.go4lunch.Model.Place.Result;
import com.pdv.go4lunch.repository.PlacesRepository;

import java.util.List;

public class PlacesViewModel extends ViewModel {

    private PlacesRepository mPlacesRepository;

    private MutableLiveData<List<Results>> mListNearestRestaurants;

    public PlacesViewModel() {}

    public void init(Location location){
        if (mListNearestRestaurants != null){
            return;
        }
        mPlacesRepository = PlacesRepository.getInstance();
        mListNearestRestaurants = mPlacesRepository.getNearestRestaurants(location);
    }

    public MutableLiveData<List<Results>> getListNearestRestaurants(){
            return mListNearestRestaurants;
    }

    public MutableLiveData<Result> getRestaurantDetails(String id){
        return PlacesRepository.getInstance().getRestaurantDetails(id);
    }

    public MutableLiveData<List<Prediction>> getPredictionAutoComplete(String input, Location location, String sessionToken){
        return mPlacesRepository.getAutoCompleteRequest(input,location,sessionToken);
    }
}
