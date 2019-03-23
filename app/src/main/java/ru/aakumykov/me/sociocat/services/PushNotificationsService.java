package ru.aakumykov.me.sociocat.services;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class PushNotificationsService extends FirebaseMessagingService {

    private final static String TAG = "PushNotifService";


    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        Log.d(TAG, "token: "+s);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
    }


    private void showNotification(RemoteMessage.Notification notification) {

    }
}
