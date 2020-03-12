package ru.aakumykov.me.sociocat.push_notifications;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.greenrobot.eventbus.EventBus;

import java.util.Map;

import ru.aakumykov.me.sociocat.event_bus_objects.NewCardEvent;
import ru.aakumykov.me.sociocat.models.Card;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "FCM_Service";

    public MyFirebaseMessagingService() {
        Log.d(TAG, "new FCMService()");
    }

    // Service
    @Override
    public void onCreate() {
        super.onCreate();
    }


    // FirebaseMessagingService
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Map<String, String> data = remoteMessage.getData();

        if (data.containsKey("isCardNotification")) {
            String cardKey = data.get(Card.KEY_KEY);
            EventBus.getDefault().post(new NewCardEvent(cardKey));
        }
    }

    @Override
    public void onDeletedMessages() {
        super.onDeletedMessages();
        Log.e(TAG, "onDeletedMessages()");
    }

    @Override
    public void onMessageSent(@NonNull String s) {
        super.onMessageSent(s);
        Log.e(TAG, "onMessageSent(), "+s);
    }

    @Override
    public void onSendError(@NonNull String s, @NonNull Exception e) {
        super.onSendError(s, e);
        Log.d(TAG, "onSendError(), "+s);
        Log.w(TAG, e);
    }

    @Override
    public void onNewToken(@NonNull String token) {
        Log.d(TAG, "New token: " + token);
        //sendRegistrationToServer(token);
    }

}
