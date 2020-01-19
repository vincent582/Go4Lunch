package com.pdv.go4lunch.ui.fragment;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.navigation.NavController;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.pdv.go4lunch.Go4LunchApplication;
import com.pdv.go4lunch.Model.GooglePlacesApiModel.Results;
import com.pdv.go4lunch.Model.Place.Result;
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        if (getArguments() != null){
            myLocation = getArguments().getParcelable("LOCATION");
            Log.e("TAG", "getLocation on Map Fragment from bundle: "+ getArguments().getParcelable("LOCATION"));
        }
        mPlacesViewModel = new PlacesViewModel();
        initMap();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        myLocation = ((Go4LunchApplication) getActivity().getApplication()).getMyLocation();
        Log.e("TAG", "getLocation on Map Fragment: "+ myLocation);
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
                placeMarkerOnMap();
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void placeMarkerOnMap() {
        mPlacesViewModel.getNearestPlaces(myLocation).observe(this, new Observer<List<Results>>() {
            @Override
            public void onChanged(List<Results> results) {
                try {
                    mMap.clear();
                    // This loop will go through all the results and add marker on each location.
                    for (int i = 0; i <= results.size(); i++) {
                        Double lat = results.get(i).getGeometry().getLocation().getLat();
                        Double lng = results.get(i).getGeometry().getLocation().getLng();

                        MarkerOptions markerOptions = new MarkerOptions();
                        LatLng latLng = new LatLng(lat, lng);
                        markerOptions.position(latLng);
                        Marker m = mMap.addMarker(markerOptions);
                        m.setTag(results.get(i).getPlaceId());
                        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET));

                        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                            @Override
                            public boolean onMarkerClick(Marker marker) {
                                callApiPlaceToStartDetailsActivity(marker);
                                return false;
                            }
                        });
                    }
                } catch (Exception e) {
                    Log.d("onResponse", "There is an error");
                    e.printStackTrace();
                }
            }
        });
    }

    private void callApiPlaceToStartDetailsActivity(Marker marker) {
        String placeId = marker.getTag().toString();
        mPlacesViewModel.getPlace(placeId).observe(this, new Observer<List<Result>>() {
            @Override
            public void onChanged(List<Result> results) {
                Intent intent = new Intent(getContext(),DetailsActivity.class);
                intent.putExtra(INTENT_PLACE, results.get(0));
                getContext().startActivity(intent);
            }
        });
    }
}

