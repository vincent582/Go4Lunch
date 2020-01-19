package com.pdv.go4lunch.API;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.pdv.go4lunch.Model.User;

public class UserHelper {

    private static final String COLLECTION_NAME = "users";

    public static CollectionReference getUsersCollection(){
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    public static Task<Void> createUser(String Uid,String userName,String urlPictureUser){
        User userToCreate = new User(Uid,userName,urlPictureUser);
        return UserHelper.getUsersCollection().document(Uid).set(userToCreate);
    }

    public static Task<DocumentSnapshot> getUser(String Uid){
        return UserHelper.getUsersCollection().document(Uid).get();
    }

    public static Task<Void> updateUserAge(int age,String Uid){
        return UserHelper.getUsersCollection().document(Uid).update("age",age);
    }

    public static Task<Void> deleteUser(String Uid){
        return UserHelper.getUsersCollection().document(Uid).delete();
    }

    public static Query getAllUsers(){
        return UserHelper.getUsersCollection();
    }
}
