package ru.aakumykov.me.mvp.utils;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.view.View;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class MyUtils {

//    private final static String TAG = "MyUtils";

    private MyUtils() {}

    public static void show(View view) {
        view.setVisibility(View.VISIBLE);
    }

    public static void hide(View view) {
        view.setVisibility(View.GONE);
    }

    public static void enable(View view) {
        view.setEnabled(true);
    }

    public static void disable(View view) {
        view.setEnabled(false);
    }

    public static String mime2ext(String mimeType) {
        Pattern pattern = Pattern.compile("^image/([a-z]+)$");
        Matcher matcher = pattern.matcher(mimeType);
        if (matcher.matches()) {
            return matcher.group(1);
        } else {
            return null;
        }
    }

//    public static Card snapshot2card(DataSnapshot dataSnapshot) throws IllegalArgumentException {
//        Card card = dataSnapshot.getValue(Card.class);
//        // TODO: протестировать с card == null
//        if (null != card) {
//            card.setKey(dataSnapshot.getKey());
//        } else {
//            throw new IllegalArgumentException("Card object is null");
//        }
//        return card;
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

    public static String normalizeTag(String tagName) {

        // удаляю концевые пробелы
        tagName = tagName.replaceAll("^\\s+|\\s+$", "");

        // удаляю концевые запрещённые символы (пока не работает с [], а может, и чем-то ещё)
//        tagName = tagName.replace("^/+|/+$", "");
//        tagName = tagName.replace("^\\.+|\\.+$", "");
//        tagName = tagName.replace("^#+|#+$", "");
//        tagName = tagName.replace("^$+|$+$", "");
//        tagName = tagName.replace("^\\[*|\\[*$", "");
//        tagName = tagName.replace("^\\]*|\\]*[m$", "");

        // заменяю внутренние запрещённые символы
        tagName = tagName.replace("/", "_");
        tagName = tagName.replace(".", "_");
        tagName = tagName.replace("#", "_");
        tagName = tagName.replace("$", "_");
        tagName = tagName.replace("[", "_");
        tagName = tagName.replace("]", "_");

        // преобразую число в строку
        if (tagName.matches("^[0-9]+$")) tagName = "_"+tagName+"_";

        return tagName;
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
}
