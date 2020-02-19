package com.pdv.go4lunch.notification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.os.Build;
import android.preference.PreferenceManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.pdv.go4lunch.API.RestaurantHelper;
import com.pdv.go4lunch.API.UserHelper;
import com.pdv.go4lunch.Go4LunchApplication;
import com.pdv.go4lunch.Model.Restaurant;
import com.pdv.go4lunch.Model.User;
import com.pdv.go4lunch.R;
import com.pdv.go4lunch.ui.activities.MainActivity;
import com.pdv.go4lunch.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import static com.pdv.go4lunch.ui.fragment.SettingsFragment.KEY_PREFERENCES_NOTIFICATION;

public class NotificationService extends FirebaseMessagingService {

    private final int NOTIFICATION_ID = 999;
    private final String NOTIFICATION_TAG = "FIREBASE_NOTIFICATION";

    private FirebaseUser mCurrentUser;
    private String message;
    private Restaurant restaurant;
    private List<User> mUserList = new ArrayList<>();

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (checkSettingsIfReceiveNotificationIsTrue()) {
            if (remoteMessage.getNotification() != null) {
                message = remoteMessage.getNotification().getBody();

                mCurrentUser = Utils.getCurrentUser();

                UserHelper.getUser(mCurrentUser.getUid()).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        User user = task.getResult().toObject(User.class);
                        if (user.getRestaurantId() != null) {
                            getRestaurant(user.getRestaurantId());
                        }
                    }
                });
            }
        }
    }

    private boolean checkSettingsIfReceiveNotificationIsTrue() {
        SharedPreferences sharedPreferences = getSharedPreferences(KEY_PREFERENCES_NOTIFICATION,MODE_PRIVATE);
        Boolean checked = sharedPreferences.getBoolean(KEY_PREFERENCES_NOTIFICATION, true);
        return checked;
    }

    private void getRestaurant(String restaurantId) {
        RestaurantHelper.getRestaurant(restaurantId).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                restaurant = task.getResult().toObject(Restaurant.class);
                getPeopleEatingThere(restaurantId);
            }
        });
    }

    private void getPeopleEatingThere(String restaurantId) {
        UserHelper.getAllUserForRestaurant(restaurantId).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (queryDocumentSnapshots != null) {
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        User user = doc.toObject(User.class);
                        mUserList.add(user);
                    }
                    sendVisualNotification(message);
                }
            }
        });
    }

    private void sendVisualNotification(String messageBody) {
        // 1 - Create an Intent that will be shown when user will click on the Notification
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        // 2 - Create a Style for the Notification
        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        inboxStyle.setBigContentTitle(getResources().getString(R.string.app_name));
        inboxStyle.addLine(messageBody +" "+restaurant.getName());
        inboxStyle.addLine(restaurant.getVicinity());

        String workmates = "";
        if (!mUserList.isEmpty()){
            for (User user: mUserList){
                if (!user.getUserName().equals(mCurrentUser.getDisplayName())) {
                    workmates += user.getUserName() + " ";
                }
            }
            inboxStyle.addLine(workmates);
        }

        // 3 - Create a Channel (Android 8)
        String channelId = "chanel";

        // 4 - Build a Notification object
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.ic_logo_auth)
                        .setContentTitle(getString(R.string.app_name))
                        .setContentText(getResources().getString(R.string.reminder_lunch_notification))
                        .setAutoCancel(true)
                        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                        .setContentIntent(pendingIntent)
                        .setStyle(inboxStyle);

        // 5 - Add the Notification to the Notification Manager and show it.
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // 6 - Support Version >= Android 8
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence channelName = "Firebase Message";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(channelId, channelName, importance);
            notificationManager.createNotificationChannel(mChannel);
        }

        // 7 - Show notification
        notificationManager.notify(NOTIFICATION_TAG, NOTIFICATION_ID, notificationBuilder.build());
    }
}
