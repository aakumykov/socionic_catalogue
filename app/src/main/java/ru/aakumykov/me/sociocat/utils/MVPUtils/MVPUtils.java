package ru.aakumykov.me.sociocat.utils.MVPUtils;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.Gravity;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;
import ru.aakumykov.me.sociocat.Config;
import ru.aakumykov.me.sociocat.Constants;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.card_edit.DraftRestoreFragment;
import ru.aakumykov.me.sociocat.models.Card;
import ru.aakumykov.me.sociocat.utils.MyUtils;

public class MVPUtils {

    private final static String TAG = "MVPUtils";
    private static Map<String,String> youtubePatterns = new HashMap<>();
    private static Map<String,String> imagePatterns = new HashMap<>();
    private static List<String> correctCardTypes = new ArrayList<>();
    private static final List<String> upperCaseTags = new ArrayList<>();

    private static final Map<String,Integer> aspectsMap = new HashMap<>();

    static {
        correctCardTypes.add(Constants.TEXT_CARD);
        correctCardTypes.add(Constants.IMAGE_CARD);
        correctCardTypes.add(Constants.VIDEO_CARD);
        correctCardTypes.add(Constants.AUDIO_CARD);
    }

    static {
        /* Все регулярные выражения для применения к URL/видео-кодам YouTube
         * обязаны выделять код видео в _первой_ группе. */
        youtubePatterns.put("youtube1", Config.YOUTUBE_LONG_LINK_REGEX_2);
        youtubePatterns.put("youtube2", Config.YOUTUBE_LONG_LINK_REGEX_1);
        youtubePatterns.put("youtube3", Config.YOUTUBE_SHORT_LINK_REGEX);
        youtubePatterns.put("youtube4", Config.YOUTUBE_CODE_REGEX);
    }

    static {
        String prefix = "^https?://.+";
        imagePatterns.put("jpg", prefix +"\\.jpe?g$");
        imagePatterns.put("png", prefix +"\\.png$");
        imagePatterns.put("gif", prefix +"\\.gif$");
        imagePatterns.put("bmp", prefix +"\\.bmp$");
    }

    static {
        upperCaseTags.add("БЛ");
        upperCaseTags.add("ЧИ");
        upperCaseTags.add("БЭ");
        upperCaseTags.add("ЧС");
        upperCaseTags.add("ЧЭ");
        upperCaseTags.add("БС");
        upperCaseTags.add("ЧЛ");
        upperCaseTags.add("БИ");
    }

    static {
        aspectsMap.put("ЧЭ", R.drawable.aspect_emotion);
        aspectsMap.put("ЧС", R.drawable.aspect_force);
        aspectsMap.put("ЧИ", R.drawable.aspect_intuition);
        aspectsMap.put("БЛ", R.drawable.aspect_logic);
        aspectsMap.put("ЧЛ", R.drawable.aspect_practice);
        aspectsMap.put("БЭ", R.drawable.aspect_relation);
        aspectsMap.put("БС", R.drawable.aspect_sence);
        aspectsMap.put("БИ", R.drawable.aspect_time);
    }


    private MVPUtils(){}


    public static String detectInputDataMode(Intent intent) {

        if (null == intent)
            return "NULL";

        String type = intent.getType() + "";
        String extraText = intent.getStringExtra(Intent.EXTRA_TEXT) + "";

        if (type.equals("text/plain")) {

            String text = intent.getStringExtra(Intent.EXTRA_TEXT);

            if (isLinkToImage(text)) {
                return Constants.MIME_TYPE_IMAGE_LINK;
            }
            else if (MVPUtils.isYoutubeLink(text)) {
                return Constants.MIME_TYPE_YOUTUBE_VIDEO;
            }
            else {
                return Constants.MIME_TYPE_TEXT;
            }
        }
        else if (type.startsWith("image/") && isLinkToImage(extraText)) {
            return Constants.MIME_TYPE_IMAGE_LINK;
        }
        else if (type.startsWith("image/")) {
            return Constants.MIME_TYPE_IMAGE_DATA;
        }
        else {
            return Constants.MIME_TYPE_UNKNOWN;
        }
    }

    public static boolean isYoutubeLink(String text) {
        text = text.trim();
        for (Map.Entry<String,String> entry : youtubePatterns.entrySet()) {
            String regex = entry.getValue();
            if (text.matches(regex)) return true;
        }
        return false;
    }

    private static boolean isLinkToImage(String text) {
        text = text.trim().toLowerCase();
        for (Map.Entry<String,String> entry : imagePatterns.entrySet()) {
            if (text.matches(entry.getValue())) return true;
        }
        return false;
    }

    public static String extractYoutubeVideoCode(String link) {
        link = ""+link.trim();

        for (Map.Entry<String,String> entry : youtubePatterns.entrySet()) {
            Pattern p = Pattern.compile(entry.getValue());
            Matcher m = p.matcher(link);
            if (m.find()) return m.group(1);
        }

        return null;
    }

    public static String normalizeTag(String tag) {

        // удаляю концевые пробелы
        tag = tag.trim();
        if (TextUtils.isEmpty(tag))
            return null;

        // отклоняю слишком короткие
        if (tag.length() < Constants.TAG_MIN_LENGTH) {
            return null;
        }

        // укорачиваю черезмерно длинные
        tag = MyUtils.cutToLength(tag, Constants.TAG_MAX_LENGTH);

        // перевожу в нижний регистр
        if (!upperCaseTags.contains(tag)) {
            tag = tag.toLowerCase();
        }

        // ещё раз удаляю концевые пробелы
//        tag = tag.replaceAll("^\\s+|\\s+$", "");
        tag = tag.trim();

        // удаляю концевые запрещённые символы (пока не работает с [], а может, и чем-то ещё)
//        tagName = tagName.replace("^/+|/+$", "");
//        tagName = tagName.replace("^\\.+|\\.+$", "");
//        tagName = tagName.replace("^#+|#+$", "");
//        tagName = tagName.replace("^$+|$+$", "");
//        tagName = tagName.replace("^\\[*|\\[*$", "");
//        tagName = tagName.replace("^\\]*|\\]*[m$", "");

        // заменяю внутренние запрещённые символы
        tag = tag.replace("/", "_");
        tag = tag.replace(".", "_");
        tag = tag.replace("#", "_");
        tag = tag.replace("$", "_");
        tag = tag.replace("[", "_");
        tag = tag.replace("]", "_");

        // преобразую число в строку
        if (tag.matches("^\\d+$")) tag = Constants.TAG_NUMBER_BRACE+tag+Constants.TAG_NUMBER_BRACE;

        return tag;
    }

    public static boolean isCorrectCardType(String cardType) {
        return correctCardTypes.contains(cardType);
    }

    public static void loadImageWithResizeInto(
            final Uri imageURI,
            final ImageView targetImageView,
            final boolean unprocessedYet,
            final Integer targetWidth,
            final Integer targetHeight,
            final iMVPUtils.ImageLoadWithResizeCallbacks callbacks

    ) throws Exception
    {
        Picasso.get().load(imageURI)
                .into(targetImageView, new Callback() {

            @Override
            public void onSuccess() {

                // Изменение размера картинки, если это первая загрузка "с диска"
                if (unprocessedYet) {
                    Drawable drawable = targetImageView.getDrawable();
                    int initialWidth = drawable.getIntrinsicWidth();
                    int initialHeight = drawable.getIntrinsicHeight();

                    int destWidth = 0;
                    int destHeight = 0;

                    if (initialWidth >= initialHeight) {
                        destWidth = (initialWidth > targetWidth) ? targetWidth : initialWidth;
                        destHeight = 0;
                    } else {
                        destWidth = 0;
                        destHeight = (initialHeight > targetHeight) ? targetHeight : initialHeight;
                    }

                    Picasso.get().load(imageURI)
                            .resize(destWidth, destHeight)
                            .into(targetImageView, new Callback() {
                                @Override
                                public void onSuccess() {
                                    Drawable drawable = targetImageView.getDrawable();
                                    int width = drawable.getIntrinsicWidth();
                                    int height = drawable.getIntrinsicHeight();

                                    FileInfo fileInfo = new FileInfo(width, height);

                                    callbacks.onImageLoadWithResizeSuccess(fileInfo);
                                }

                                @Override
                                public void onError(Exception e) {

                                }
                            });
                }
                else {
                    Drawable drawable = targetImageView.getDrawable();
                    int width = drawable.getIntrinsicWidth();
                    int height = drawable.getIntrinsicHeight();
                    callbacks.onImageLoadWithResizeSuccess(new FileInfo(width, height));
                }
            }

            @Override
            public void onError(Exception e) {
                callbacks.onImageLoadWithResizeFail(e.getMessage());
                e.printStackTrace();
            }
        });
    }

    public static String uri2ext(Context context, Uri uri) {
        ContentResolver contentResolver =  context.getContentResolver();
        String mimeType = contentResolver.getType(uri);
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType);
    }

    public static byte[] compressImage(Bitmap bitmap, String imageType) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Bitmap.CompressFormat compressFormat = Bitmap.CompressFormat.JPEG;

        switch (imageType) {
            case "png":
                compressFormat = Bitmap.CompressFormat.PNG;
                break;
            case "webp":
                // TODO: проверить
                compressFormat = Bitmap.CompressFormat.WEBP;
        }

        bitmap.compress(compressFormat, Config.DEFAULT_JPEG_QUALITY, baos);
        return baos.toByteArray();
    }

    public static SpannableString aspects2images(Context context, String inputText) {

        // Текст --> spannableString
        SpannableString spannableString = new SpannableString(inputText);

        // Перебираю массив соответствий "аббривеатура -> иконка"
        for (Map.Entry<String, Integer> entry : aspectsMap.entrySet()) {

            String aspectText = entry.getKey();
            ImageSpan aspectImage = new ImageSpan(context, entry.getValue());

            // Позиция первого вхождния текстового обозначения аспекта
            int startPosition = inputText.indexOf(aspectText);

            // Заменяю все встречающиеся вхождения
            while (startPosition > -1) {
                int endPosition = startPosition + aspectText.length();
                spannableString.setSpan(aspectImage, startPosition, endPosition, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                startPosition = inputText.indexOf(aspectText, endPosition);
            }
        }

        return spannableString;
    }


    public interface SubscribeToTopicCallbacks {
        void onTopicNotificationsSubscribed();
        void onTopicNotificationsUnsubscribed();
        void onTopicNotificationsError(String errorMsg);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private static void setupNewCardsNotificationChannel(Context context, boolean doEnable){

        CharSequence sociocatChannelName = context.getString(R.string.NOTIFICATIONS_sociocat_channel_name);
        String sociocatChannelDescription = context.getString(R.string.NOTIFICATIONS_sociocat_channel_description);

        NotificationChannel adminChannel;
        adminChannel = new NotificationChannel(Constants.NEW_CARDS_NOTIFICATIONS_CHANNEL_ID, sociocatChannelName, NotificationManager.IMPORTANCE_LOW);
        adminChannel.setDescription(sociocatChannelDescription);
//        adminChannel.enableLights(true);
//        adminChannel.setLightColor(Color.RED);
        adminChannel.enableVibration(true);

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (notificationManager != null) {
            if (doEnable) notificationManager.createNotificationChannel(adminChannel);
            else notificationManager.deleteNotificationChannel(Constants.NEW_CARDS_NOTIFICATIONS_CHANNEL_ID);
        }
    }

    public static void subscribeToTopicNotifications(Context context, String topicName,
           @Nullable Integer successMessageId, @Nullable Integer errorMessageId, SubscribeToTopicCallbacks callbacks) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            setupNewCardsNotificationChannel(context, true);

        FirebaseMessaging.getInstance().subscribeToTopic(topicName)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        callbacks.onTopicNotificationsSubscribed();
                        if (null != successMessageId)
                            showToast(context, successMessageId, true);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        callbacks.onTopicNotificationsError(e.getMessage());
                        if (null != errorMessageId)
                            showToast(context, errorMessageId, true);
                        Log.e(TAG, "Error subscribing to topic '" + topicName + "': " + e.getMessage());
                        e.printStackTrace();
                    }
                });
    }

    public static void unsubscribeFromTopicNotifications(Context context, String topicName,
           @Nullable Integer successMessageId, @Nullable Integer errorMessageId, SubscribeToTopicCallbacks callbacks) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            setupNewCardsNotificationChannel(context, true);

        FirebaseMessaging.getInstance().unsubscribeFromTopic(topicName)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        callbacks.onTopicNotificationsUnsubscribed();
                        if (null != successMessageId)
                            showToast(context, successMessageId, true);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        callbacks.onTopicNotificationsError(e.getMessage());
                        if (null != errorMessageId)
                            showToast(context, errorMessageId, true);
                        Log.e(TAG, "Error unsubscribing from topic '" + topicName + "': " + e.getMessage());
                        e.printStackTrace();
                    }
                });
    }



    public static boolean hasCardDraft(Context context) {
        SharedPreferences sharedPreferences =
                context.getSharedPreferences(Constants.SHARED_PREFERENCES_CARD_EDIT, Context.MODE_PRIVATE);
        return sharedPreferences.contains(Constants.CARD_DRAFT);
    }

    public static void saveCardDraft(Context context, Card card) {
        SharedPreferences.Editor editor =
                context.getSharedPreferences(Constants.SHARED_PREFERENCES_CARD_EDIT, Context.MODE_PRIVATE)
                .edit();

        String cardJSON = new Gson().toJson(card);

        editor.putString(Constants.CARD_DRAFT, cardJSON);

        editor.apply();
    }

    public static Card retriveCardDraft(Context context) {
        SharedPreferences sharedPreferences =
                context.getSharedPreferences(Constants.SHARED_PREFERENCES_CARD_EDIT, Context.MODE_PRIVATE);

        if (!sharedPreferences.contains(Constants.CARD_DRAFT))
            return null;

        String cardJSON = sharedPreferences.getString(Constants.CARD_DRAFT, "");
        return new Gson().fromJson(cardJSON, Card.class);
    }

    public static void clearCardDraft(@Nullable Context context) {
        if (null != context) {
            SharedPreferences.Editor editor =
                    context.getSharedPreferences(Constants.SHARED_PREFERENCES_CARD_EDIT, Context.MODE_PRIVATE)
                            .edit();
            editor.remove(Constants.CARD_DRAFT);
            editor.apply();
        }
    }

    public static void showDraftRestoreDialog(FragmentManager fragmentManager, Card cardDraft, DraftRestoreFragment.Callbacks callbacks) {

        DraftRestoreFragment draftRestoreFragment =
                DraftRestoreFragment.getInstance(cardDraft);

        draftRestoreFragment.setCallbacks(callbacks);

        draftRestoreFragment.show(fragmentManager, "draft_restore_dialog");
    }


    // TODO: перенести showToast из BaseView
    public static <T> void showToast(Context context, T messageId, boolean atCenter) {
        String message = (messageId instanceof Integer) ?
                context.getResources().getString((Integer) messageId) :
                String.valueOf(messageId);

        Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);

        if (atCenter)
            toast.setGravity(Gravity.CENTER, 0,0);

        toast.show();
    }
}
