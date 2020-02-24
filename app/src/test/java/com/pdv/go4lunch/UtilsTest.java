package com.pdv.go4lunch;

import android.location.Location;

import com.pdv.go4lunch.Model.GooglePlacesApiModel.Results;
import com.pdv.go4lunch.utils.Utils;

import org.hamcrest.core.IsInstanceOf;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class UtilsTest {

    private Results results1 = new Results();
    private Results results2 = new Results();
    private Location myLocation = new Location("Location");
    private Location otherLocation = new Location("Location");

    @Before
    public void initTest(){
        myLocation.setLatitude(10.00);
        myLocation.setLongitude(10.00);
        otherLocation.setLatitude(10.10);
        otherLocation.setLongitude(10.10);
    }

    @Test
    public void returnIntValueWhenGetDistanceBetweenLocations() {
        int distance = Utils.getDistanceBetweenLocation(myLocation,otherLocation);
        assertThat(distance, new IsInstanceOf(int.class));
    }

    @Test
    public void formatTimeFromStringToGetTimeToShow(){
        String timeString = "1027";
        String timeFormated = Utils.formatTimeFromOpenningHours(timeString);
        assertEquals("10.27am", timeFormated);

        String timeString2 = "1723";
        String timeFormated2 = Utils.formatTimeFromOpenningHours(timeString2);
        assertEquals("5.23pm", timeFormated2);
    }

    @Test
    public void sortByDistance() {
        results1.setDistance(110);
        results2.setDistance(77);
        List<Results> resultsList = new ArrayList<>();
        resultsList.add(results1);
        resultsList.add(results2);

        Utils.sortByDistance(resultsList);

        assertEquals(resultsList.get(0), results2);
        assertEquals(resultsList.get(1), results1);
    }

    @Test
    public void getNbrOfStarsForLike(){
        int nbrOfUsers = 10;
        int nbrOfLikes = 5;
        //we got 50% of users like restaurant should return 2 stars
        int nbrOfStars = Utils.getNumberOfStars(nbrOfUsers, nbrOfLikes);
        assertEquals(2, nbrOfStars);
    }
}