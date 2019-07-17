package ru.aakumykov.me.sociocat.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import ru.aakumykov.me.sociocat.utils.MVPUtils.MVPUtils;

public class ImageInfo {

    private Uri localURI;
    private String type;

    public Uri getLocalURI() {
        return localURI;
    }

    public void setLocalURI(Uri localURI) {
        this.localURI = localURI;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    public static ImageInfo getImageInfo(Context context, Intent intent) {
        Uri imageLocalURI = MVPUtils.extractImageUriFromIntent(context, intent);
        String imageType = MVPUtils.detectImageType(context, imageLocalURI);

        if (null != imageLocalURI && null != imageType) {
            ImageInfo imageInfo = new ImageInfo();
            imageInfo.setLocalURI(imageLocalURI);
            imageInfo.setType(imageType);
            return imageInfo;
        }
        else
            return null;
    }
}