package ru.aakumykov.me.sociocat.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class IntentUtils {

    public static ContentType detectContentType(@Nullable Intent intent) {
        if (null == intent)
            return null;

        String type = intent.getType();

        if (null == type)
            return ContentType.NO_CONTENT_TYPE;

        type = type.trim();

        if (TextUtils.isEmpty(type))
            return ContentType.NO_CONTENT_TYPE;

        if (type.startsWith("image/")) {
            return ContentType.IMAGE;
        }
        else if ("text/plain".equals(type)) {
            return detectTextType(type);
        }
        else {
            return ContentType.OTHER;
        }
    }

    public static String extractText(@Nullable Intent intent) {
        if (null == intent)
            return null;

        return intent.getStringExtra(Intent.EXTRA_TEXT);
    }

    public static void extractImage(Context context, @Nullable Intent intent, ImageExtractionCallbacks callbacks) {

        Uri imageURI;
        ImageType imageType;

        // URI изображения
        try {
            imageURI = Intent_URIExtractor.extractUriFromIntent(intent);
        }
        catch (Intent_URIExtractor.Intent_URIExtractor_Exception e) {
            callbacks.onImageExtractionError(e.getMessage());
            MyUtils.printError(TAG, e);
            return;
        }

        // Тип изображения
        try {
            imageType = URI_ImageTypeExtractor.extractImageTypeFromURI(context, imageURI);
        }
        catch (URI_ImageTypeExtractor.URIImageTypeExtractor_Exception e) {
            callbacks.onImageExtractionError(e.getMessage());
            MyUtils.printError(TAG, e);
            return;
        }

        // Собственно картинка
        ImageLoader.loadImage(context, imageURI, new ImageLoader.LoadImageCallbacks() {
            @Override
            public void onImageLoadSuccess(Bitmap imageBitmap) {
                callbacks.onImageExtractionSuccess(imageBitmap, imageType, imageURI);
            }

            @Override
            public void onImageLoadError(String errorMsg) {
                callbacks.onImageExtractionError(errorMsg);
            }
        });
    }

    public static String extractYoutubeVideoCode(@Nullable Intent intent) {
        if (null == intent)
            return null;

        String text = extractText(intent);

        return YoutubeUtils.extractYoutubeVideoCode(text);
    }


    public interface ImageExtractionCallbacks {
        void onImageExtractionSuccess(Bitmap bitmap, ImageType imageType, Uri imageURI);
        void onImageExtractionError(String errorMsg);
    }


    // Внутренние свойства
    private static final String TAG = "IntentUtils";

    // Внутренние методы
    private static ContentType detectTextType(@NonNull String type) {

        if (YoutubeUtils.isYoutubeLink(type))
            return ContentType.YOUTUBE_VIDEO;
        else
            return ContentType.TEXT;
    }
}
