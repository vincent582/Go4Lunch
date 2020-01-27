package com.pdv.go4lunch.ui.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;

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
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.pdv.go4lunch.API.UserHelper;
import com.pdv.go4lunch.Go4LunchApplication;
import com.pdv.go4lunch.Model.GooglePlacesApiModel.Results;
import com.pdv.go4lunch.Model.Place.Result;
import com.pdv.go4lunch.Model.User;
import com.pdv.go4lunch.R;
import com.pdv.go4lunch.ui.ViewModel.PlacesViewModel;
import com.pdv.go4lunch.ui.activities.DetailsActivity;

import java.util.List;

import static com.pdv.go4lunch.ui.activities.DetailsActivity.INTENT_PLACE;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LatLng mDefaultLocation = new LatLng(10,10);
    private int DEFAULT_ZOOM = 18;

    private PlacesViewModel mPlacesViewModel;
    private Location myLocation;
    private List<Results> mPlaces;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        mPlacesViewModel = ViewModelProviders.of(this).get(PlacesViewModel.class);

        if (getArguments() != null){
            myLocation = getArguments().getParcelable("LOCATION");
            Log.e("TAG", "getLocation on Map Fragment from bundle: "+ getArguments().getParcelable("LOCATION"));
            mPlacesViewModel.init(myLocation);
        }

        initMap();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        myLocation = ((Go4LunchApplication) getActivity().getApplication()).getMyLocation();
        Log.e("TAG", "getLocation on Map Fragment: "+ myLocation);
        if (myLocation != null){
            mPlacesViewModel.init(myLocation);
        }
    }

    private void initMap() {
        SupportMapFragment mapFragment = SupportMapFragment.newInstance();
        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.map, mapFragment);
        fragmentTransaction.commit();
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        updateLocationUI();
    }

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
                                myLocation.getLongitude()), DEFAULT_ZOOM));
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

    private void getNearestPlaces() {
            mPlacesViewModel.getNearestPlaces().observe(this, this::putMarkerOnMap);
    }

    private void putMarkerOnMap(List<Results> results) {
        mPlaces = results;
        mMap.clear();
        for (Results place: mPlaces) {
            Double lat = place.getGeometry().getLocation().getLat();
            Double lng = place.getGeometry().getLocation().getLng();
            MarkerOptions markerOptions = new MarkerOptions();
            LatLng latLng = new LatLng(lat, lng);
            markerOptions.position(latLng);

            UserHelper.getAllUserForRestaurant(place.getName())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.getResult().isEmpty()){
                                for (QueryDocumentSnapshot snapshot : task.getResult()){
                                    Log.e("TAG", "onComplete: "+snapshot.toObject(User.class).getRestaurant());
                                }
                                markerOptions.icon((BitmapDescriptorFactory.fromResource(R.drawable.icon_restaurant_red)));
                            }else{
                                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_restaurant_green));
                            }
                        }
                    });

            Marker m = mMap.addMarker(markerOptions);
            m.setTag(place.getPlaceId());

            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    callApiPlaceToStartDetailsActivity(marker);
                    return false;
                }
            });
        }
    }


    private void callApiPlaceToStartDetailsActivity(Marker marker) {
        String placeId = marker.getTag().toString();

        mPlacesViewModel.getPlace(placeId).observe(this, new Observer<Result>() {
            @Override
            public void onChanged(Result result) {
                Intent intent = new Intent(getContext(), DetailsActivity.class);
                intent.putExtra(INTENT_PLACE, result);
                getContext().startActivity(intent);
            }
        });
    }
}