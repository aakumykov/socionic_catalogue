package ru.aakumykov.me.sociocat.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;

public final class ImageBitmapLoader {

    private static final String TAG = "ImageBitmapLoader";

    // Публичные методы
    public static void loadImageAsBitmap(Context context, String imageURL, LoadImageCallbacks callbacks)
            throws ImageBitmapLoaderException, IllegalArgumentException
    {
        loadImageWithGlide(context, imageURL, callbacks);
    }

    public static void loadImageAsBitmap(Context context, Uri imageURI, LoadImageCallbacks callbacks)
            throws ImageBitmapLoaderException, IllegalArgumentException
    {
        loadImageWithGlide(context, imageURI, callbacks);
    }


    // Интерфейсы
    public interface LoadImageCallbacks {
        void onImageLoadSuccess(Bitmap imageBitmap);
        void onImageLoadError(String errorMsg);
    }


    // Внутренние методы
    @SuppressLint("CheckResult")
    private static <T> void loadImageWithGlide(Context context, T imageLink, LoadImageCallbacks callbacks)
            throws ImageBitmapLoaderException, IllegalArgumentException
    {
        if (null == context)
            throw new IllegalArgumentException("Context cannot be null");

        if (null == imageLink)
            throw new IllegalArgumentException("imageLink cannot be null");

        if (null == callbacks)
            throw new IllegalArgumentException("Callbacks cannot be null");


        RequestBuilder<Drawable> requestBuilder =  Glide.with(context)
                .load(imageLink)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        String errorMsg = (null != e) ? e.getMessage() : "Error loading image from: "+imageLink;
                        callbacks.onImageLoadError(errorMsg);
                        MyUtils.printError(TAG, e);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {

                        Bitmap bitmap = null;

                        if (resource instanceof BitmapDrawable) {
                            bitmap = ((BitmapDrawable) resource).getBitmap();
                        }
                        else if (resource instanceof GifDrawable) {
                            bitmap = ((GifDrawable) resource).getFirstFrame();
                        }
                        else {
                            callbacks.onImageLoadError("Such type of image can not be loaded by ImageBitmapLoader: "+resource);
                        }

                        callbacks.onImageLoadSuccess(bitmap);

                        return false;
                    }
                });

        requestBuilder.into(new CustomTarget<Drawable>() {
            @Override
            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {

            }

            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {

            }
        });

        /*Glide.with(context)
                .load(imageLink)
                .into(new CustomTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {

                        Bitmap bitmap = null;

                        if (resource instanceof BitmapDrawable) {
                            bitmap = ((BitmapDrawable) resource).getBitmap();
                        }
                        else if (resource instanceof GifDrawable) {
                            bitmap = ((GifDrawable) resource).getFirstFrame();
                        }
                        else {
                            callbacks.onImageLoadError("Such type of image can not be loaded: "+resource);
                        }

                        callbacks.onImageLoadSuccess(bitmap);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                        callbacks.onImageLoadError("Loading of image is cleared");
                    }
                });*/
    }


    // Классы исключений
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
