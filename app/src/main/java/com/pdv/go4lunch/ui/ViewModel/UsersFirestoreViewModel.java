package com.pdv.go4lunch.ui.ViewModel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.pdv.go4lunch.API.UserHelper;
import com.pdv.go4lunch.Model.User;
import com.pdv.go4lunch.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class UsersFirestoreViewModel extends ViewModel {

    private MutableLiveData<List<User>> mListUsersInFirestore = new MutableLiveData<>();
    private MutableLiveData<List<User>> mListUsersForRestaurant = new MutableLiveData<>();
    private FirebaseUser currentUser;

    public void init(){
        currentUser = Utils.getCurrentUser();
        getAllUsersInFirestore();
    }

    private void getAllUsersInFirestore (){
        UserHelper.getAllUsers().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<User> users = new ArrayList<>();
                for (QueryDocumentSnapshot document :queryDocumentSnapshots){
                    User usersItem = document.toObject(User.class);
                    if (!usersItem.getId().equals(currentUser.getUid())) {
                        users.add(usersItem);
                    }
                }
                mListUsersInFirestore.setValue(users);
            }
        });
    }

    public MutableLiveData<List<User>> getListUsersInFirestore(){
        return mListUsersInFirestore;
    }

    public MutableLiveData<List<User>> getUsersForRestaurant(String restaurantId){
        UserHelper.getAllUserForRestaurant(restaurantId).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<User> users = new ArrayList<>();
                for (QueryDocumentSnapshot document: queryDocumentSnapshots) {
                    User user = document.toObject(User.class);
                    users.add(user);
                }
                mListUsersForRestaurant.setValue(users);
            }
        });
        return mListUsersForRestaurant;
    }
}
