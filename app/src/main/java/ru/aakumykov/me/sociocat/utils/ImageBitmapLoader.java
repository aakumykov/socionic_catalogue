package ru.aakumykov.me.sociocat.utils;

import android.content.Context;
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

public final class ImageBitmapLoader {

    // Публичные методы
    public static void loadImage2Bitmap(Context context, String imageURL, LoadImageCallbacks callbacks) throws ImageBitmapLoaderException {
        loadImageWithGlide(context, imageURL, callbacks);
    }

    public static void loadImage2Bitmap(Context context, Uri imageURI, LoadImageCallbacks callbacks) throws ImageBitmapLoaderException {
        loadImageWithGlide(context, imageURI, callbacks);
    }


    // Интерфейсы
    public interface LoadImageCallbacks {
        void onImageLoadSuccess(Bitmap imageBitmap);
        void onImageLoadError(String errorMsg);
    }


    // Внутренние методы
    private static <T> void loadImageWithGlide(Context context, T imageLink, LoadImageCallbacks callbacks)
            throws ImageBitmapLoaderException
    {
        if (null == context)
            throw new IllegalArgumentException("Context cannot be null");

        if (null == imageLink)
            throw new IllegalArgumentException("imageLink cannot be null");

        if (null == callbacks)
            throw new IllegalArgumentException("Callbacks cannot be null");


        Glide.with(context)
                .load(imageLink)
                .into(new CustomTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {

                        Bitmap bitmap = null;

                        if (resource instanceof BitmapDrawable) {
                            bitmap = ((BitmapDrawable) resource).getBitmap();
                        } else if (resource instanceof GifDrawable) {
                            bitmap = ((GifDrawable) resource).getFirstFrame();
                        } else {
                            callbacks.onImageLoadError("Such type of image can not be loaded: "+resource);
                        }

                        callbacks.onImageLoadSuccess(bitmap);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                        callbacks.onImageLoadError("Loading of image is cleared");
                    }
                });
    }


    // Классы исключений
    public static class UnsupportedImageType_Exception extends ImageBitmapLoaderException {
        public UnsupportedImageType_Exception(String message) {
            super(message);
        }
    }

    public static class ImageBitmapLoaderException extends Exception {
        public ImageBitmapLoaderException(String message) {
            super(message);
        }
    }


    // Конструктор (отключен)
    private ImageBitmapLoader() {
        throw new RuntimeException("Construstor of class ImageBitmapLoader is denied");
    }
}
