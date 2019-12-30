package ru.aakumykov.me.sociocat.utils;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.MimeTypeMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.ByteArrayOutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ru.aakumykov.me.sociocat.R;

public class ImageUtils {

    public final static int DEFAULT_JPEG_QUALITY = 90;
    public final static int CODE_SELECT_IMAGE = 10;
    private final static String TAG = "ImageUtils";

    
    public static boolean pickImage(Activity activity) {
        return pickImage(activity, "image/*");
    }

    public static boolean pickImage(Activity activity, String mimeType) {
        Intent intent = new Intent();
        intent.setType(mimeType);
        intent.setAction(Intent.ACTION_GET_CONTENT);

        if (null != intent.resolveActivity(activity.getPackageManager())) {
            activity.startActivityForResult(
                    Intent.createChooser(intent, activity.getResources().getString(R.string.select_image)),
                    CODE_SELECT_IMAGE
            );
            return true;
        }
        else {
            Log.e(TAG, "Error resolving activity for Intent.ACTION_GET_CONTENT");
            return false;
        }
    }


    public static ImageInfo extractImageInfo(Context context, @Nullable Intent intent) throws
            UnsupportedFormat_Exception,
            NoImageInfo_Exception
    {
        Uri imageLocalURI = extractImageUriFromIntent(context, intent);

        String imageTypeString = extractImageTypeString(context, imageLocalURI);
        ImageType imageType = detectImageType(imageTypeString);

        if (null != imageLocalURI && null != imageType) {
            ImageInfo imageInfo = new ImageInfo();
            imageInfo.setLocalURI(imageLocalURI);
            imageInfo.setImageType(imageType);
            return imageInfo;
        }
        else
            throw new NoImageInfo_Exception("");
    }


    public static byte[] compressImage(Bitmap imageBitmap, ImageType imageType) {
        return compressImage(imageBitmap, imageType, ImageUtils.DEFAULT_JPEG_QUALITY);
    }

    public static byte[] compressImage(Bitmap imageBitmap, ImageType imageType, int quality) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Bitmap.CompressFormat compressFormat;

        switch (imageType) {
            case JPEG:
                compressFormat = Bitmap.CompressFormat.JPEG;
                break;
            case PNG:
                compressFormat = Bitmap.CompressFormat.PNG;
                break;
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


    // Внутренние методы
    private static ImageType detectImageType(@Nullable String imageTypeString) throws UnsupportedFormat_Exception {
        String imgType = (""+imageTypeString).toLowerCase().trim();

        switch (imgType) {
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
                throw new UnsupportedFormat_Exception("Unsupported image type: "+imgType);
        }
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

    private static Uri extractImageUriFromIntent(Context context, @NonNull Intent intent) {

        Object imageUri = intent.getParcelableExtra(Intent.EXTRA_STREAM); // Первый способ получить содержимое

        if (null == imageUri)
            imageUri = intent.getData(); // Второй способ получить содержимое

        if (null == imageUri)
            imageUri = intent.getStringExtra(Intent.EXTRA_TEXT); // Третий способ

        if (null == imageUri)
            return null;


        String imageType = "";
        Uri resultImageUri = null;

        if (imageUri instanceof Uri) {
            imageType = extractImageTypeString(context, (Uri) imageUri);
            resultImageUri = (Uri) imageUri;
        }
        else if (imageUri instanceof String) {
            imageType = extractImageTypeString(context, (String) imageUri);
            resultImageUri = Uri.parse((String) imageUri);
        }
        else
            return null;

        if (null != imageType)
            return resultImageUri;
        else
            return null;
    }

    private static <T> String extractImageTypeString(Context context, T imageUri) {
        if (imageUri instanceof Uri) {
            ContentResolver contentResolver = context.getContentResolver();
            MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
            String mimeType = contentResolver.getType((Uri) imageUri);
            return (null == mimeType) ? null : mimeTypeMap.getExtensionFromMimeType(mimeType);
        }
        else if (imageUri instanceof String) {
            String imageString = (String) imageUri;
            imageString = imageString.trim();
            imageString = imageString.toLowerCase();
            Pattern pattern = Pattern.compile("^.+\\.([a-z]+)$");
            // запомни, регулярное выражение должно соответствовать строке целиком!
            Matcher matcher = pattern.matcher(imageString);
            return (matcher.matches()) ? matcher.group(1) : null;
        }
        else {
            return null;
        }
    }


    // Конструктор (запрещён)
    private ImageUtils(){}

    public static String makeFileName(String nameBase, ImageType imageType) {
        if (TextUtils.isEmpty(nameBase))
            throw new IllegalArgumentException("nameBase cannot be empty");

        if (null == imageType)
            throw new IllegalArgumentException("imageType cannot be null");

        return nameBase + "." + imageType.name().toLowerCase();
    }


    // Классы исключений
    public static class WrongArgument_Exception extends ImageUtils_Exception {
        public WrongArgument_Exception(String message) {
            super(message);
        }
    }
    public static class UnsupportedFormat_Exception extends ImageUtils_Exception {
        public UnsupportedFormat_Exception(String message) {
            super(message);
        }
    }
    public static class NoImageInfo_Exception extends ImageUtils_Exception {
        public NoImageInfo_Exception(String message) {
            super(message);
        }
    }
    public static class ImageUtils_Exception extends Exception {
        public ImageUtils_Exception(String message) {
            super(message);
        }
    }
}
