package com.pdv.go4lunch;

import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.idling.CountingIdlingResource;
import androidx.test.filters.LargeTest;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import androidx.test.rule.ActivityTestRule;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.pdv.go4lunch.API.UserHelper;
import com.pdv.go4lunch.Model.Restaurant;
import com.pdv.go4lunch.Model.User;
import com.pdv.go4lunch.ui.activities.MainActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.theories.DataPoint;
import org.junit.runner.RunWith;

import java.util.List;

import static androidx.test.espresso.Espresso.onIdle;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4ClassRunner.class)
@LargeTest
public class Go4LunchApplicationTest {

    private FirebaseUser firebaseUser;
    private final CountingIdlingResource callOnApiIdl = new CountingIdlingResource("Data API");
    private User mUser;
    private boolean success;

    @Rule
    public final ActivityTestRule<MainActivity> rule = new ActivityTestRule<>(MainActivity.class);

    @Before
    public void init(){
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        assertNotNull(firebaseUser);
        mUser = new User("UID","userName","pictureUrl");
        success = false;
        IdlingRegistry.getInstance().register(callOnApiIdl);
    }

    @After
    public void setDown() {
        IdlingRegistry.getInstance().unregister(callOnApiIdl);
    }


    //Show fragment
    @Test
    public void showMapFragmentOnClickOnMapMenuItem() {
        onView(withId(R.id.mapFragment)).perform(click());
        onView(withId(R.id.mapFragment)).check(matches(isDisplayed()));
    }

    @Test
    public void showListViewFragmentOnClickOnListViewMenuItem() {
        onView(withId(R.id.listViewFragment)).perform(click());
        onView(withId(R.id.listViewFragment)).check(matches(isDisplayed()));
    }

    @Test
    public void showWorkMatesFragmentOnClickOnWorkmatesMenuItem() {
        onView(withId(R.id.workMatesFragment)).perform(click());
        onView(withId(R.id.workMatesFragment)).check(matches(isDisplayed()));
    }

    @DataPoint
    private void addUserInDataBase() {
        callOnApiIdl.increment();
        UserHelper.createUser("UID","userName","pictureUrl").addOnCompleteListener(task -> {
            success = task.isSuccessful();
            callOnApiIdl.decrement();
        });
        onIdle();
    }

    @DataPoint
    private void deleteUserInDataBase() {
        callOnApiIdl.increment();
        UserHelper.deleteUser(mUser.getId()).addOnCompleteListener(task -> {
            success = task.isSuccessful();
            callOnApiIdl.decrement();
        });
        onIdle();
    }

    @Test
    public void addUserInFirebase() {
        addUserInDataBase();
        assertTrue("Add user to the cloud firestore", success);
        deleteUserInDataBase();
    }

    @Test
    public void deleteUserFromFirebase() {
        addUserInDataBase();
        deleteUserInDataBase();
        assertTrue("Delete user from the cloud firestore", success);
    }

    @Test
    public void convertUserFromFirebaseToModel() {
        addUserInDataBase();
        callOnApiIdl.increment();
        UserHelper.getUser(mUser.getId()).addOnCompleteListener(result -> {
            if (result.isSuccessful() && result.getResult() != null) {
                User userResult = result.getResult().toObject(User.class);
                success = mUser.equals(userResult);
            }
            callOnApiIdl.decrement();
        });
        onIdle();
        deleteUserInDataBase();
        assertTrue("Get user from the cloud firestore", success);
    }

    @Test
    public void getAllUsersFromFirebase() {
        addUserInDataBase();
        callOnApiIdl.increment();
        UserHelper.getAllUsers().addOnCompleteListener(result -> {
            if (result.isSuccessful() && result.getResult() != null) {
                List<User> users = result.getResult().toObjects(User.class);
                success = !users.isEmpty();
            }
            callOnApiIdl.decrement();
        });
        onIdle();
        deleteUserInDataBase();
        assertTrue("Get users from database ", success);
    }

    @Test
    public void updateRestaurantForUser() {
        addUserInDataBase();
        Restaurant restaurant = new Restaurant();
        restaurant.setId("updateLunchRestaurantIdTest");
        restaurant.setName("updateLunchRestaurantNameTest");
        callOnApiIdl.increment();
        UserHelper.updateUserRestaurantNameAndId(mUser.getId(),restaurant.getId(),restaurant.getName()).addOnCompleteListener(task -> {
            success = task.isSuccessful();
            callOnApiIdl.decrement();
        });
        onIdle();
        assertTrue("Update the restaurant where to have lunch", success);
        deleteUserInDataBase();
    }
}