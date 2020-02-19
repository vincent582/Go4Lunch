package com.pdv.go4lunch.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.Query;
import com.pdv.go4lunch.API.UserHelper;
import com.pdv.go4lunch.Model.User;
import com.pdv.go4lunch.R;
import com.pdv.go4lunch.ui.ViewModel.UsersFirestoreViewModel;
import com.pdv.go4lunch.ui.viewHolder.UserRecyclerViewAdapter;
import com.pdv.go4lunch.utils.Utils;

import java.util.List;

import static com.pdv.go4lunch.utils.Utils.getCurrentUser;

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

        UsersFirestoreViewModel usersFirestoreViewModel = ViewModelProviders.of(getActivity()).get(UsersFirestoreViewModel.class);
        usersFirestoreViewModel.getListUsersInFirestore().observe(this,this::updateUsersForRecyclerView);

        RecyclerView mRecyclerView = view.findViewById(R.id.list_works_mates_recycler_view);
        adapter = new UserRecyclerViewAdapter();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(adapter);
        return view;
    }

    private void updateUsersForRecyclerView(List<User> userList) {
        adapter.updateUsers(userList);
    }
}
