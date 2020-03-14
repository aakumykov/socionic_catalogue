package ru.aakumykov.me.sociocat.services;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.HashSet;
import java.util.Set;

import ru.aakumykov.me.sociocat.Constants;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.card_show.CardShow_View;
import ru.aakumykov.me.sociocat.event_bus_objects.NewCommentEvent;
import ru.aakumykov.me.sociocat.singletons.AuthSingleton;
import ru.aakumykov.me.sociocat.singletons.UsersSingleton;

public class NewCommentsService extends Service {

    // Service
    @Override
    public void onCreate() {
        super.onCreate();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    // Подписка на событие NewCommentEvent
    @Subscribe
    public void onNewCommentEvent(NewCommentEvent newCommentEvent) {
        showNewCommentNotification(newCommentEvent);
    }


    // Внутренние методы
    private void showNewCommentNotification(@NonNull NewCommentEvent newCommentEvent) {

        String currentUserId = AuthSingleton.currentUserId();
        if (null == currentUserId)
            return;

        if (currentUserId.equals(newCommentEvent.getUserId()))
            return;

        String commentKey = newCommentEvent.getCommentKey();
        String cardId = newCommentEvent.getCardId();

        int notificationId = commentKey.hashCode();

        Intent intent = new Intent(this, CardShow_View.class);
        intent.putExtra(Constants.CARD_KEY, cardId);
        intent.putExtra(Constants.COMMENT_KEY, commentKey);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                Constants.CODE_SHOW_COMMENT,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        String title = getString(R.string.NOTIFICATIONS_new_comment_notification_title, newCommentEvent.getUserName());

        String commentText = newCommentEvent.getText();

        NotificationCompat.BigTextStyle bigCommentText = new NotificationCompat.BigTextStyle().bigText(commentText);

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, Constants.NOTIFICATIONS_CHANNEL_AND_TOPIC_NAME_NEW_COMMENTS)
                        .setSmallIcon(R.drawable.ic_notification_new_comment)
                        .setContentTitle(title)
                        .setContentText(commentText)
                        .setStyle(bigCommentText)
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true);

        Notification notification = notificationBuilder.build();
        NotificationManagerCompat.from(this).notify(notificationId, notification);
    }

}
