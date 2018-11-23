package ru.aakumykov.me.mvp.utils;

import android.content.Intent;
import android.net.Uri;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MVPUtils {

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

        Map<String,String> regexMap = new HashMap<>();
        regexMap.put("youtube1", "^https?://youtube\\.com/watch\\?v=([^=?&]+)");
        regexMap.put("youtube2", "^https?://www\\.youtube\\.com/watch\\?v=([^=?&]+)");
        regexMap.put("youtube3", "^https?://youtu.be/([^/]+)$");

        for(Map.Entry<String,String> entry : regexMap.entrySet()) {
            String key = entry.getKey();
            String regex = entry.getValue();
            if (text.matches(regex)) return true;
        }

        return false;
    }

    public static String extractYoutubeVideoCode(String link) {
        link = link.trim();

        Pattern p = Pattern.compile("/([\\w-]+)$|^([\\w-]+)$|/watch\\?v=([\\w-]+)$");
        Matcher m = p.matcher(link);

        if (m.find()) {
            return m.group(1);
        } else {
            return null;
        }
    }

}
