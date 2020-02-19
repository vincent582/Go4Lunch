package com.pdv.go4lunch.ui.ViewModel;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.pdv.go4lunch.API.RestaurantHelper;
import com.pdv.go4lunch.API.UserHelper;
import com.pdv.go4lunch.Model.Restaurant;

import java.util.ArrayList;
import java.util.List;

public class RestaurantFirestoreViewModel extends ViewModel {

    private MutableLiveData<List<Restaurant>> mListRestaurantInFirestore = new MutableLiveData<>();

    public void init(){
        getAllRestaurantInFirestore();
    }

    /**
     * get All restaurant saved in firestore
     * @return
     */
    private void getAllRestaurantInFirestore(){
        RestaurantHelper.getRestaurantsCollection().addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.e("TAG", "Listen failed.", e);
                    mListRestaurantInFirestore = null;
                    return;
                }
                List<Restaurant> restaurants = new ArrayList<>();
                for (QueryDocumentSnapshot document :queryDocumentSnapshots){
                    Restaurant restaurantItem = document.toObject(Restaurant.class);
                    restaurants.add(restaurantItem);
                }
                mListRestaurantInFirestore.setValue(restaurants);
            }
        });
    }

    public MutableLiveData<List<Restaurant>> getListRestaurantInFirestore(){
        return mListRestaurantInFirestore;
    }

    /**
     * Get one restaurant from firestore
     * @param Uid
     * @param activity
     * @return
     */
    public Task<DocumentSnapshot> getRestaurant(String Uid , Activity activity){
        return RestaurantHelper.getRestaurant(Uid);
    }

    /**
     * Update nbr of people for each restaurant saved in firestore
     * to do it get all user going to this restaurant.
     * @param activity
     */
    public void updateNbrOfPeopleForeachRestaurant(Activity activity){
        RestaurantHelper.getRestaurantsCollection().addSnapshotListener(activity, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                for (QueryDocumentSnapshot doc: queryDocumentSnapshots) {
                    Restaurant restaurant = doc.toObject(Restaurant.class);
                    UserHelper.getAllUserForRestaurant(restaurant.getId()).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            RestaurantHelper.updateRestaurantNbrPeople(restaurant.getId(),queryDocumentSnapshots.size());
                        }
                    });
                }
            }
        });
    }

    /**
     * add like to a restaurant
     * @param restaurantId
     * @param likes
     */
    public void addLikes(String restaurantId,int likes){
        RestaurantHelper.updateRestaurantLikes(restaurantId,likes);
    }
}
