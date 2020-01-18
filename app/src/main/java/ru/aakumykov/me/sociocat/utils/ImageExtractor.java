package ru.aakumykov.me.sociocat.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

@Deprecated
public class ImageExtractor {

    public static void extractImageFromIntent(Context context, @Nullable Intent intent, ImageExtractionCallbacks callbacks) {

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
        Glide.with(context).load(imageURI).into(new CustomTarget<Drawable>() {
            @Override
            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {

                Bitmap bitmap = null;

                if (resource instanceof BitmapDrawable) {
                    bitmap = ((BitmapDrawable) resource).getBitmap();
                }
                else if (resource instanceof GifDrawable) {
                    bitmap = ((GifDrawable) resource).getFirstFrame();
                }

                if (null != bitmap)
                    callbacks.onImageExtractionSuccess(bitmap, imageType, imageURI);
                else
                    callbacks.onImageExtractionError("Glide cannot load such type of image");
            }

            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {
                callbacks.onImageExtractionError("Loading of image cleared");
            }
        });
    }

    public interface ImageExtractionCallbacks {
        void onImageExtractionSuccess(Bitmap bitmap, ImageType imageType, Uri imageURI);
        void onImageExtractionError(String errorMsg);
    }

    private static final String TAG = "ImageExtractor";
}
