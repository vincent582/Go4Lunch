package com.pdv.go4lunch.API;

import com.pdv.go4lunch.Model.AutoComplete.AutoComplete;
import com.pdv.go4lunch.Model.GooglePlacesApiModel.GooglePlaces;
import com.pdv.go4lunch.Model.Place.Place;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GoogleApiService {

    @GET("maps/api/place/nearbysearch/json?")
    Call<GooglePlaces> getNearestPlaces(
            @Query("location") String location,
            @Query("radius") String radius,
            @Query("type") String type,
            @Query("key") String key);

    //https://maps.googleapis.com/maps/api/place/details/json?&fields=name,rating,formatted_phone_number,icon,geometry,vicinity,types&place_id=ChIJcc0bfjDsGGARhGn7xgEYgcs&key=AIzaSyDGFBPIUVLpd36GZCrt1LQVL4zCaSbMzxU
    @GET("maps/api/place/details/json?&fields=place_id,name,geometry,formatted_phone_number,formatted_address,vicinity,photos,website,opening_hours")
    Call<Place> getPlace(
            @Query("place_id") String id,
            @Query("key") String key);


    //AUTOCOMPLETE
    //https://maps.googleapis.com/maps/api/place/autocomplete/json?input=Jo&types=establishment&location=35.7604138,139.6201991&radius=500&key=AIzaSyDGFBPIUVLpd36GZCrt1LQVL4zCaSbMzxU&sessiontoken=122333
    @GET("maps/api/place/autocomplete/json?types=establishment&strictbounds")
    Call<AutoComplete> getAutoCompleteRequest(
      @Query("input") String input,
      @Query("radius") String radius,
      @Query("location") String location,
      @Query("key") String key,
      @Query("sessiontoken") String sessiontoken
    );
}

