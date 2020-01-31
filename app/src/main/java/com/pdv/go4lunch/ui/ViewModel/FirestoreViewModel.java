package com.pdv.go4lunch.ui.ViewModel;

import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.pdv.go4lunch.API.RestaurantHelper;
import com.pdv.go4lunch.Model.Restaurant;

import java.util.ArrayList;
import java.util.List;

public class FirestoreViewModel extends ViewModel {

    private String TAG = "FIRESTORE_VIEW_MODEL";
    private MutableLiveData<List<Restaurant>> mListRestaurant = new MutableLiveData<>();

    public MutableLiveData<List<Restaurant>> getAllRestaurantInFirestore(){
        RestaurantHelper.getAllRestaurant().addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.e(TAG, "Listen failed.", e);
                    mListRestaurant = null;
                    return;
                }
                List<Restaurant> restaurants = new ArrayList<>();
                for (QueryDocumentSnapshot document :queryDocumentSnapshots){
                    Restaurant restaurantItem = document.toObject(Restaurant.class);
                    restaurants.add(restaurantItem);
                }

                mListRestaurant.setValue(restaurants);
            }
        });
        return mListRestaurant;
    }
}
