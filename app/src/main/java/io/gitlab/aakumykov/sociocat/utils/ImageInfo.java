package io.gitlab.aakumykov.sociocat.utils;

import android.net.Uri;

public class ImageInfo {

    private Uri localURI;
    private ImageType imageType;

    public ImageInfo() {
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
