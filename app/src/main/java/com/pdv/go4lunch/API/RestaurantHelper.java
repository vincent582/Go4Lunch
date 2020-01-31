package com.pdv.go4lunch.API;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.pdv.go4lunch.Model.Restaurant;

public class RestaurantHelper {

    public static final String COLLECTION_NAME = "restaurant";

    public static CollectionReference getRestaurantsCollection(){
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    public static Task<Void> createRestaurant(String name,String id,Boolean someoneEatingHere){
        Restaurant restaurantToCreate = new Restaurant(name,id,someoneEatingHere);
        return RestaurantHelper.getRestaurantsCollection().document(id).set(restaurantToCreate);
    }

    public static Task<DocumentSnapshot> getRestaurant(String Uid){
        return RestaurantHelper.getRestaurantsCollection().document(Uid).get();
    }

    public static Task<Void> updateRestaurantName(String name,String Uid){
        return RestaurantHelper.getRestaurantsCollection().document(Uid).update("name",name);
    }

    public static Task<Void> updateRestaurantRate(String Uid,int rate){
        return RestaurantHelper.getRestaurantsCollection().document(Uid).update("rate",rate);
    }

    public static Task<Void> deleteRestaurant(String Uid){
        return RestaurantHelper.getRestaurantsCollection().document(Uid).delete();
    }

    public static Query getAllRestaurant(){
        return RestaurantHelper.getRestaurantsCollection();
    }

}
