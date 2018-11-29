package ru.aakumykov.me.mvp.utils.MVPUtils;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ru.aakumykov.me.mvp.Config;
import ru.aakumykov.me.mvp.Constants;
import ru.aakumykov.me.mvp.utils.MyUtils;

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

    public static byte[] imageView2Bitmap(ImageView imageView) throws Exception {
        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, Config.JPEG_QUALITY, baos);
        return baos.toByteArray();
    }
}
