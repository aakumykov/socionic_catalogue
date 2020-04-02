package ru.aakumykov.me.sociocat.push_notifications;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.greenrobot.eventbus.EventBus;

import java.util.Map;

import ru.aakumykov.me.sociocat.event_bus_objects.NewCardEvent;
import ru.aakumykov.me.sociocat.event_bus_objects.NewCommentEvent;
import ru.aakumykov.me.sociocat.models.Card;
import ru.aakumykov.me.sociocat.models.Comment;

public class FCMService extends FirebaseMessagingService {

    private static final String TAG = "FCM_Service";

    // FirebaseMessagingService
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Map<String, String> data = remoteMessage.getData();

        if (data.containsKey("isCardNotification")) {
            processNewCardNotification(data);
        }
        else if (data.containsKey("isCommentNotification")) {
            processNewCommentNotification(data);
        }
        else {
            Log.e(TAG, "Unknown push notification type: "+data);
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


    // Внутренние методы
    private void processNewCardNotification(Map<String, String> data) {
        NewCardEvent newCardEvent = new NewCardEvent();
            newCardEvent.setKey(data.get(Card.KEY_KEY));
            newCardEvent.setTitle(data.get(Card.KEY_TITLE));
            newCardEvent.setUserId(data.get(Card.KEY_USER_ID));
            newCardEvent.setUserName(data.get(Card.KEY_USER_NAME));

        EventBus.getDefault().post(newCardEvent);
    }

    private void processNewCommentNotification(Map<String, String> data) {
        NewCommentEvent newCommentEvent = new NewCommentEvent();
            newCommentEvent.setCommentKey(data.get(Comment.KEY_KEY));
            newCommentEvent.setText(data.get(Comment.KEY_TEXT));
            newCommentEvent.setUserId(data.get(Comment.KEY_USER_ID));
            newCommentEvent.setUserName(data.get(Comment.KEY_USER_NAME));
            newCommentEvent.setCardId(data.get(Comment.KEY_CARD_ID));
            newCommentEvent.setCardTitle(data.get(Comment.KEY_CARD_TITLE));

        EventBus.getDefault().post(newCommentEvent);
    }

}
