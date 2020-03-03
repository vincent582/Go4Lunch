package com.pdv.go4lunch.ui.ViewModel;

import android.app.Activity;
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

    public void init(Location location, Activity activity){
        if (mListNearestRestaurants != null){
            return;
        }
        mPlacesRepository = PlacesRepository.getInstance(activity);
        mListNearestRestaurants = mPlacesRepository.getNearestRestaurants(location);
    }

    public void refreshListNearestRestaurants(Location location){
        mListNearestRestaurants = mPlacesRepository.getNearestRestaurants(location);
    }

    public MutableLiveData<List<Results>> getListNearestRestaurants(){
            return mListNearestRestaurants;
    }

    public MutableLiveData<List<Prediction>> getPredictionAutoComplete(String input, Location location, String sessionToken){
        return mPlacesRepository.getAutoCompleteRequest(input,location,sessionToken);
    }
}
