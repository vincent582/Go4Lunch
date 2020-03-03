package com.pdv.go4lunch.ui.ViewModel;

import android.app.Activity;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.pdv.go4lunch.Model.Place.Result;
import com.pdv.go4lunch.repository.PlacesRepository;

public class DetailsViewModel extends ViewModel {

    private PlacesRepository mPlacesRepository;

    public void init(Activity activity){
        mPlacesRepository = PlacesRepository.getInstance(activity);
    }

    public MutableLiveData<Result> getRestaurantDetails(String id){
        return mPlacesRepository.getRestaurantDetails(id);
    }
}
