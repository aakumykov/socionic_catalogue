package ru.aakumykov.me.sociocat.utils;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import ru.aakumykov.me.sociocat.Constants;
import ru.aakumykov.me.sociocat.R;

public class ImageSelector {

    private static final String TAG = "ImageSelector";

    private ImageSelector() {}

    public static void selectFile(Activity activity) {

    }

    public static void selectImage(Activity activity) {
        selectFile(activity, "image/*");
    }

    public static void selectFile(Activity activity, String mimeTypeFilter) {
        Intent intent = new Intent();
        intent.setType(mimeTypeFilter);
        intent.setAction(Intent.ACTION_GET_CONTENT);

        if (null != intent.resolveActivity(activity.getPackageManager())) {
            activity.startActivityForResult(
                    Intent.createChooser(intent, activity.getResources().getString(R.string.select_image)),
                    Constants.CODE_SELECT_IMAGE
            );
        }
        else {
            Log.e(TAG, "Error resolving activity for Intent.ACTION_GET_CONTENT");
        }
    }
}
