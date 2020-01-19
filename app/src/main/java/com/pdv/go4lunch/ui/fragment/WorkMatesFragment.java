package com.pdv.go4lunch.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;
import com.pdv.go4lunch.API.UserHelper;
import com.pdv.go4lunch.Model.User;
import com.pdv.go4lunch.R;
import com.pdv.go4lunch.ui.viewHolder.UserRecyclerViewAdapter;


public class WorkMatesFragment extends Fragment{

    private UserRecyclerViewAdapter adapter;

    public WorkMatesFragment() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_work_mates, container, false);

        RecyclerView mRecyclerView = view.findViewById(R.id.list_works_mates_recycler_view);

        adapter = new UserRecyclerViewAdapter(generateOptionForAdapter(UserHelper.getAllUsers()));

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(adapter);

        return view;
    }

    private FirestoreRecyclerOptions<User> generateOptionForAdapter(Query query) {
        return new FirestoreRecyclerOptions.Builder<User>()
                .setQuery(query,User.class)
                .setLifecycleOwner(this)
                .build();
    }
}
