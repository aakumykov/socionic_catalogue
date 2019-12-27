package ru.aakumykov.me.sociocat.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import ru.aakumykov.me.sociocat.utils.MVPUtils.MVPUtils;

public class ImageInfo {

    private Uri localURI;
    private ImageType imageType;

    public ImageInfo() {
    }

    public ImageInfo(Uri localURI, ImageType imageType) {
        this.localURI = localURI;
        this.imageType = imageType;
    }

    public Uri getLocalURI() {
        return localURI;
    }
    public void setLocalURI(Uri localURI) {
        this.localURI = localURI;
    }

    public ImageType getImageType() {
        return imageType;
    }
    public void setImageType(ImageType imageType) {
        this.imageType = imageType;
    }

    public String getTypeString() {
        return imageType.name();
    }
}
