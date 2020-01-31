package com.pdv.go4lunch.ui.fragment;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.pdv.go4lunch.API.RestaurantHelper;
import com.pdv.go4lunch.Go4LunchApplication;
import com.pdv.go4lunch.Model.Place.Result;
import com.pdv.go4lunch.Model.Restaurant;
import com.pdv.go4lunch.R;
import com.pdv.go4lunch.ui.ViewModel.FirestoreViewModel;
import com.pdv.go4lunch.ui.activities.DetailsActivity;
import com.pdv.go4lunch.utils.Permission;

import java.util.ArrayList;
import java.util.List;

import static com.pdv.go4lunch.ui.activities.DetailsActivity.DETAILS_PLACES;
import static com.pdv.go4lunch.ui.activities.MainActivity.BUNDLE_PLACES;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Location myLocation;
    private int DEFAULT_ZOOM = 17;

    private FirestoreViewModel mFirestoreViewModel;

    private List<Result> mRestaurants;
    private List<Restaurant> mRestaurantsListFromFirestore = new ArrayList<>();

    /**
     * Get Arguments Bundle from activity.
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        mFirestoreViewModel = ViewModelProviders.of(this).get(FirestoreViewModel.class);
        mFirestoreViewModel.getAllRestaurantInFirestore().observe(this,this::getRestaurantInFirestore);

        if (getArguments() != null)
        {
            Log.e("TAG", "onCreateView map fragment: " + getArguments().getParcelableArrayList(BUNDLE_PLACES));
            mRestaurants = getArguments().getParcelableArrayList(BUNDLE_PLACES);
        }

        initMap();
        return view;
    }

    private void getRestaurantInFirestore(List<Restaurant> restaurants) {
        mRestaurantsListFromFirestore = restaurants;
    }


    /**
     * onResume get myLocation from application
     */
    @Override
    public void onResume() {
        super.onResume();
        myLocation = ((Go4LunchApplication) getActivity().getApplication()).getMyLocation();
    }

    /**
     * get mapFragment.
     */
    private void initMap() {
        SupportMapFragment mapFragment = SupportMapFragment.newInstance();
        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.map, mapFragment);
        fragmentTransaction.commit();
        mapFragment.getMapAsync(this);
    }

    /**
     * On map ready update location
     * @param googleMap
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        updateLocationUI();
        putMarkerOnMap();
    }

    /**
     * if location != null showing myLocation on map
     */
    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (myLocation != null) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(myLocation.getLatitude(),myLocation.getLongitude()), DEFAULT_ZOOM));
            } else {
                if(!Permission.checkIfLocationPermissionGranted(getContext())){
                    Snackbar.make(getView(),"We can't access to your location, please check location permission!",Snackbar.LENGTH_LONG).show();
                }
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    /**
     * Showing markers on map for each restaurant.
     */
    private void putMarkerOnMap() {
        mMap.clear();
        for (Result place: mRestaurants) {
            Log.e("TAG", "restaurant: "+place.getPlace_id());
            Double lat = place.getGeometry().getLocation().getLat();
            Double lng = place.getGeometry().getLocation().getLng();
            LatLng latLng = new LatLng(lat, lng);
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);

            for (Restaurant restaurant: mRestaurantsListFromFirestore) {
                if (restaurant.getId().equals(place.getPlace_id()) && restaurant.getSomeoneEatingHere() == true) {
                    Log.e("TAG", "restaurant in firebase: " + restaurant);
                    markerOptions.icon((BitmapDescriptorFactory.fromResource(R.drawable.icon_restaurant_green)));
                } else {
                    markerOptions.icon((BitmapDescriptorFactory.fromResource(R.drawable.icon_restaurant_red)));
                }
            }

            Marker m = mMap.addMarker(markerOptions);
            m.setTag(place.getPlace_id());

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
     * Start DetailsActivity with restaurant in intent
     * @param marker
     */
    private void startDetailsActivity(Marker marker) {
        for (Result result : mRestaurants) {
            if (result.getPlace_id() == marker.getTag().toString()){
                Intent intent = new Intent(getContext(), DetailsActivity.class);
                intent.putExtra(DETAILS_PLACES, result);
                getContext().startActivity(intent);
            }
        }
    }
}