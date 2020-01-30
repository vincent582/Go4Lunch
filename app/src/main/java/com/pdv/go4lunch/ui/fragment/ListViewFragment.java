package com.pdv.go4lunch.ui.fragment;

import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pdv.go4lunch.Go4LunchApplication;
import com.pdv.go4lunch.Model.Place.Result;
import com.pdv.go4lunch.R;
import com.pdv.go4lunch.ui.viewHolder.PlacesRecyclerViewAdapter;
import com.pdv.go4lunch.utils.Permission;
import com.pdv.go4lunch.utils.Utils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.pdv.go4lunch.ui.activities.MainActivity.BUNDLE_PLACES;

public class ListViewFragment extends Fragment {

    //FOR UI
    @BindView(R.id.list_restaurant_recycler_view)
    RecyclerView mRecyclerView;

    //FOR DATA
    private PlacesRecyclerViewAdapter adapter = new PlacesRecyclerViewAdapter();
    private Location myLocation;
    private List<Result> mRestaurant;

    /**
     * Get Location if permission granted
     * Get Arguments
     * @param savedInstanceState
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Permission.checkIfLocationPermissionGranted(getContext())){
            myLocation = ((Go4LunchApplication) getActivity().getApplication()).getMyLocation();
            Log.i("TAG", "Get location in ListView : "+ myLocation);
        }

        if (getArguments() != null)
        {
            mRestaurant = getArguments().getParcelableArrayList(BUNDLE_PLACES);
            Log.e("TAG", "onCreateView listViewFragment fragment: " + getArguments().getParcelableArrayList(BUNDLE_PLACES));
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
        setDistanceBetweenRestaurantAndSortByNearest();
        return view;
    }

    /**
     * Set distance to each restaurant and sort by ascending
     */
    private void setDistanceBetweenRestaurantAndSortByNearest() {
        for (Result result : mRestaurant){
            result.setDistance(Utils.getDistanceBetweenLocation(myLocation,result));
        }
        Utils.sortByDistance(mRestaurant);
        updateUi();
    }

    /**
     * Pass restaurant list to the adapter
     */
    private void updateUi(){
        adapter.updatedPlaces(mRestaurant);
    }

}
