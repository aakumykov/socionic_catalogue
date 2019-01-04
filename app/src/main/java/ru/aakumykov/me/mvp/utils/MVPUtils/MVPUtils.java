package ru.aakumykov.me.mvp.utils.MVPUtils;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

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
    private static Map<String,String> youtubePatterns = new HashMap<>();
    private static Map<String,String> imagePatterns = new HashMap<>();
    private static List<String> correctCardTypes = new ArrayList<>();
    private static final List<String> upperCaseTags = new ArrayList<>();

    static {
        correctCardTypes.add(Constants.TEXT_CARD);
        correctCardTypes.add(Constants.IMAGE_CARD);
        correctCardTypes.add(Constants.VIDEO_CARD);
        correctCardTypes.add(Constants.AUDIO_CARD);
    }

    static {
        /* Все регулярные выражения для применения к URL/видео-кодам YouTube
         * обязаны выделять код видео в _первой_ группе. */
        youtubePatterns.put("youtube1", "^https?://youtube\\.com/watch\\?v=([^=?&]+)");
        youtubePatterns.put("youtube2", "^https?://www\\.youtube\\.com/watch\\?v=([^=?&]+)");
        youtubePatterns.put("youtube3", "^https?://youtu.be/([^/]+)$");
        youtubePatterns.put("youtube4", "^([\\w-]{11})$");
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

    private MVPUtils(){}


    public static String detectInputDataMode(Intent intent) {

        if (null == intent)
            return "NULL";

        String type = intent.getType() + "";
        String extraText = intent.getStringExtra(Intent.EXTRA_TEXT) + "";

        if (type.equals("text/plain")) {

            String text = intent.getStringExtra(Intent.EXTRA_TEXT);

            if (isLinkToImage(text)) {
                return Constants.TYPE_IMAGE_LINK;
            }
            else if (MVPUtils.isYoutubeLink(text)) {
                return Constants.TYPE_YOUTUBE_VIDEO;
            }
            else {
                return Constants.TYPE_TEXT;
            }
        }
        else if (type.startsWith("image/") && isLinkToImage(extraText)) {
            return Constants.TYPE_IMAGE_LINK;
        }
        else if (type.startsWith("image/")) {
            return Constants.TYPE_IMAGE_DATA;
        }
        else {
            return Constants.TYPE_UNKNOWN;
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
}
