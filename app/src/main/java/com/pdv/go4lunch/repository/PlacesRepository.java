package com.pdv.go4lunch.repository;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.pdv.go4lunch.API.GoogleApiService;
import com.pdv.go4lunch.BuildConfig;
import com.pdv.go4lunch.Model.AutoComplete.AutoComplete;
import com.pdv.go4lunch.Model.AutoComplete.Prediction;
import com.pdv.go4lunch.Model.GooglePlacesApiModel.GooglePlaces;
import com.pdv.go4lunch.Model.GooglePlacesApiModel.Results;
import com.pdv.go4lunch.Model.Place.Place;
import com.pdv.go4lunch.Model.Place.Result;
import com.pdv.go4lunch.utils.RetrofitInstance;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.pdv.go4lunch.ui.fragment.SettingsFragment.KEY_PREFERENCES_DISTANCE;

public class PlacesRepository {

    private SharedPreferences mSharedPreferences;

    private String type = "restaurant";
    private String key = BuildConfig.GOOGLE_API_KEY;

    private int callApi = 1;

    //FOR DATA
    private MutableLiveData<List<Results>> mNearestPlaces = new MutableLiveData<>();
    private MutableLiveData<Result> mPlaceDetails = new MutableLiveData<>();
    private MutableLiveData<List<Prediction>> mAutoCompleteMutableLiveData = new MutableLiveData<List<com.pdv.go4lunch.Model.AutoComplete.Prediction>>();

    private static PlacesRepository mPlacesRepository;
    private GoogleApiService mGoogleApiService;

    //REPOSITORY INSTANCE
    public static PlacesRepository getInstance(Activity activity){
        if (mPlacesRepository == null){
            mPlacesRepository = new PlacesRepository(activity);
        }
        return mPlacesRepository;
    }

    //CONSTRUCTOR
    public PlacesRepository(Activity activity) {
        mGoogleApiService = RetrofitInstance.getGoogleService();
        mSharedPreferences = activity.getPreferences(Context.MODE_PRIVATE);
    }

    /**
     * Get nearest restaurant from Google Places API
     * @param location
     * @return
     */
    public MutableLiveData<List<Results>> getNearestRestaurants(Location location) {
        int radius = mSharedPreferences.getInt(KEY_PREFERENCES_DISTANCE,100);
        String locationToString = location.getLatitude()+","+location.getLongitude();
        Call<GooglePlaces> call = mGoogleApiService.getNearestPlaces(locationToString,String.valueOf(radius),type,key);
        call.enqueue(new Callback<GooglePlaces>() {
            @Override
            public void onResponse(Call<GooglePlaces> call, Response<GooglePlaces> response) {
                GooglePlaces googlePlaces = response.body();
                if (googlePlaces != null || googlePlaces.getResults() != null){
                    mNearestPlaces.setValue(googlePlaces.getResults());
                    Log.e("TAG", "Call Api from repository (getNearestPlaces): "+ callApi++);
                }
            }
            @Override
            public void onFailure(Call<GooglePlaces> call, Throwable t) {
                mNearestPlaces.setValue(null);
            }
        });
        return mNearestPlaces;
    }

    /**
     * Get the restaurant details from an ID in Google Places API.
     * @param id
     * @return
     */
    public MutableLiveData<Result> getRestaurantDetails(String id) {
        Call<Place> call = mGoogleApiService.getPlace(id,key);
        call.enqueue(new Callback<Place>() {
            @Override
            public void onResponse(Call<Place> call, Response<Place> response) {
                mPlaceDetails.setValue(response.body().getResult());
            }
            @Override
            public void onFailure(Call<Place> call, Throwable t) {
                mPlaceDetails = null;
            }
        });
        Log.e("TAG", "Call Api from repository (getDetailsRestaurant): "+ callApi++);
        return mPlaceDetails;
    }


    /**
     * Get Places around with autocomplete
     * @param input
     * @param location
     * @param sessionToken
     * @return
     */
    public MutableLiveData<List<Prediction>> getAutoCompleteRequest(String input, Location location, String sessionToken){
        int radius = mSharedPreferences.getInt(KEY_PREFERENCES_DISTANCE,100);
        String locationToString = location.getLatitude()+","+location.getLongitude();
        Call<AutoComplete> call = mGoogleApiService.getAutoCompleteRequest(input,String.valueOf(radius),locationToString,key,sessionToken);
        call.enqueue(new Callback<AutoComplete>() {
            @Override
            public void onResponse(Call<AutoComplete> call, Response<AutoComplete> response) {
                mAutoCompleteMutableLiveData.setValue(response.body().getPredictions());
            }
            @Override
            public void onFailure(Call<AutoComplete> call, Throwable t) {
            }
        });

        return mAutoCompleteMutableLiveData;
    }
}