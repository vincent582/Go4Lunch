package com.pdv.go4lunch.ui.fragment;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pdv.go4lunch.Go4LunchApplication;
import com.pdv.go4lunch.Model.GooglePlacesApiModel.Results;
import com.pdv.go4lunch.Model.Place.Result;
import com.pdv.go4lunch.R;
import com.pdv.go4lunch.ui.ViewModel.PlacesViewModel;
import com.pdv.go4lunch.ui.viewHolder.PlacesRecyclerViewAdapter;
import com.pdv.go4lunch.utils.Permission;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ListViewFragment extends Fragment {

    @BindView(R.id.list_restaurant_recycler_view)
    RecyclerView mRecyclerView;

    private PlacesRecyclerViewAdapter adapter = new PlacesRecyclerViewAdapter();
    private PlacesViewModel mPlacesViewModel = new PlacesViewModel();
    private Location myLocation;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Permission.checkIfLocationPermissionGranted(getContext())){
            myLocation = ((Go4LunchApplication) getActivity().getApplication()).getMyLocation();
            Log.i("TAG", "Get location in ListView : "+ myLocation);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_view, container, false);
        ButterKnife.bind(this, view);

        mRecyclerView.setAdapter(adapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));

        mPlacesViewModel.getNearestPlaces(myLocation).observe(this, this::getAllPlaces);
        return view;
    }

    private void getAllPlaces(List<Results> results) {
        for (Results result: results) {
            mPlacesViewModel.getPlace(result.getPlaceId()).observe(this,this::getPlaceByIds);
        }
    }

    private void getPlaceByIds(List<Result> result) {
        adapter.updatedPlaces(result,myLocation);
    }
}
