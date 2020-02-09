package com.pdv.go4lunch.API;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.pdv.go4lunch.Model.Restaurant;
import com.pdv.go4lunch.Model.User;

public class RestaurantHelper {

    public static final String COLLECTION_NAME = "restaurant";

    public static CollectionReference getRestaurantsCollection(){
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    public static Task<Void> createRestaurant(String id,String name){
        Restaurant restaurantToCreate = new Restaurant(name,id);
        return RestaurantHelper.getRestaurantsCollection().document(id).set(restaurantToCreate);
    }

    public static Task<DocumentSnapshot> getRestaurant(String Uid){
        return RestaurantHelper.getRestaurantsCollection().document(Uid).get();
    }

    public static Task<Void> updateRestaurantNbrPeople(String Uid,int nbrPeople){
        return RestaurantHelper.getRestaurantsCollection().document(Uid).update("nbrPeopleEatingHere",nbrPeople);
    }

    public static Task<Void> updateRestaurantLikes(String Uid,int likes){
        return RestaurantHelper.getRestaurantsCollection().document(Uid).update("likes",likes);
    }
}
