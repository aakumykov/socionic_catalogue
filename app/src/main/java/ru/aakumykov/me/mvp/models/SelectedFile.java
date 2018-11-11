package ru.aakumykov.me.mvp.models;

import android.net.Uri;

public class SelectedFile {

    private String mimeType;
    private Uri dataURI;

    public String getMimeType() {
        return mimeType;
    }
    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public Uri getDataURI() {
        return dataURI;
    }
    public void setDataURI(Uri dataURI) {
        this.dataURI = dataURI;
    }
}
