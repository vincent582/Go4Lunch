package com.pdv.go4lunch.ui.viewHolder;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseUser;
import com.pdv.go4lunch.Model.User;
import com.pdv.go4lunch.R;

public class UserRecyclerViewAdapter extends FirestoreRecyclerAdapter<User,UsersViewHolder> {

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.
     * See {@link FirestoreRecyclerOptions} for searchable options.
     * @param options
     */
    public UserRecyclerViewAdapter(FirestoreRecyclerOptions<User> options) {
        super(options);
    }

    @NonNull
    @Override
    public UsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_workmates_list,parent,false);
        return new UsersViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull UsersViewHolder usersViewHolder, int i, @NonNull User user) {
        usersViewHolder.updateWithUsers(user);
    }
}
