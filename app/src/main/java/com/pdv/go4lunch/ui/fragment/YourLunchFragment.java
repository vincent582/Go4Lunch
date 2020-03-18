package com.pdv.go4lunch.ui.fragment;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.pdv.go4lunch.API.UserHelper;
import com.pdv.go4lunch.Model.User;
import com.pdv.go4lunch.R;
import com.pdv.go4lunch.ui.activities.DetailsActivity;
import com.pdv.go4lunch.utils.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.pdv.go4lunch.ui.fragment.MapFragment.PLACE_ID;

/**
 * A simple {@link Fragment} subclass.
 */
public class YourLunchFragment extends Fragment {

    //FOR UI
    @BindView(R.id.your_lunch_details_empty_ll)
    LinearLayout mLinearLayoutYourLunchEmpty;

    private NavController mNavController;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_your_lunch, container, false);
        ButterKnife.bind(this,view);

        mNavController = NavHostFragment.findNavController(this);

        getCurrentUserWithAllData();
        return view;
    }

    /**
     * Get current user from firebase with all information
     * Create custom User model
     */
    private void getCurrentUserWithAllData() {
        UserHelper.getUser(Utils.getCurrentUser().getUid()).addOnSuccessListener(getActivity(), new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User user = documentSnapshot.toObject(User.class);
                getPlace(user);
            }
        });
    }

    /**
     * Get user chosen restaurant in list
     * @param user
     */
    private void getPlace(User user) {
        if (user.getRestaurantId() != null) {
            mNavController.navigate(R.id.mapFragment);
            startDetailsActivity(user.getRestaurantId());
        } else {
            mLinearLayoutYourLunchEmpty.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Start details activity on click on marker.
     * @param restaurantId
     */
    private void startDetailsActivity(String restaurantId) {
        Intent intent = new Intent(getContext(), DetailsActivity.class);
        intent.putExtra(PLACE_ID, restaurantId);
        getContext().startActivity(intent);
    }
}