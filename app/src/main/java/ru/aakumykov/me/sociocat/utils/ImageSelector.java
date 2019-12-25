package ru.aakumykov.me.sociocat.utils;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.webkit.MimeTypeMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ru.aakumykov.me.sociocat.R;

public class ImageSelector {

    public final static int CODE_SELECT_IMAGE = 10;

    private static final String TAG = "ImageSelector";

    private ImageSelector() {}


    public static boolean selectImage(Activity activity) {
        return selectImage(activity, "image/*");
    }

    public static boolean selectImage(Activity activity, String mimeType) {
        Intent intent = new Intent();
        intent.setType(mimeType);
        intent.setAction(Intent.ACTION_GET_CONTENT);

        if (null != intent.resolveActivity(activity.getPackageManager())) {
            activity.startActivityForResult(
                    Intent.createChooser(intent, activity.getResources().getString(R.string.select_image)),
                    CODE_SELECT_IMAGE
            );
            return true;
        }
        else {
            Log.e(TAG, "Error resolving activity for Intent.ACTION_GET_CONTENT");
            return false;
        }
    }


    public static ImageInfo getImageInfo(Context context, @NonNull Intent intent) {

        Uri imageLocalURI = extractImageUriFromIntent(context, intent);

        String imageType = detectImageType(context, imageLocalURI);

        if (null != imageLocalURI && null != imageType) {
            ImageInfo imageInfo = new ImageInfo();
            imageInfo.setLocalURI(imageLocalURI);
            imageInfo.setType(imageType);
            return imageInfo;
        }
        else
            return null;
    }

    public static Uri extractImageUriFromIntent(Context context, @NonNull Intent intent) {

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
            imageType = detectImageType(context, (Uri) imageUri);
            resultImageUri = (Uri) imageUri;
        }
        else if (imageUri instanceof String) {
            imageType = detectImageType(context, (String) imageUri);
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

}
