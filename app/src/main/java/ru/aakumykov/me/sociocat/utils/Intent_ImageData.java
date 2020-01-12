package ru.aakumykov.me.sociocat.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.annotation.Nullable;

public class Intent_ImageData {

    private static final String TAG = "Intent_ImageData";

    private ImageType imageType = null;
    private Uri imageURI = null;
    private String error;


    public Intent_ImageData(Context context, @Nullable Intent intent) {

        try {
            imageURI = Intent_URIExtractor.extractUriFromIntent(intent);
        }
        catch (Intent_URIExtractor.Intent_URIExtractor_Exception e) {
            this.error = e.getMessage();
            return;
        }


        try {
            this.imageType = URI_ImageTypeExtractor.extractImageTypeFromURI(context, imageURI);
        }
        catch (URI_ImageTypeExtractor.URIImageTypeExtractor_Exception e) {
            this.error = e.getMessage();
            return;
        }
    }


    public ImageType getImageType() {
        return imageType;
    }

    public Uri getImageURI() {
        return imageURI;
    }

    public String getError() {
        return error;
    }

    public boolean noError() {
        return null == error;
    }
}
