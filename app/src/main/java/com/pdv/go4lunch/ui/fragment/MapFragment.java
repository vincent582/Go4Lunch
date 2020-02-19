package com.pdv.go4lunch.ui.fragment;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
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

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.pdv.go4lunch.Go4LunchApplication;
import com.pdv.go4lunch.Model.AutoComplete.Prediction;
import com.pdv.go4lunch.Model.GooglePlacesApiModel.Results;
import com.pdv.go4lunch.Model.Restaurant;
import com.pdv.go4lunch.R;
import com.pdv.go4lunch.ui.ViewModel.RestaurantFirestoreViewModel;
import com.pdv.go4lunch.ui.ViewModel.PlacesViewModel;
import com.pdv.go4lunch.ui.activities.DetailsActivity;
import com.pdv.go4lunch.ui.activities.MainActivity;
import com.pdv.go4lunch.utils.Permission;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    public static final String PLACE_ID = "PLACE_ID";

    private GoogleMap mMap;
    private LatLng mDefaultLocation = new LatLng(48.8534,2.3488);
    private int DEFAULT_ZOOM = 10;
    private Location myLocation;

    //For DATA
    private PlacesViewModel mPlacesViewModel;
    private RestaurantFirestoreViewModel mFirestoreViewModel;
    private List<Results> mPlacesFromApi;
    private List<Restaurant> mRestaurantListSavedInFireStore;

    //For Token
    private String mSessionToken = "12345";

    //For Localisation
    private FusedLocationProviderClient mFusedLocationProviderClient;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mPlacesViewModel = ViewModelProviders.of(getActivity()).get(PlacesViewModel.class);
        mFirestoreViewModel = ViewModelProviders.of(getActivity()).get(RestaurantFirestoreViewModel.class);
        mFirestoreViewModel.getListRestaurantInFirestore().observe(this, restaurantList -> mRestaurantListSavedInFireStore = restaurantList);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        if (mMap == null) {
            initMap();
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (myLocation != null) {
            putMarkerOnMap(mPlacesFromApi);
        }
    }

    /**
     * Inflate Menu, setUp searchView item.
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
                    putMarkerOnMap(mPlacesFromApi);
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
            for (Results results: mPlacesFromApi) {
                if (results.getPlaceId().equals(prediction.getPlaceId())){
                    resultsList.add(results);
                }
            }
        }
        putMarkerOnMap(resultsList);
    }

    /**
     * Initialise map
     */
    private void initMap() {
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getContext(),R.raw.style_map_json));
        getLocation();
    }

    /**
     * Get Location of the user if permission granted and save in application and request places.
     * Else ask request permission.
     */
    private void getLocation() {
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        if (Permission.checkIfLocationPermissionGranted(getContext())) {
            mFusedLocationProviderClient.getLastLocation().addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    myLocation = location;
                    ((Go4LunchApplication) getActivity().getApplication()).setMyLocation(location);
                    mPlacesViewModel.init(location, (MainActivity) getActivity());
                    updateLocationUI();
                }
            });
        } else {
            Permission.requestLocationPermissions(getActivity());
        }
    }

    /**
     * Update Location of user on map.
     */
    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (myLocation != null) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                        new LatLng(myLocation.getLatitude(),
                                myLocation.getLongitude()), 17));
                getNearestPlaces();
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    /**
     * get nearest places from API.
     */
    private void getNearestPlaces() {
        mPlacesViewModel.getListNearestRestaurants().observe(this, this::updateNearestPlaces);
    }

    /**
     * Save result in argument.
     * @param results
     */
    private void updateNearestPlaces(List<Results> results) {
        mPlacesFromApi = results;
        putMarkerOnMap(mPlacesFromApi);
    }

    /**
     * Add Marker on map for each places.
     * @param results
     */
    private void putMarkerOnMap(List<Results> results) {
        mMap.clear();
        for (Results place: results) {
            LatLng latLng = new LatLng(place.getGeometry().getLocation().getLat(), place.getGeometry().getLocation().getLng());
            MarkerOptions markerOptions = new MarkerOptions().position(latLng);
            if (checkInFirestore(place)){
                markerOptions.icon((BitmapDescriptorFactory.fromResource(R.drawable.icon_restaurant_green)));
            }else {
                markerOptions.icon((BitmapDescriptorFactory.fromResource(R.drawable.icon_restaurant_red)));
            }
            Marker m = mMap.addMarker(markerOptions);
            m.setTag(place.getPlaceId());

            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    startDetailsActivity(marker);
                    return false;
                }
            });
        }
    }

    /**
     * Check in firestore if someone eating in this place.
     * @param place
     * @return
     */
    private boolean checkInFirestore(Results place) {
        Boolean b = false;
        for (Restaurant restaurant: mRestaurantListSavedInFireStore) {
            if (restaurant.getId().equals(place.getPlaceId()) && restaurant.getNbrPeopleEatingHere() > 0){
                b = true;
            }
        }
        return b;
    }

    /**
     * Start details activity on click on marker.
     * @param marker
     */
    private void startDetailsActivity(Marker marker) {
        Intent intent = new Intent(getContext(), DetailsActivity.class);
        intent.putExtra(PLACE_ID, String.valueOf(marker.getTag()));
        getContext().startActivity(intent);
    }
}