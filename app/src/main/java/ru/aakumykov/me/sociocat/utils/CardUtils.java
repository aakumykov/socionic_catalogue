package ru.aakumykov.me.sociocat.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.view.Display;
import android.view.WindowManager;

public final class CardUtils {

    private CardUtils() {}

    public static boolean need2adjustViewBounds(Context context, Bitmap bitmap) {

        int imgWidth = bitmap.getWidth();
        int imgHeight = bitmap.getHeight();

        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

        if (null != windowManager) {
            Display display = windowManager.getDefaultDisplay();

            Point size = new Point();
            display.getSize(size);
            int displayWidth = size.x;

            boolean imageIsVertical = imgHeight > imgWidth;
            boolean imageIsNarrowerThanScreen = imgWidth < displayWidth;

            return imageIsVertical && imageIsNarrowerThanScreen;
        }
        else
            return false;
    }
}
