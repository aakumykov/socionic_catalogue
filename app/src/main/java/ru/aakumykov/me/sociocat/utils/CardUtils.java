package ru.aakumykov.me.sociocat.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.view.Display;
import android.view.WindowManager;
import android.widget.ImageView;

import io.aakumykov.me.sociocat.R;

public final class CardUtils {

    private CardUtils() {}

    public static void smartDisplayImage(ImageView targetImageView, Bitmap bitmap) {

        int imgWidth = bitmap.getWidth();
        int imgHeight = bitmap.getHeight();

        targetImageView.setImageBitmap(bitmap);

        if (need2adjustViewBounds(targetImageView.getContext(), imgWidth, imgHeight))
            setAdjustViewBounds(targetImageView);
        else
            setDejustViewBounds(targetImageView);
    }

    public static void smartDisplayImage(ImageView targetImageView, Drawable imageDrawable) {

        int imgWidth = imageDrawable.getIntrinsicWidth();
        int imgHeight = imageDrawable.getIntrinsicHeight();

        targetImageView.setImageDrawable(imageDrawable);

        if (need2adjustViewBounds(targetImageView.getContext(), imgWidth, imgHeight))
            setAdjustViewBounds(targetImageView);
        else
            setDejustViewBounds(targetImageView);
    }

    private static boolean need2adjustViewBounds(Context context, int imgWidth, int imgHeight) {

        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

        if (null != windowManager) {
            Display display = windowManager.getDefaultDisplay();
            windowManager = null;

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


    private static void setAdjustViewBounds(ImageView imageView) {
        imageView.setAdjustViewBounds(false);
        imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        imageView.setBackgroundResource(R.drawable.shape_green_border);
    }

    private static void setDejustViewBounds(ImageView imageView) {
        imageView.setAdjustViewBounds(true);
        imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        imageView.setBackgroundResource(R.drawable.shape_transparent_border);
    }
}
