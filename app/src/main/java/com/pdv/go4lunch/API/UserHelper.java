package com.pdv.go4lunch.API;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.pdv.go4lunch.Model.User;

import java.util.HashMap;
import java.util.Map;

public class UserHelper {

    private static final String COLLECTION_NAME = "users";

    public static CollectionReference getUsersCollection(){
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    public static Task<Void> createUser(String Uid,String userName,String urlPictureUser){
        User userToCreate = new User(Uid,userName,urlPictureUser);
        return UserHelper.getUsersCollection().document(Uid).set(userToCreate);
    }

    public static Task<Void> createCustomUser(String Uid,String userName){
        User userToCreate = new User(Uid,userName);
        return UserHelper.getUsersCollection().document(Uid).set(userToCreate);
    }

    public static Task<DocumentSnapshot> getUser(String Uid){
        return UserHelper.getUsersCollection().document(Uid).get();
    }

    public static Task<Void> updateUserRestaurantNameAndId(String Uid,String restaurantId,String restaurantName){
        return UserHelper.getUsersCollection().document(Uid).update("restaurantId",restaurantId, "restaurantName",restaurantName);
    }

    public static Task<Void> deleteUser(String Uid){
        return UserHelper.getUsersCollection().document(Uid).delete();
    }

    public static Task<Void> deleteRestaurantFromUser(String Uid){
        DocumentReference docRef = UserHelper.getUsersCollection().document(Uid);
        // Remove the 'restaurant' field from the document
        Map<String,Object> updates = new HashMap<>();
        updates.put("restaurantId", FieldValue.delete());
        updates.put("restaurantName", FieldValue.delete());
        return docRef.update(updates);
    }

    public static Task<QuerySnapshot> getAllUsers(){
        return getUsersCollection().get();
    }

    public static Task<QuerySnapshot> getAllUserForRestaurant(String restaurantId){
        return UserHelper.getUsersCollection()
                .whereEqualTo("restaurantId",restaurantId).get();
    }
}
