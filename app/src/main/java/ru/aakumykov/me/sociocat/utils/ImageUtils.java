package ru.aakumykov.me.sociocat.utils;

import android.graphics.Bitmap;
import android.graphics.Matrix;

import androidx.annotation.Nullable;

import java.io.ByteArrayOutputStream;

public class ImageUtils {

    public final static int DEFAULT_JPEG_QUALITY = 90;


    public static ImageType detectImageType(@Nullable String imageTypeString) {
        switch ((""+imageTypeString).toLowerCase().trim()) {
            case "jpg":
            case "jpeg":
                return ImageType.JPEG;
            case "png":
                return ImageType.PNG;
            case "webp":
                return ImageType.WEBP;
            case "bmp":
                return ImageType.BMP;
            default:
                return ImageType.UNSUPPORTED_IMAGE_TYPE;
        }
    }

    public byte[] image2bytes(Bitmap imageBitmap, ImageType imageType) {
        return image2bytes(imageBitmap, imageType, ImageUtils.DEFAULT_JPEG_QUALITY);
    }

    public byte[] image2bytes(Bitmap imageBitmap, ImageType imageType, int quality) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Bitmap.CompressFormat compressFormat;

        switch (imageType) {
            case JPEG:
                compressFormat = Bitmap.CompressFormat.JPEG;
                break;
            case PNG:
                // TODO: проверить
                compressFormat = Bitmap.CompressFormat.WEBP;
            default:
                compressFormat = Bitmap.CompressFormat.JPEG;
        }

        imageBitmap.compress(compressFormat, quality, baos);
        return baos.toByteArray();
    }


    public static Bitmap scaleDownBitmap(Bitmap bitmap, int threshold) {
        return scaleDownBitmap(bitmap, threshold, false);
    }

    /**
     * @param bitmap the Bitmap to be scaled
     * @param threshold the maximum dimension (either width or height) of the scaled bitmap
     * @param isNecessaryToKeepOrig is it necessary to keep the original bitmap? If not recycle the original bitmap to prevent memory leak.
     * @see "https://gist.github.com/vxhviet/873d142b41217739a1302d337b7285ba"
     * */
    public static Bitmap scaleDownBitmap(Bitmap bitmap, int threshold, boolean isNecessaryToKeepOrig){
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int newWidth = width;
        int newHeight = height;

        if(width > height && width > threshold){
            newWidth = threshold;
            newHeight = (int)(height * (float)newWidth/width);
        }

        if(width > height && width <= threshold){
            //the bitmap is already smaller than our required dimension, no need to resize it
            return bitmap;
        }

        if(width < height && height > threshold){
            newHeight = threshold;
            newWidth = (int)(width * (float)newHeight/height);
        }

        if(width < height && height <= threshold){
            //the bitmap is already smaller than our required dimension, no need to resize it
            return bitmap;
        }

        if(width == height && width > threshold){
            newWidth = threshold;
            newHeight = newWidth;
        }

        if(width == height && width <= threshold){
            //the bitmap is already smaller than our required dimension, no need to resize it
            return bitmap;
        }

        return getResizedBitmap(bitmap, newWidth, newHeight, isNecessaryToKeepOrig);
    }

    private static Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight, boolean isNecessaryToKeepOrig) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
        if(!isNecessaryToKeepOrig){
            bm.recycle();
        }
        return resizedBitmap;
    }


    // Запрещённый конструктор
    private ImageUtils(){}
}
