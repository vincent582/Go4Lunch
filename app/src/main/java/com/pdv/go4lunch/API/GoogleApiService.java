package com.pdv.go4lunch.API;

import com.pdv.go4lunch.Model.GooglePlacesApiModel.GooglePlaces;
import com.pdv.go4lunch.Model.Place.Place;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GoogleApiService {

    @GET("maps/api/place/nearbysearch/json")
    Call<GooglePlaces> getNearestPlaces(
            @Query("location") String location,
            @Query("radius") String radius,
            @Query("type") String type,
            @Query("key") String key);

    @GET("maps/api/place/details/json?&fields=name,rating,formatted_phone_number,formatted_address,vicinity,types,photos")
    Call<Place> getPlace(
            @Query("place_id") String id,
            @Query("key") String key);

    @GET("maps/api/place/photo?maxwidth=400")
    Call<ResponseBody> getPhoto(
            @Query("photoreference") String photoreference,
            @Query("key") String key);

}

