package io.gitlab.aakumykov.sociocat.push_notifications;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

import io.gitlab.aakumykov.sociocat.constants.PreferencesConstants;
import io.gitlab.aakumykov.sociocat.event_bus_objects.NewCardEvent;
import io.gitlab.aakumykov.sociocat.event_bus_objects.NewCommentEvent;
import io.gitlab.aakumykov.sociocat.models.Card;
import io.gitlab.aakumykov.sociocat.models.Comment;

public class FCMService extends FirebaseMessagingService {

    private static final String TAG = "FCM_Service";
    private String mCurrentUserId;

    @Override
    public void onCreate() {
        super.onCreate();

        mCurrentUserId = PreferenceManager
                .getDefaultSharedPreferences(this)
                .getString(PreferencesConstants.key_current_user_id, null);
    }

    // FirebaseMessagingService
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Map<String, String> data = remoteMessage.getData();

        //logDataToFile(data);

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

        NewCardNotification_Helper.processNotification(
                this, newCardEvent, mCurrentUserId);
    }

    private void processNewCommentNotification(Map<String, String> data) {

        NewCommentEvent newCommentEvent = new NewCommentEvent();
            newCommentEvent.setCommentKey(data.get(Comment.KEY_KEY));
            newCommentEvent.setText(data.get(Comment.KEY_TEXT));
            newCommentEvent.setUserId(data.get(Comment.KEY_USER_ID));
            newCommentEvent.setUserName(data.get(Comment.KEY_USER_NAME));
            newCommentEvent.setCardId(data.get(Comment.KEY_CARD_ID));
            newCommentEvent.setCardTitle(data.get(Comment.KEY_CARD_TITLE));

        NewCommentNotification_Helper.processNotification(
                this, newCommentEvent, mCurrentUserId);
    }

    private void logDataToFile(Map<String, String> data) {

        String filename = "FCMService-debug.log";
        String fileContents = data.toString();

        try (FileOutputStream fos = openFileOutput(filename, Context.MODE_PRIVATE)) {
            fos.write(fileContents.getBytes());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
