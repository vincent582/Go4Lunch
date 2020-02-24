package com.pdv.go4lunch.ui.fragment;

import android.app.SearchManager;
import android.content.Context;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.pdv.go4lunch.Go4LunchApplication;
import com.pdv.go4lunch.Model.AutoComplete.Prediction;
import com.pdv.go4lunch.Model.GooglePlacesApiModel.Results;
import com.pdv.go4lunch.Model.Restaurant;
import com.pdv.go4lunch.R;
import com.pdv.go4lunch.ui.ViewModel.RestaurantFirestoreViewModel;
import com.pdv.go4lunch.ui.ViewModel.PlacesViewModel;
import com.pdv.go4lunch.ui.activities.MainActivity;
import com.pdv.go4lunch.ui.viewHolder.PlacesRecyclerViewAdapter;
import com.pdv.go4lunch.utils.Permission;
import com.pdv.go4lunch.utils.Utils;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ListViewFragment extends Fragment {

    //FOR UI
    @BindView(R.id.list_restaurant_recycler_view)
    RecyclerView mRecyclerView;

    //FOR DATA
    private PlacesRecyclerViewAdapter adapter = new PlacesRecyclerViewAdapter();
    private RestaurantFirestoreViewModel mFirestoreViewModel;
    private PlacesViewModel mPlacesViewModel;
    private Location myLocation;
    private List<Restaurant> mRestaurantListSavedInFirestore = new ArrayList<>();
    private List<Results> mListNearestRestaurantFromApi = new ArrayList<>();

    //For Token
    private String mSessionToken = "12345";

    /**
     * Get Location if permission granted
     * Init viewModels
     * @param savedInstanceState
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Permission.checkIfLocationPermissionGranted(getContext())){
            myLocation = ((Go4LunchApplication) getActivity().getApplication()).getMyLocation();
        }
        mPlacesViewModel = ViewModelProviders.of(getActivity()).get(PlacesViewModel.class);
        mFirestoreViewModel = ViewModelProviders.of(getActivity()).get(RestaurantFirestoreViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_view, container, false);
        ButterKnife.bind(this, view);
        setHasOptionsMenu(true);

        if (Permission.checkIfLocationPermissionGranted(getContext())) {
            configureRecyclerView();
            mPlacesViewModel.getListNearestRestaurants().observe(this,this::setDistanceBetweenRestaurantAndSortByNearest);
        } else {
            Permission.requestLocationPermissions(getActivity());
        }
        return view;
    }

    /**
     * ConfigureRecyclerView with PlacesRecyclerViewAdapter
     */
    private void configureRecyclerView() {
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
    }

    /**
     * Inflate Menu, setUp searchView item for autocomplete search.
     * @param menu
     * @param inflater
     */
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_toolbar, menu);

        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.app_bar_search).getActionView();
        searchView.setIconifiedByDefault(false);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.e("TAG", "onQueryTextSubmit: "+query );
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                Log.e("TAG", "onQueryTextSubmit: "+newText );
                if (newText.isEmpty()){
                    adapter.updatedPlaces(mListNearestRestaurantFromApi, mRestaurantListSavedInFirestore);
                }
                else{
                    getPredictionAutocomplete(newText);
                }
                return false;
            }
        });
    }

    /**
     * Get Autocomplete result from API.
     * @param newText
     */
    private void getPredictionAutocomplete(String newText) {
        mPlacesViewModel.getPredictionAutoComplete(newText,myLocation,mSessionToken).observe(this, this::getPrediction);
    }

    /**
     * Compare prediction received with restaurant and update view adapter.
     * @param predictions
     */
    private void getPrediction(List<Prediction> predictions) {
        List<Results> resultsList = new ArrayList<>();
        resultsList.clear();
        for (Prediction prediction: predictions) {
            for (Results results: mListNearestRestaurantFromApi) {
                if (results.getPlaceId().equals(prediction.getPlaceId())){
                    resultsList.add(results);
                }
            }
        }
        adapter.updatedPlaces(resultsList, mRestaurantListSavedInFirestore);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUi();
    }

    /**
     * Get list nearest restaurant from Api
     * Set distance to each restaurant and sort by ascending
     * then get list of restaurant in Firestore
     */
    private void setDistanceBetweenRestaurantAndSortByNearest(List<Results> results) {
        mListNearestRestaurantFromApi.addAll(results);
        for (Results restaurant: mListNearestRestaurantFromApi) {
            Location placeLocation = new Location("Location");
            placeLocation.setLatitude(restaurant.getGeometry().getLocation().getLat());
            placeLocation.setLongitude(restaurant.getGeometry().getLocation().getLng());
            restaurant.setDistance(Utils.getDistanceBetweenLocation(myLocation,placeLocation));
        }
        Utils.sortByDistance(mListNearestRestaurantFromApi);

        mFirestoreViewModel.getListRestaurantInFirestore().observe(this,this::getRestaurantFromFS);
    }

    private void getRestaurantFromFS(List<Restaurant> restaurants) {
        mRestaurantListSavedInFirestore = restaurants;
        updateUi();
    }

    /**
     * Pass both restaurant list to the adapter
     */
    private void updateUi(){
        adapter.updatedPlaces(mListNearestRestaurantFromApi, mRestaurantListSavedInFirestore);
    }
}
