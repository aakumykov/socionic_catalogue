package ru.aakumykov.me.sociocat.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.greenrobot.eventbus.EventBus;

import java.util.Map;

import ru.aakumykov.me.sociocat.Constants;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.event_bus_objects.NewCardEvent;
import ru.aakumykov.me.sociocat.models.Card;

public class FCMService extends FirebaseMessagingService {

    private static final String TAG = "FCMService";

    public FCMService() {
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


    // Внтуренние методы
    /*private void sendRegistrationToServer(String token) {
    }*/

    /*private boolean isNotificationChannelEnabled(Context context, @Nullable String channelId){
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O)
            return NotificationManagerCompat.from(context).areNotificationsEnabled();

        if(TextUtils.isEmpty(channelId))
            return false;

        NotificationManagerCompat notificationManagerCompat =  NotificationManagerCompat.from(context);
        NotificationChannel channel = notificationManagerCompat.getNotificationChannel(channelId);
        if (null != channel)
            return channel.getImportance() != NotificationManager.IMPORTANCE_NONE;
        else
            return false;
    }*/
}
