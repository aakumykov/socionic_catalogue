package ru.aakumykov.me.mvp.utils;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ru.aakumykov.me.mvp.Constants;

public class MVPUtils {

    private final static String TAG = "MVPUtils";
    private static Map<String,String> regexMap = new HashMap<>();
    private static List<String> correctCardTypes = new ArrayList<>();

    static {
        correctCardTypes.add(Constants.TEXT_CARD);
        correctCardTypes.add(Constants.IMAGE_CARD);
        correctCardTypes.add(Constants.VIDEO_CARD);
        correctCardTypes.add(Constants.AUDIO_CARD);
    }

    /* Все регулярные выражения для применения к URL/видео-кодам YouTube
     * обязаны выделять код видео в ПЕРВОЙ группе. */
    static {
        regexMap.put("youtube1", "^https?://youtube\\.com/watch\\?v=([^=?&]+)");
        regexMap.put("youtube2", "^https?://www\\.youtube\\.com/watch\\?v=([^=?&]+)");
        regexMap.put("youtube3", "^https?://youtu.be/([^/]+)$");
    }

    private MVPUtils(){}

    public static String detectInputDataMode(Intent intent) {

        if (null == intent)
            return "NULL";

        String type = intent.getType() + ""; // для превращения NULL в пустую строку

        if (type.equals("text/plain")) {
            String extraText = intent.getStringExtra(Intent.EXTRA_TEXT);

            if (MVPUtils.isYoutubeLink(extraText)) {
                return "YOUTUBE_VIDEO";
            } else {
                return "TEXT";
            }
        }
        else if (type.startsWith("image/")) {
            return "IMAGE";
        }
        else {
            return "UNKNOWN";
        }
    }

    public static boolean isYoutubeLink(String text) {

        text = text.trim();

        for(Map.Entry<String,String> entry : regexMap.entrySet()) {
            String key = entry.getKey();
            String regex = entry.getValue();
            if (text.matches(regex)) return true;
        }

        return false;
    }

    public static String extractYoutubeVideoCode(String link) {
        link = ""+link.trim();

        Map<String,String> patternsMap = new HashMap<>();
        patternsMap.put("simpleVideoCode", "^([\\w-]+)$");
        patternsMap.putAll(regexMap);

        for (Map.Entry<String,String> entry : patternsMap.entrySet()) {
            Pattern p = Pattern.compile(entry.getValue());
            Matcher m = p.matcher(link);
            if (m.find()) return m.group(1);
        }

        return null;
    }

    public static String normalizeTag(String tagName) {

        // обрезаю черезмерно длинные
//        if (tagName.length() > Constants.TAG_MAX_LENGTH) {
//            tagName = tagName.substring(
//                    0,
//                    Math.min(tagName.length(),Constants.TAG_MAX_LENGTH)
//            );
//        }
        tagName = MyUtils.cutToLength(tagName, Constants.TAG_MAX_LENGTH);

        // отпинываю слишком короткия
        if (tagName.length() < Constants.TAG_MIN_LENGTH) {
            return null;
        }

        // перевожу в нижний регистр
        tagName = tagName.toLowerCase();

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

    public static boolean isCorrectCardType(String cardType) {
        return correctCardTypes.contains(cardType);
    }
}
