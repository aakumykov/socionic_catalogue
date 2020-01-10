package ru.aakumykov.me.sociocat.utils.data_detector;

import android.content.Intent;

import androidx.annotation.Nullable;

import ru.aakumykov.me.sociocat.utils.ImageInfo;
import ru.aakumykov.me.sociocat.utils.ImageUtils;
import ru.aakumykov.me.sociocat.utils.MyUtils;

public class IntentDataDetector {

    private static final String TAG = "IntentDataDetector";

    public static IntentDataType detectType(@Nullable Intent inputIntent) {
        if (null == inputIntent)
            return new IntentDataType(DataType.NO_DATA);

        try {
            ImageInfo imageInfo = ImageUtils.extractImageInfo(this, inputIntent);
        }
        catch (ImageUtils.ImageUtils_Exception e) {
            MyUtils.printError(TAG, e);
        }
    }


    public enum DataType {
        TEXT,
        IMAGE,
        URL_YOUTUBE,
        URL_WWW,
        UNKNOWN,
        NO_DATA
    }

    public static class IntentDataType {

        private DataType dataType;

        public IntentDataType(DataType dataType) {
            this.dataType = dataType;
        }
    }
}
