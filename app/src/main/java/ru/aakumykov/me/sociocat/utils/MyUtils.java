package ru.aakumykov.me.sociocat.utils;

import android.app.Activity;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Point;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ru.aakumykov.me.sociocat.R;

import static android.content.Context.CLIPBOARD_SERVICE;

public final class MyUtils {

    private final static String TAG = "MyUtils";

    private MyUtils() {}

    public static void show(View view) {
        view.setVisibility(View.VISIBLE);
    }

    public static void hide(View view) {
        view.setVisibility(View.GONE);
    }

    public static void hide(View view, boolean makeInvisibleInsteadOfGone) {
        if (makeInvisibleInsteadOfGone)
            view.setVisibility(View.INVISIBLE);
        else
            MyUtils.hide(view);
    }

    public static void toggleVisibility(View view) {
        if (View.VISIBLE == view.getVisibility()) {
            MyUtils.show(view);
        }
        else {
            MyUtils.hide(view);
        }
    }

    public static void enable(View view) {
        view.setEnabled(true);
    }

    public static void disable(View view) {
        view.setEnabled(false);
    }

    public static String mime2ext(String mimeType) {
        if (null == mimeType) return null;

        Pattern pattern = Pattern.compile("^image/([a-z]+)$");
        Matcher matcher = pattern.matcher(mimeType);

        if (matcher.matches()) {
            return matcher.group(1);
        } else {
            return null;
        }
    }

    //    public static Card snapshot2card(DataSnapshot dataSnapshot) throws IllegalArgumentException {
//        Card card_edit = dataSnapshot.getValue(Card.class);
//        // TODO: протестировать с card_edit == null
//        if (null != card_edit) {
//            card_edit.setKey(dataSnapshot.getKey());
//        } else {
//            throw new IllegalArgumentException("Card object is null");
//        }
//        return card_edit;
//    }

//    public static String getMimeType(String url) {
//        String type = null;
//        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
//        if (extension != null) {
//            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
//        }
//        return type;
//    }

    public static <K, V> Map<K, V> mapDiff(Map<? extends K, ? extends V> left, Map<? extends K, ? extends V> right) {
        Map<K, V> difference = new HashMap<>();
        difference.putAll(left);
        difference.putAll(right);
        difference.entrySet().removeAll(right.entrySet());
        return difference;
    }

    public static <T> List<T> listDiff(List<T> list1, List<T> list2) {
        Set<T> set1 = new HashSet<>(list1);
        set1.removeAll(new HashSet<>(list2));
        return new ArrayList<>(set1);
    }


//    public static String getMimeTypeFromIntent(@Nullable Intent intent) throws IllegalArgumentException {
//
//        if (null == intent) throw new IllegalArgumentException("Supplied Intent is null");
//
//        ClipData clipData = intent.getClipData();
//        if (null == clipData) throw new IllegalArgumentException("ClipData is null");
//
//        ClipDescription clipDescription = clipData.getDescription();
//        if (null == clipDescription) throw new IllegalArgumentException("ClipDescription is null");
//
//        return clipDescription.getMimeType(0);
//    }

    public static String getMimeTypeFromIntent(@Nullable Intent intent) {

        if (null == intent) return null;

        ClipData clipData = intent.getClipData();
        if (null == clipData) return null;

        ClipDescription clipDescription = clipData.getDescription();
        if (null == clipDescription) return null;

        String mimeType = clipDescription.getMimeType(0);

        // TODO: проверять с помощью regexp-ов
//        if (mimeType.matches("^[a-z]+\\/[^a-z0-9.+-]+$")) return mimeType;
//        else

        return mimeType;
    }

    public static String cutToLength(@Nullable String text, Integer maxLength) {
        if (null == text) {
            return null;
        }
        if (null == maxLength) {
            maxLength = text.length();
        }
        return text.substring(0, Math.min(text.length(), maxLength));
    }


    public static void showKeyboard(Context ctx, EditText editText) {
        InputMethodManager imm = (InputMethodManager) ctx.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInput(editText, 0);
        }
    }

    public static void showKeyboardOnFocus(Context context, EditText editTextView) {

        editTextView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                if (null != imm)
                    imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
            }
        });

        editTextView.requestFocus();
    }

    public static void hideKeyboard(Context ctx, EditText editText) {
        InputMethodManager imm = (InputMethodManager) ctx.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(editText.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
        }
    }

    public static int getScreenWidth(Activity activity) {
        Display display = activity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.x;
    }
    public static int getScreenHeight(Activity activity) {
        Display display = activity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.y;
    }

    public static boolean isCorrectEmail(String email) {
        Pattern pattern = Pattern.compile("^([a-z0-9+_]+[.-]?)*[a-z0-9]+@([a-z0-9]+[.-]?)*[a-z0-9]+\\.[a-z]+$");
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public static <T> HashMap<T, Boolean> list2hashMap(Set<T> list, boolean defaultValue) {
        HashMap<T,Boolean> hashMap = new HashMap<>();
        for(T key : list)
            hashMap.put(key, defaultValue);
        return hashMap;
    }

    public static void setMargins (View v, int l, int t, int r, int b) {
        if (v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            p.setMargins(l, t, r, b);
            v.requestLayout();
        }
    }

    public static <T> String object2string(Context context, T arg) {
        return (arg instanceof Integer) ? context.getResources().getString((Integer)arg) : String.valueOf(arg);
    }

    public static String getClipboardText(Context context) {
        ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(CLIPBOARD_SERVICE);
        ClipData clipData = clipboardManager.getPrimaryClip();
        if (null != clipData && clipData.getItemCount() > 0) {
            ClipData.Item item = clipData.getItemAt(0);
            return item.getText().toString();
        } else {
            return null;
        }
    }

    public static int getOrientation(Context context) {
        Configuration configuration = context.getResources().getConfiguration();
        return configuration.orientation;
    }

    public static boolean isPortraitOrientation(Context context) {
        return Configuration.ORIENTATION_PORTRAIT == getOrientation(context);
    }


    // Всплывающие сообщения
    public static void showCustomToast(Context context, int messageId) {
        String message = context.getResources().getString(messageId);
        showCustomToast(context, message);
    }

    public static void showCustomToast(Context context, String message) {
        showCustomToast(context, message, Toast.LENGTH_SHORT);
    }

    public static void showLongCustomToast(Context context, int messageId) {
        String message = context.getResources().getString(messageId);
        showCustomToast(context, message, Toast.LENGTH_LONG);
    }

    public static void showLongCustomToast(Context context, String message) {
        showCustomToast(context, message, Toast.LENGTH_LONG);
    }

    private static void showCustomToast(Context context, String message, int toastShowLength) {
        LayoutInflater myInflater = LayoutInflater.from(context);
        View view = myInflater.inflate(R.layout.toast, null);

        TextView textView = view.findViewById(R.id.messageView);
        textView.setText(message);

        Toast mytoast = new Toast(context);
        mytoast.setView(view);
        mytoast.setDuration(toastShowLength);
        mytoast.show();
    }


    public static String stackTrace2String(StackTraceElement[] stackTraceElements) {
        String stackTrace = "";
        for (StackTraceElement element : stackTraceElements)
            stackTrace += element.toString() + "\n";
        return stackTrace;
    }

    public static String getString(Context context, int stringResourceId) {
        return context.getResources().getString(stringResourceId);
    }

    public static String getStringWithStringResource(Context context, int baseStringResourceId, int insertedStringResourceId) {
        String innerString = getString(context, insertedStringResourceId);
        return getStringWithString(context, baseStringResourceId, innerString);
    }

    public static String getStringWithString(Context context, int stringResourceId, String insertedText) {
        return context.getResources().getString(stringResourceId, insertedText);
    }

    public static String getStringWithNumber(Context context, int stringResourceId, int number) {
        return context.getResources().getString(stringResourceId, number);
    }

    public static String getStringWithMultipleStrings(Context context, int stringResourceId, String... insertedTextPieces) {
        return context.getResources().getString(stringResourceId, insertedTextPieces);
    }

    public static String seconds2HHMMSS(Double seconds) {
        try {
            int sec = new BigDecimal(seconds).intValue();
            return seconds2HHMMSS(sec);
        }
        catch (Exception e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
            return "00:00:00";
        }
    }

    public static String seconds2HHMMSS(Float seconds) {
        try {
            int sec = new BigDecimal(seconds).intValue();
            return seconds2HHMMSS(sec);
        }
        catch (Exception e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
            return "00:00:00";
        }
    }

    public static String seconds2HHMMSS(int seconds) {
        try {
            TimeZone tz = TimeZone.getTimeZone("UTC");
            SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss", Locale.US);
            df.setTimeZone(tz);
            return df.format(new Date(seconds * 1000));
        }
        catch (Exception e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
            return "00:00:00";
        }
    }

    public static float double2float(Double timecode) {
        return new BigDecimal(timecode).floatValue();
    }

    public static Notification prepareNotification(
            Context context,
            String channelId,
            int iconId,
            String title,
            @Nullable String text,
            boolean withProgressBar,
            boolean isOngoing
    ) {
        return prepareNotification(
                context,
                channelId,
                iconId,
                title,
                text,
                withProgressBar,
                isOngoing,
                null
        );
    }

    public static Notification prepareNotification(
            Context context,
            String channelId,
            int iconId,
            String title,
            @Nullable String text,
            boolean withProgressBar,
            boolean isOngoing,
            @Nullable PendingIntent pendingIntent
    )
    {
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context, channelId)
                        .setSmallIcon(iconId)
                        .setContentTitle(title)
                        .setAutoCancel(true);

        if (null != text)
            builder.setContentText(text);

        if (null != pendingIntent)
            builder.setContentIntent(pendingIntent);

        if (isOngoing)
            builder.setOngoing(true);

        return builder.build();
    }

    // MD5Sum
    public static String md5sum(String string) {
        MessageDigest messageDigest = null;
        byte[] digest = new byte[0];

        try {
            messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            messageDigest.update(string.getBytes());
            digest = messageDigest.digest();
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }

        BigInteger bigInt = new BigInteger(1, digest);
        StringBuilder md5Hex = new StringBuilder(bigInt.toString(16));

        while( md5Hex.length() < 32 ){
            md5Hex.insert(0, "0");
        }

        return md5Hex.toString();
    }

    // Обработка исключений
    public static String processException(String logTag, Exception e) {
        String errorMsg = e.getMessage();

        if (null == errorMsg)
            errorMsg = e.toString();

        Log.e(logTag, errorMsg);

        Log.e(logTag, TextUtils.join("\n", e.getStackTrace()));

        return errorMsg;
    }

    public static String getExceptionMessage(Exception e) {
        String errorMsg = e.getMessage();
        if (null == errorMsg)
            errorMsg = e.toString();
        return errorMsg;
    }

    public static String quoteString(Context context, String dirName) {
        return context.getResources().getString(R.string.aquotes, dirName);
    }

    public static String getHumanTimeAgo(Context context, Long timestamp, @Nullable Integer stringToEmbedTime_ResourceId) {
        CharSequence relatedTimeCharSequence = DateUtils.getRelativeTimeSpanString(timestamp, new Date().getTime(), DateUtils.SECOND_IN_MILLIS);

        String relatedTimeString = relatedTimeCharSequence.toString().toLowerCase();

        return (null != stringToEmbedTime_ResourceId) ?
                context.getString(stringToEmbedTime_ResourceId, relatedTimeString)
                : relatedTimeString;
    }

    public static <T> void printError(@NonNull String tag, @Nullable T e) {

        if (null != e)
        {
            if (e instanceof Exception) {
                Exception exception = ((Exception) e);

                String msg = exception.getMessage();

                if (null != msg)
                    Log.e(tag, msg);

                for (StackTraceElement stackTraceElement : exception.getStackTrace()) {
                    Log.e(tag, stackTraceElement.toString());
                }
            } else {
                Log.e(tag, String.valueOf(e));
            }
        }
    }

    public static int random(int min, int max) {
        return new Random().nextInt((max - min) + 1) + min;
    }

    public static long getCurrentTimeInSeconds() {
        return new Date().getTime() / 1000;
    }

    /*public static long getCurrentTime(TimeUnit timeUnit) {
        long time = new Date().getTime();

        switch (timeUnit) {
            case SECONDS:
                return TimeUnit.MILLISECONDS.toSeconds(time);
            case MINUTES:
                return TimeUnit.MILLISECONDS.toMinutes(time);
            case HOURS:
                return TimeUnit.MILLISECONDS.toHours(time);
            case DAYS:
                return TimeUnit.MILLISECONDS.toDays(time);
            default:
                throw new RuntimeException(timeUnit+" is not supported");
        }
    }*/
}
