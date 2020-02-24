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

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.pdv.go4lunch.API.RestaurantHelper;
import com.pdv.go4lunch.API.UserHelper;
import com.pdv.go4lunch.BuildConfig;
import com.pdv.go4lunch.Model.Place.Result;
import com.pdv.go4lunch.Model.Restaurant;
import com.pdv.go4lunch.Model.User;
import com.pdv.go4lunch.R;
import com.pdv.go4lunch.ui.ViewModel.RestaurantFirestoreViewModel;
import com.pdv.go4lunch.ui.ViewModel.PlacesViewModel;
import com.pdv.go4lunch.ui.ViewModel.UsersFirestoreViewModel;
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
    private UserRecyclerViewAdapter mAdapter;
    private PlacesViewModel mPlacesViewModel;
    private RestaurantFirestoreViewModel mRestaurantFirestoreViewModel;
    private UsersFirestoreViewModel mUsersFirestoreViewModel;
    private String mRestaurantId;
    private Result mRestaurant;
    private Restaurant mRestaurantInFireStore;
    private User mCurrentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        ButterKnife.bind(this);

        checkIfIntent();
        getCurrentUserFromFirebase();
        configureRecyclerView();
        configureViewModel();
        getUsersForRestaurant();
        getRestaurantFromFirestore();

        mPlacesViewModel.getRestaurantDetails(mRestaurantId,this).observe(this,this::updateView);
    }

    /**
     * Get Intent who contain the place_id
     */
    private void checkIfIntent() {
        if(getIntent().hasExtra(PLACE_ID)){
            mRestaurantId = getIntent().getStringExtra(PLACE_ID);
        }
    }

    /**
     * get the current user information in firebase
     * and update button
     */
    private void getCurrentUserFromFirebase() {
        if (isCurrentUserLogged() != null){
            Task<DocumentSnapshot> user = UserHelper.getUser(getCurrentUser().getUid());
            user.addOnSuccessListener(this, new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    mCurrentUser = documentSnapshot.toObject(User.class);
                    setActionButtonImage();
                }
            });
        }
    }

    /**
     * set up button image if currentUser selected the restaurant
     */
    private void setActionButtonImage() {
        if (mCurrentUser.getRestaurantName() != null && mCurrentUser.getRestaurantName().equals(mRestaurant.getName())){
            mSelectRestaurantButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_close_black_24dp));
        }else{
            mSelectRestaurantButton.setImageDrawable(getResources().getDrawable(R.drawable.fui_ic_check_circle_black_128dp));
        }
    }

    /**
     * Configure recycler view with UserRecyclerViewAdapter
     */
    private void configureRecyclerView() {
        mAdapter = new UserRecyclerViewAdapter();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mAdapter);
    }

    /**
     * configure all viewModel
     */
    private void configureViewModel() {
        mUsersFirestoreViewModel = ViewModelProviders.of(this).get(UsersFirestoreViewModel.class);
        mPlacesViewModel = ViewModelProviders.of(this).get(PlacesViewModel.class);
        mRestaurantFirestoreViewModel = ViewModelProviders.of(this).get(RestaurantFirestoreViewModel.class);
    }

    /**
     * Get users going to this restaurant from viewModel
     * and update list of recyclerView
     */
    private void getUsersForRestaurant(){
        mUsersFirestoreViewModel.getUsersForRestaurant(mRestaurantId).observe(this, userList -> mAdapter.updateUsers(userList));
    }

    /**
     * Get the restaurant in Firestore and setUpStars.
     */
    private void getRestaurantFromFirestore() {
        mRestaurantFirestoreViewModel.getRestaurant(mRestaurantId,this).addOnSuccessListener(this, new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                mRestaurantInFireStore = documentSnapshot.toObject(Restaurant.class);
                setUpStars();
            }
        });
    }

    /**
     * Show the number of likes in firebase for this restaurant
     * Use Utils getNumberOfStars to get average.
     */
    private void setUpStars() {
        if (mRestaurantInFireStore != null && mRestaurantInFireStore.getLikes() > 0){
            UserHelper.getAllUsers().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    int nbrOfStars = Utils.getNumberOfStars(queryDocumentSnapshots.size(), mRestaurantInFireStore.getLikes());
                    mRatingBar.setNumStars(nbrOfStars);
                    mRatingBar.setRating(nbrOfStars);
                    mRatingBar.setVisibility(View.VISIBLE);
                }
            });
        }
    }

    /**
     * SetUp view with result information got from GooglePlace Api
     */
    private void updateView(Result result) {
        mRestaurant = result;
        mName.setText(mRestaurant.getName());
        mAdress.setText(mRestaurant.getVicinity());
        if (mRestaurant.getPhotos() != null){
        String url = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference="+ mRestaurant.getPhotos().get(0).getPhotoReference()+"&key="+ BuildConfig.GOOGLE_API_KEY;
        Glide.with(mPictureRestaurant.getContext())
                .load(url)
                .centerCrop()
                .into(mPictureRestaurant);
        }
        setUpPhoneIcon();
        setUpWebsiteIcon();
        setUpLikesBtn();
        setRestaurantSchedules(result);
        setUpBackBtn();
        setUpSelectRestaurantButton();
    }

    /**
     * Set up click on phone icon if mRestaurantInFireStore have phone number.
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

    /**
     * Set up click on Website icon if mRestaurantInFireStore have one.
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
     * SetUp click Listener on the like icon
     */
    public void setUpLikesBtn(){ mLikesBtn.setOnClickListener(v -> checkLikes()); }

    /**
     * Check if the restaurant in firestore exist
     * if not create the restaurant and add like
     * if exist get likes and add one.
     */
    private void checkLikes(){
        if (mRestaurantInFireStore == null){
            RestaurantHelper.createRestaurant(mRestaurant.getPlace_id(),mRestaurant.getName(),mRestaurant.getVicinity()).addOnSuccessListener(this, new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    mRestaurantFirestoreViewModel.addLikes(mRestaurantId,1);
                    mlikesIcon.setColorFilter(ContextCompat.getColor(getBaseContext(), R.color.colorGrey), PorterDuff.Mode.MULTIPLY);
                    mlikesTextView.setTextColor(getResources().getColor(R.color.colorGrey));
                    Toast.makeText(getBaseContext(), R.string.liked_restaurant, Toast.LENGTH_SHORT).show();
                }
            });
        }else {
            int likes = mRestaurantInFireStore.getLikes() + 1;
            mRestaurantFirestoreViewModel.addLikes(mRestaurantId, likes);
            mlikesIcon.setColorFilter(ContextCompat.getColor(getBaseContext(), R.color.colorGrey), PorterDuff.Mode.MULTIPLY);
            mlikesTextView.setTextColor(getResources().getColor(R.color.colorGrey));
            Toast.makeText(getBaseContext(), R.string.liked_restaurant, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Show schedules of the restaurant
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
                    mOpeningRestaurant.setText(getResources().getString(R.string.opening_time) +" "+ Utils.formatTimeFromOpenningHours(time));
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
     * Add click listener to the back button
     * and kill this Activity
     */
    private void setUpBackBtn() {
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    /**
     * Add click listener to ActionButton to select mRestaurantInFireStore
     */
    private void setUpSelectRestaurantButton() {
        mSelectRestaurantButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DetailsActivity.this.updateRestaurantInFirebase();
            }
        });
    }

    /**
     * Save the restaurant chosen for user in firebase
     * if already saved cancel it.
     */
    private void updateRestaurantInFirebase() {
        if (mRestaurantInFireStore == null){
            RestaurantHelper.createRestaurant(mRestaurant.getPlace_id(),mRestaurant.getName(),mRestaurant.getVicinity());
            mRestaurantFirestoreViewModel.updateNbrOfPeopleForeachRestaurant(this);
        }
        else if (mRestaurantInFireStore.getId().equals(mRestaurant.getPlace_id())){
            mRestaurantFirestoreViewModel.updateNbrOfPeopleForeachRestaurant(this);
        }

        if (mCurrentUser.getRestaurantName() != null && mCurrentUser.getRestaurantName().equals(mRestaurant.getName())){
            UserHelper.deleteRestaurantFromUser(Utils.getCurrentUser().getUid());
            Toast.makeText(this, R.string.lunch_canceled, Toast.LENGTH_SHORT).show();
            getUsersForRestaurant();
        }else{
            UserHelper.updateUserRestaurantNameAndId(getCurrentUser().getUid(),mRestaurant.getPlace_id(),mRestaurant.getName());
            Toast.makeText(this, R.string.restaurant_chosen, Toast.LENGTH_SHORT).show();
            getUsersForRestaurant();
        }

        getCurrentUserFromFirebase();
    }
}
