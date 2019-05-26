package ru.aakumykov.me.sociocat.push_notifications_service;

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
import ru.aakumykov.me.sociocat.old_card_show.OldCardShow_View;
import ru.aakumykov.me.sociocat.singletons.AuthSingleton;

public class PushNotificationsService extends FirebaseMessagingService {

    private final static String TAG = "PushNotifService";


    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        //Log.d(TAG, "token: "+s);
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

        String notificationType = data.get("notification_type") + "";

        switch (notificationType) {
            case "new_card":
                showNewCardNotification(data);
                break;
            case "new_comment":
                showNewCommentNotification(data);
                break;
            default:
                Log.e(TAG, "Unknown notification type: '"+notificationType+"'");
        }
    }


    private void showNewCardNotification(Map<String,String> data) {

        String cardUserId = data.get("card_user_id");

        // Для определения дальнеших действий нужен userId
        if (null == cardUserId) {
            Log.e(TAG, "There is no card's user id in push notification: "+data);
            return;
        }

        // Не показываю уведомление автору карточки
        if (cardUserId.equals(AuthSingleton.currentUserId()))
            return;

        String text = data.get("text");
        String cardId = data.get("card_id");
        String cardUserName = data.get("card_user_name");

        String notificationTitle = getResources()
                .getString(R.string.PUSH_NOTIFICATION_SERVICE_new_card_created_title, text);
        String notificationMessage = getResources()
                .getString(R.string.PUSH_NOTIFICATION_SERVICE_new_card_created_message, cardUserName);

        Intent intent = new Intent(this, OldCardShow_View.class);
        intent.putExtra(Constants.CARD_KEY, cardId);

        showNotification(notificationTitle, notificationMessage, intent);
    }


    private void showNewCommentNotification(Map<String,String> data) {

        String text = data.get("text");
        String commentId = data.get("comment_id");
        String commentUserId = data.get("comment_user_id");
        String commentUserName = data.get("comment_user_name");

        if (null == commentUserId) {
            Log.e(TAG, "There is no comment's user id in push notification: "+data);
            return;
        }

        String cardId = data.get("card_id");

        String notificationTitle = getResources()
                .getString(R.string.PUSH_NOTIFICATION_SERVICE_new_comment_created_title, text);
        String notificationMessage = getResources()
                .getString(R.string.PUSH_NOTIFICATION_SERVICE_new_comment_created_message, commentUserName);

        // TODO: сделать переход к комментарию !
        Intent intent = new Intent(this, OldCardShow_View.class);
        intent.putExtra(Constants.CARD_KEY, cardId);
        intent.putExtra(Constants.COMMENT_KEY, commentId);

        showNotification(notificationTitle, notificationMessage, intent);
    }


    private void showNotification(String title, String message, Intent intent) {

        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_ONE_SHOT
        );

        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, Constants.SOCIOCAT_NOTIFICATIONS_CHANNEL)
                        .setSmallIcon(R.drawable.ic_notification_default)
                        .setContentTitle(title)
                        .setContentText(message)
                        .setAutoCancel(true)
                        .setSound(soundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (null != notificationManager)
            notificationManager.notify(0, notificationBuilder.build());

    }
}
