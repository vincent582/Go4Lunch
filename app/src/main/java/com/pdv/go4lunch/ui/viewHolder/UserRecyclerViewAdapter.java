package com.pdv.go4lunch.ui.viewHolder;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseUser;
import com.pdv.go4lunch.Model.User;
import com.pdv.go4lunch.R;

import java.util.List;

public class UserRecyclerViewAdapter extends RecyclerView.Adapter<UsersViewHolder> {

    private List<User> mUserList;

    public UserRecyclerViewAdapter(){}

    @NonNull
    @Override
    public UsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_workmates_list,parent,false);
        return new UsersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UsersViewHolder holder, int position) {
        holder.updateWithUsers(this.mUserList.get(position));
    }

    @Override
    public int getItemCount() {
        if (mUserList != null) {
            return mUserList.size();
        } else {
            return 0;
        }
    }

    public void updateUsers(List<User> userList){
        this.mUserList = userList;
        notifyDataSetChanged();
    }
}
