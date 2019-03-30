package ru.aakumykov.me.sociocat.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import ru.aakumykov.me.sociocat.Constants;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.card_show.CardShow_View;
import ru.aakumykov.me.sociocat.interfaces.iAuthSingleton;

public class PushNotificationsService extends FirebaseMessagingService {

    private final static String TAG = "PushNotifService";
    private iAuthSingleton authService = AuthSingleton.getInstance();

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate()");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy()");
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "onUnbind()");
        return super.onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
        Log.d(TAG, "onRebind()");
    }


    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        Log.d(TAG, "token: "+s);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        try {
            showNotification(remoteMessage.getData());
        } catch (Exception e) {
            // TODO: как собирать эту ошибку?
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        }
    }

    private void showNotification(@Nullable Map<String,String> data) throws Exception {

        if (null == data) {
            Log.w(TAG, "There is no data in push notification: "+data);
            return;
        }

        String cardUserId = data.get("card_user_id");
        if (null == cardUserId) {
            Log.w(TAG, "There is no card's user id in push notification: "+data);
            return;
        }

        // Не показываю уведомление автору карточки
        if (cardUserId.equals(authService.currentUserId())) {
            return;
        }

        String title = getResources().getString(R.string.PUSH_NOTIFICATION_SERVICE_new_card_created);
        String cardKey = data.get("card_key");
        String cardTitle = data.get("card_title");

        Intent intent = new Intent(this, CardShow_View.class);
        intent.putExtra(Constants.CARD_KEY, cardKey);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_ONE_SHOT
        );

        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_notification_default)
                .setContentTitle(title)
                .setContentText(cardTitle)
                .setAutoCancel(true)
                .setSound(soundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (null != notificationManager)
            notificationManager.notify(0, notificationBuilder.build());
    }
}
