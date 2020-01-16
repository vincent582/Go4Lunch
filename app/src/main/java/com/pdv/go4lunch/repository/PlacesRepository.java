package com.pdv.go4lunch.repository;

import android.location.Location;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.pdv.go4lunch.API.GoogleApiService;
import com.pdv.go4lunch.Model.GooglePlacesApiModel.GooglePlaces;
import com.pdv.go4lunch.Model.GooglePlacesApiModel.Results;
import com.pdv.go4lunch.Model.Place.Place;
import com.pdv.go4lunch.Model.Place.Result;
import com.pdv.go4lunch.utils.RetrofitInstance;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlacesRepository {

    private String location = "48.8566,2.3522";
    private String radius = "1500";
    private String type = "restaurant";
    private String key = "AIzaSyDGFBPIUVLpd36GZCrt1LQVL4zCaSbMzxU";

    private GoogleApiService mGoogleApiService = RetrofitInstance.getGoogleService();
    private MutableLiveData<List<Results>> nearestPlaces = new MutableLiveData<>();
    private MutableLiveData<List<Result>> mMutableLiveDataPlaces = new MutableLiveData<>();
    private ArrayList<Result> mPlace = new ArrayList<>();

    public MutableLiveData<List<Results>> getNearestPlaces(Location location) {
        String locationToString = location.getLatitude()+","+location.getLongitude();
        Call<GooglePlaces> call = mGoogleApiService.getNearestPlaces(locationToString,radius,type,key);
        call.enqueue(new Callback<GooglePlaces>() {
            @Override
            public void onResponse(Call<GooglePlaces> call, Response<GooglePlaces> response) {
                GooglePlaces googlePlaces = response.body();
                if (googlePlaces != null || googlePlaces.getResults() != null){
                    nearestPlaces.setValue(googlePlaces.getResults());
                }
            }
            @Override
            public void onFailure(Call<GooglePlaces> call, Throwable t) {
            }
        });
        return nearestPlaces;
    }

    public MutableLiveData<List<Result>> getPlace(String id){
        Call<Place> call = mGoogleApiService.getPlace(id,key);
        call.enqueue(new Callback<Place>() {
            @Override
            public void onResponse(Call<Place> call, Response<Place> response) {
                Place place = response.body();
                if (place != null){
                    mPlace.add(response.body().getResult());
                    mMutableLiveDataPlaces.setValue(mPlace);
                }
            }
            @Override
            public void onFailure(Call<Place> call, Throwable t) {
            }
        });
        return mMutableLiveDataPlaces;
    }
}
