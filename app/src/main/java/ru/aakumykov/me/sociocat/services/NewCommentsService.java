package ru.aakumykov.me.sociocat.services;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import ru.aakumykov.me.sociocat.Constants;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.card_show.CardShow_View;
import ru.aakumykov.me.sociocat.event_bus_objects.NewCommentEvent;
import ru.aakumykov.me.sociocat.singletons.AuthSingleton;

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

        Notification notification = prepareNotification(newCommentEvent);

        int notificationId = newCommentEvent.getCommentKey().hashCode();

        NotificationManagerCompat.from(this).notify(notificationId, notification);
    }

    private Notification prepareNotification(NewCommentEvent newCommentEvent) {

        String title = getString(R.string.NOTIFICATIONS_new_comment_notification_title, newCommentEvent.getCardTitle());

        String shortMessage = getString(R.string.NOTIFICATIONS_new_comment_short_message, newCommentEvent.getText());

        String longMessage = newCommentEvent.getText();

        PendingIntent pendingIntent = preparePendingIntent(newCommentEvent);

        return new NotificationCompat.Builder(this, Constants.NOTIFICATIONS_CHANNEL_AND_TOPIC_NAME_NEW_COMMENTS)
                        .setSmallIcon(R.drawable.ic_notification_new_comment)
                        .setContentTitle(title)
                        .setContentText(shortMessage)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(longMessage))
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true)
                .build();
    }

    private PendingIntent preparePendingIntent(NewCommentEvent newCommentEvent) {

        Intent intent = new Intent(this, CardShow_View.class);
        intent.putExtra(Constants.CARD_KEY, newCommentEvent.getCardId());
        intent.putExtra(Constants.COMMENT_KEY, newCommentEvent.getCommentKey());

        return PendingIntent.getActivity(
                this,
                Constants.CODE_SHOW_COMMENT,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );
    }

}
