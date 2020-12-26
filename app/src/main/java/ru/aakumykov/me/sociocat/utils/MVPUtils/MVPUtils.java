package ru.aakumykov.me.sociocat.utils.MVPUtils;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.webkit.MimeTypeMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ru.aakumykov.me.sociocat.AppConfig;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.card_edit.DraftRestoreFragment;
import ru.aakumykov.me.sociocat.constants.Constants;
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
        youtubePatterns.put("youtube1", AppConfig.YOUTUBE_LONG_LINK_REGEX_2);
        youtubePatterns.put("youtube2", AppConfig.YOUTUBE_LONG_LINK_REGEX_1);
        youtubePatterns.put("youtube3", AppConfig.YOUTUBE_SHORT_LINK_REGEX);
        youtubePatterns.put("youtube4", AppConfig.YOUTUBE_CODE_REGEX);
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
        aspectsMap.put("БС", R.drawable.aspect_sense);
        aspectsMap.put("БИ", R.drawable.aspect_time);
    }


    private MVPUtils(){}


    @Deprecated
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

    @Deprecated
    public static String extractYoutubeVideoCode(@Nullable String link) {

        if (null == link)
            return null;

        link = link.trim();

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
        if (tag.length() < AppConfig.TAG_MIN_LENGTH) {
            return null;
        }

        // укорачиваю черезмерно длинные
        tag = MyUtils.cutToLength(tag, AppConfig.TAG_MAX_LENGTH);

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

        bitmap.compress(compressFormat, AppConfig.DEFAULT_JPEG_QUALITY, baos);
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

    public static String tempUserName(@NonNull Context context) {
        String nameSuffix = UUID.randomUUID().toString().substring(9, 12);
        String namePrefix = context.getResources().getString(R.string.CONFIG_temp_user_name_prefix);
        return namePrefix+"-"+nameSuffix;
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


    public static Uri extractImageUriFromIntent(Context context, @Nullable Intent intent) {

        if (null == intent)
            return null;

        Object imageUri = intent.getParcelableExtra(Intent.EXTRA_STREAM); // Первый способ получить содержимое

        if (null == imageUri)
            imageUri = intent.getData(); // Второй способ получить содержимое

        if (null == imageUri)
            imageUri = intent.getStringExtra(Intent.EXTRA_TEXT); // Третий способ

        if (null == imageUri)
            return null;


        String imageType = "";
        Uri resultImageUri = null;

        if (imageUri instanceof Uri) {
            imageType = detectImageType(context, imageUri);
            resultImageUri = (Uri) imageUri;
        }
        else if (imageUri instanceof String) {
            imageType = detectImageType(context, imageUri);
            resultImageUri = Uri.parse((String) imageUri);
        }
        else
            return null;

        if (null != imageType)
            return resultImageUri;
        else
            return null;
    }

    public static <T> String detectImageType(Context context, T imageUri) {
        if (imageUri instanceof Uri) {
            ContentResolver contentResolver = context.getContentResolver();
            MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
            String mimeType = contentResolver.getType((Uri) imageUri);
            return (null == mimeType) ? null : mimeTypeMap.getExtensionFromMimeType(mimeType);
        }
        else if (imageUri instanceof String) {
            String imageString = (String) imageUri;
            imageString = imageString.trim();
            imageString = imageString.toLowerCase();
            Pattern pattern = Pattern.compile("^.+\\.([a-z]+)$");
            // запомни, регулярное выражение должно соответствовать строке целиком!
            Matcher matcher = pattern.matcher(imageString);
            return (matcher.matches()) ? matcher.group(1) : null;
        }
        else {
            return null;
        }
    }

    @Deprecated
    public static String getYoutubeVideoCodeFromIntent(Intent intent) throws Exception {

        String link = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (null == link)
            throw new IllegalArgumentException("Video link is null");

        String videoCode = MVPUtils.extractYoutubeVideoCode(link);
        if (null == videoCode)
            throw new IllegalArgumentException("Where is no video code in link '"+link+"");

        return videoCode;
    }

    public static String getTextFromIntent(@Nullable Intent intent) {

        if (null == intent)
            return null;

        String text = intent.getStringExtra(Intent.EXTRA_TEXT);

        if (TextUtils.isEmpty(text))
            return null;

        return text;
    }

}
