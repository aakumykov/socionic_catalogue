package ru.aakumykov.me.mvp.utils;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MVPUtils {

    private final static String TAG = "MVPUtils";
    private static Map<String,String> regexMap = new HashMap<>();


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
        link = link.trim();

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

}
