package ru.aakumykov.me.sociocat;

import android.app.Application;
import android.net.Uri;

public class App extends Application {

    public String detectMimeType(Uri fileUri) {
        if (null == fileUri) return null;
        return getApplicationContext().getContentResolver().getType(fileUri);
    }
}
