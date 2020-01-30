package com.pdv.go4lunch.ui.fragment;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.pdv.go4lunch.API.UserHelper;
import com.pdv.go4lunch.Model.Place.Result;
import com.pdv.go4lunch.Model.User;
import com.pdv.go4lunch.R;
import com.pdv.go4lunch.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.pdv.go4lunch.ui.activities.MainActivity.BUNDLE_PLACES;

/**
 * A simple {@link Fragment} subclass.
 */
public class YourLunchFragment extends Fragment {

    //FOR UI
    @BindView(R.id.your_lunch_details_ll)
    LinearLayout mLinearLayoutYourLunch;
    @BindView(R.id.your_lunch_details_empty_ll)
    LinearLayout mLinearLayoutYourLunchEmpty;
    @BindView(R.id.your_lunch_restaurant_picture_iv)
    ImageView mPictureRestaurant;
    @BindView(R.id.your_lunch_restaurant_name_tv)
    TextView mNameRestaurant;
    @BindView(R.id.your_lunch_restaurant_adress_tv)
    TextView mAdressRestaurant;
    @BindView(R.id.your_lunch_restaurant_website_tv)
    TextView mWebsiteRestaurant;
    @BindView(R.id.your_lunch_cancel_btn)
    Button mCancelBtn;

    //FOR DATA
    private List<Result> mRestaurants = new ArrayList<>();

    /**
     * Get Arguments
     * @param savedInstanceState
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null)
        {
            mRestaurants = getArguments().getParcelableArrayList(BUNDLE_PLACES);
            Log.e("TAG", "onCreateView listViewFragment fragment: " + getArguments().getParcelableArrayList(BUNDLE_PLACES));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_your_lunch, container, false);
        ButterKnife.bind(this,view);
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
        Log.e("TAG", "getPlace: "+mRestaurants);
        for (Result mRestaurant : mRestaurants){
            if (user.getRestaurantId() == mRestaurant.getPlace_id()){
                updateView(mRestaurant);
            }
        }
        mLinearLayoutYourLunchEmpty.setVisibility(View.VISIBLE);
    }

    /**
     * Update fragment UI
     * @param restaurant
     */
    private void updateView(Result restaurant) {
        if (restaurant.getPhotos() != null) {
            String url = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=" + restaurant.getPhotos().get(0).getPhotoReference() + "&key=AIzaSyDGFBPIUVLpd36GZCrt1LQVL4zCaSbMzxU";
            Glide.with(mPictureRestaurant.getContext())
                    .load(url)
                    .centerCrop()
                    .into(mPictureRestaurant);
        }
        mNameRestaurant.setText(restaurant.getName());
        mAdressRestaurant.setText(restaurant.getVicinity());
        mWebsiteRestaurant.setText(restaurant.getWebsite());
        mLinearLayoutYourLunch.setVisibility(View.VISIBLE);

        mCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserHelper.deleteRestaurantFromUser(Utils.getCurrentUser().getUid());
                Toast.makeText(getContext(), "Lunch Canceled", Toast.LENGTH_SHORT).show();
                mLinearLayoutYourLunch.setVisibility(View.GONE);
                mLinearLayoutYourLunchEmpty.setVisibility(View.VISIBLE);
            }
        });
    }
}
