package com.pdv.go4lunch.ui.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.pdv.go4lunch.API.RestaurantHelper;
import com.pdv.go4lunch.API.UserHelper;
import com.pdv.go4lunch.Model.Place.Result;
import com.pdv.go4lunch.Model.Restaurant;
import com.pdv.go4lunch.Model.User;
import com.pdv.go4lunch.R;
import com.pdv.go4lunch.ui.ViewModel.FirestoreViewModel;
import com.pdv.go4lunch.ui.ViewModel.PlacesViewModel;
import com.pdv.go4lunch.ui.viewHolder.UserRecyclerViewAdapter;
import com.pdv.go4lunch.utils.Permission;
import com.pdv.go4lunch.utils.Utils;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.pdv.go4lunch.ui.fragment.MapFragment.PLACE_ID;
import static com.pdv.go4lunch.utils.Utils.getCurrentUser;
import static com.pdv.go4lunch.utils.Utils.isCurrentUserLogged;

public class DetailsActivity extends AppCompatActivity {

    //FOR UI
    @BindView(R.id.details_name)
    TextView mName;
    @BindView(R.id.details_adress)
    TextView mAdress;
    @BindView(R.id.details_picture_restaurant)
    ImageView mPictureRestaurant;
    @BindView(R.id.phone_icon_iv)
    ImageView mCallIcon;
    @BindView(R.id.calling_tv)
    TextView mCallTextView;
    @BindView(R.id.website_icon_iv)
    ImageView mWebsiteIcon;
    @BindView(R.id.website_textview)
    TextView mWebsiteTextView;
    @BindView(R.id.likes_icon_ll)
    LinearLayout mLikesBtn;
    @BindView(R.id.like_icon)
    ImageView mlikesIcon;
    @BindView(R.id.likes_tv)
    TextView mlikesTextView;
    @BindView(R.id.floatingActionButton)
    FloatingActionButton mSelectRestaurantButton;
    @BindView(R.id.details_item_workmates_rv)
    RecyclerView mRecyclerView;
    @BindView(R.id.opening_hours_tv_details)
    TextView mOpeningRestaurant;
    @BindView(R.id.ratingRestaurantDetails)
    RatingBar mRatingBar;
    @BindView(R.id.iv_back)
    ImageView mBackButton;

    //FOR DATA
    private UserRecyclerViewAdapter adapter;
    private PlacesViewModel mPlacesViewModel;
    private FirestoreViewModel mFirestoreViewModel;
    private String mRestaurantId;
    private Result mRestaurant;
    private Restaurant restaurantInfireStore;
    private User mUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        ButterKnife.bind(this);

        if(getIntent().hasExtra(PLACE_ID)){
            mRestaurantId = getIntent().getStringExtra(PLACE_ID);
        }
        getUserFromFirebase();
        adapter = new UserRecyclerViewAdapter(generateOptionForAdapter(UserHelper.getAllUserForRestaurant(mRestaurantId)));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(adapter);

        mPlacesViewModel = ViewModelProviders.of(this).get(PlacesViewModel.class);
        mFirestoreViewModel = ViewModelProviders.of(this).get(FirestoreViewModel.class);
        mFirestoreViewModel.getRestaurant(mRestaurantId,this).addOnSuccessListener(this, new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                restaurantInfireStore = documentSnapshot.toObject(Restaurant.class);
                setUpStars();
            }
        });

        mPlacesViewModel.getRestaurantDetails(mRestaurantId,this).observe(this,this::updateView);
    }

    /**
     * get the current user information in firebase
     */
    private void getUserFromFirebase() {
        if (isCurrentUserLogged() != null){
            Task<DocumentSnapshot> user = UserHelper.getUser(getCurrentUser().getUid());
            user.addOnSuccessListener(this, new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    mUser = documentSnapshot.toObject(User.class);
                    setActionButtonImage();
                }
            });
        }
    }

    /**
     * Generation Option adapter for query request on Firebase.
     * @param query
     * @return
     */
    private FirestoreRecyclerOptions<User> generateOptionForAdapter(Query query) {
        return new FirestoreRecyclerOptions.Builder<User>()
                .setQuery(query,User.class)
                .setLifecycleOwner(this)
                .build();
    }

    /**
     * Show the number of likes in firebase
     * Use Utils getNumberOfStars to get average.
     */
    private void setUpStars() {
        if (restaurantInfireStore != null && restaurantInfireStore.getLikes() > 0){
            UserHelper.getAllUsers().addSnapshotListener(this, new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                    int nbrOfStars = Utils.getNumberOfStars(queryDocumentSnapshots.size(),restaurantInfireStore.getLikes());
                    mRatingBar.setNumStars(nbrOfStars);
                    mRatingBar.setRating(nbrOfStars);
                    mRatingBar.setVisibility(View.VISIBLE);
                }
            });
        }
    }

    /**
     * Set view with restaurantInfireStore information got from Intent
     */
    private void updateView(Result result) {
        mRestaurant = result;
        mName.setText(mRestaurant.getName());
        mAdress.setText(mRestaurant.getVicinity());
        if (mRestaurant.getPhotos() != null){
        String url = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference="+ mRestaurant.getPhotos().get(0).getPhotoReference()+"&key=AIzaSyDGFBPIUVLpd36GZCrt1LQVL4zCaSbMzxU";
        Glide.with(mPictureRestaurant.getContext())
                .load(url)
                .centerCrop()
                .into(mPictureRestaurant);
        }
        setUpPhoneIcon();
        setUpWebsiteIcon();
        setUpLikesBtn();
        setUpSelectRestaurantButton();
        setRestaurantSchedules(result);
        setUpBackBtn();
    }

    /**
     * Set schedules of the restaurantInfireStore
     * @param restaurant
     */
    private void setRestaurantSchedules(Result restaurant) {
        if (restaurant.getOpeningHours() != null) {
            if (restaurant.getOpeningHours().getOpenNow()) {
                Calendar calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_WEEK);
                //Api give array size 1 if place open 24/7
                if (restaurant.getOpeningHours().getPeriods().size() > 1){
                    String time = restaurant.getOpeningHours().getPeriods().get(day-1).getClose().getTime();
                    mOpeningRestaurant.setText(Utils.formatTimeFromOpenningHours(time,this));
                }else {
                    mOpeningRestaurant.setText(getResources().getString(R.string.open_24));
                }
            }else{
                mOpeningRestaurant.setText(getResources().getString(R.string.closed));
            }
        }else {
            mOpeningRestaurant.setText("");
        }
    }

    /**
     * Set up click on Website icon if restaurantInfireStore have one.
     */
    private void setUpWebsiteIcon() {
        if (mRestaurant.getWebsite() != null) {
            mWebsiteIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goToWebsiteDialog();
                }
            });
        }
        else {
            mWebsiteIcon.setColorFilter(ContextCompat.getColor(this, R.color.colorGrey), PorterDuff.Mode.MULTIPLY);
            mWebsiteTextView.setTextColor(getResources().getColor(R.color.colorGrey));
        }
    }

    /**
     * Open Dialog alert to ask to go on the website by intent.
     */
    private void goToWebsiteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.go_to_website)).setMessage(mRestaurant.getWebsite());
        builder.setPositiveButton(getResources().getString(R.string.go), (dialog, id) -> {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.addCategory(Intent.CATEGORY_BROWSABLE);
            intent.setData(Uri.parse(mRestaurant.getWebsite()));
            startActivity(intent);
        });
        builder.setNegativeButton(getResources().getString(R.string.cancel), (dialog, id) -> dialog.cancel());
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * Set up click on phone icon if restaurantInfireStore have phone number.
     */
    private void setUpPhoneIcon() {
        if (mRestaurant.getFormattedPhoneNumber() != null && !mRestaurant.getFormattedPhoneNumber().isEmpty()){
            mCallIcon.setOnClickListener(v -> checkPermissionCall());
        }
        else {
            mCallIcon.setColorFilter(ContextCompat.getColor(this, R.color.colorGrey),PorterDuff.Mode.MULTIPLY);
            mCallTextView.setTextColor(getResources().getColor(R.color.colorGrey));
        }
    }

    /**
     * Asking for permissionCalling if ok,
     * Open Dialog alert to ask to go on make phone call by intent.
     */
    public void checkPermissionCall(){
        if (Permission.checkIfCallingPermissionGranted(this)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getResources().getString(R.string.call_restaurant)).setMessage(mRestaurant.getFormattedPhoneNumber());
            builder.setPositiveButton(getResources().getString(R.string.call), (dialog, id) -> {
                Intent callingIntent = new Intent(Intent.ACTION_CALL);
                callingIntent.setData(Uri.parse("tel:" + mRestaurant.getFormattedPhoneNumber()));
                startActivity(callingIntent);
            });
            builder.setNegativeButton(getResources().getString(R.string.cancel), (dialog, id) -> dialog.cancel());
            AlertDialog dialog = builder.create();
            dialog.show();
        }
        else{
            Permission.requestCallingPermissions(this);
        }
    }

    public void setUpLikesBtn(){ mLikesBtn.setOnClickListener(v -> checkLikes()); }
    
    /**
     * Check if the restaurant in firestore exist
     * if not create the restaurant and add like
     * if exist get likes and add one.
     */
    private void checkLikes(){
        if (restaurantInfireStore == null){
            RestaurantHelper.createRestaurant(mRestaurant.getPlace_id(),mRestaurant.getName(),mRestaurant.getVicinity()).addOnSuccessListener(this, new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    mFirestoreViewModel.addLikes(mRestaurantId,1);
                    mlikesIcon.setColorFilter(ContextCompat.getColor(getBaseContext(), R.color.colorGrey), PorterDuff.Mode.MULTIPLY);
                    mlikesTextView.setTextColor(getResources().getColor(R.color.colorGrey));
                    Toast.makeText(getBaseContext(), R.string.liked_restaurant, Toast.LENGTH_SHORT).show();
                }
            });
        }else {
            int likes = restaurantInfireStore.getLikes() + 1;
            mFirestoreViewModel.addLikes(mRestaurantId, likes);
            mlikesIcon.setColorFilter(ContextCompat.getColor(getBaseContext(), R.color.colorGrey), PorterDuff.Mode.MULTIPLY);
            mlikesTextView.setTextColor(getResources().getColor(R.color.colorGrey));
            Toast.makeText(getBaseContext(), R.string.liked_restaurant, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Add click listener to ActionButton to select restaurantInfireStore
     */
    private void setUpSelectRestaurantButton() {
        mSelectRestaurantButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DetailsActivity.this.updateRestaurantInFirebase();
            }
        });
    }

    private void setActionButtonImage() {
        if (mUser.getRestaurantName() != null && mUser.getRestaurantName().equals(mRestaurant.getName())){
            mSelectRestaurantButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_close_black_24dp));
        }else{
            mSelectRestaurantButton.setImageDrawable(getResources().getDrawable(R.drawable.fui_ic_check_circle_black_128dp));
        }
    }

    /**
     * Save the restaurant chosen in firebase
     * if already saved cancel it.
     */
    private void updateRestaurantInFirebase() {
        if (restaurantInfireStore == null){
            RestaurantHelper.createRestaurant(mRestaurant.getPlace_id(),mRestaurant.getName(),mRestaurant.getVicinity());
            mFirestoreViewModel.updateNbrOfPeopleForeachRestaurant(this);
        }
        else if (restaurantInfireStore.getId().equals(mRestaurant.getPlace_id())){
            mFirestoreViewModel.updateNbrOfPeopleForeachRestaurant(this);
        }

        if (mUser.getRestaurantName() != null && mUser.getRestaurantName().equals(mRestaurant.getName())){
            UserHelper.deleteRestaurantFromUser(Utils.getCurrentUser().getUid());
            Toast.makeText(this, R.string.lunch_canceled, Toast.LENGTH_SHORT).show();
        }else{
            UserHelper.updateUserRestaurantNameAndId(getCurrentUser().getUid(),mRestaurant.getPlace_id(),mRestaurant.getName());
            Toast.makeText(this, R.string.restaurant_chosen, Toast.LENGTH_SHORT).show();
        }

        getUserFromFirebase();
    }

    private void setUpBackBtn() {
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
