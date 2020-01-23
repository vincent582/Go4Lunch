package com.pdv.go4lunch.ui.fragment;


import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.navigation.fragment.NavHostFragment;

import android.os.Parcelable;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.pdv.go4lunch.API.UserHelper;
import com.pdv.go4lunch.Go4LunchApplication;
import com.pdv.go4lunch.Model.Place.Result;
import com.pdv.go4lunch.Model.User;
import com.pdv.go4lunch.R;
import com.pdv.go4lunch.ui.ViewModel.PlacesViewModel;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class YourLunchFragment extends Fragment {

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

    private FirebaseUser currentUser;
    private PlacesViewModel mPlacesViewModel;
    private Result place;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentUser = ((Go4LunchApplication)getActivity().getApplication()).getCurrentUser();
        mPlacesViewModel = new PlacesViewModel();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_your_lunch, container, false);
        ButterKnife.bind(this,view);

        UserHelper.getUser(currentUser.getUid()).addOnSuccessListener(getActivity(), new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User user = documentSnapshot.toObject(User.class);
                getPlace(user);
            }
        });

        return view;
    }

    private void getPlace(User user) {
        Log.e("TAG", "onSuccess: "+user.getRestaurantId());
        mPlacesViewModel.getPlace(user.getRestaurantId()).observe(this,this::getDetails);
    }

    private void getDetails(List<Result> results) {
        Log.e("TAG", "place return : " + results);
        place = results.get(0);
        updateView(place);
    }

    private void updateView(Result place) {
        if (place != null) {
            if (place.getPhotos() != null) {
                String url = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=" + place.getPhotos().get(0).getPhotoReference() + "&key=AIzaSyDGFBPIUVLpd36GZCrt1LQVL4zCaSbMzxU";
                Glide.with(mPictureRestaurant.getContext())
                        .load(url)
                        .centerCrop()
                        .into(mPictureRestaurant);
            }
            mNameRestaurant.setText(place.getName());
            mAdressRestaurant.setText(place.getVicinity());
            mWebsiteRestaurant.setText(place.getWebsite());

            mCancelBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UserHelper.deleteRestaurantFromUser(currentUser.getUid());
                    mLinearLayoutYourLunch.setVisibility(View.GONE);
                    Toast.makeText(getContext(),"Lunch Canceled",Toast.LENGTH_SHORT).show();
                    NavHostFragment.findNavController(getParentFragment()).navigate(R.id.yourLunchFragment);
                }
            });
            mLinearLayoutYourLunch.setVisibility(View.VISIBLE);
        }else{
            mLinearLayoutYourLunchEmpty.setVisibility(View.VISIBLE);
        }
    }
}
