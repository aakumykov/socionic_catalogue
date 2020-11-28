package ru.aakumykov.me.sociocat.utils;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.MimeTypeMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

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


    public static void extractImageFromIntent(
            Context context,
            @Nullable Intent intent,
            ImageExtractionCallbacks callbacks
    )
        throws ImageUtils_Exception
    {
        if (null == intent)
            throw new ImageUtils_NoData_Exception("Intent is null");

        ImageInfo imageInfo = extractImageInfo(context, intent);
        if (null == imageInfo)
            throw new ImageUtils_NoData_Exception("Cannot extract ImageInfo from Intent");

        Glide.with(context).load(imageInfo.getLocalURI()).into(new CustomTarget<Drawable>() {
            @Override
            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                Bitmap bitmap = null;

                if (resource instanceof BitmapDrawable) {
                    bitmap = ((BitmapDrawable) resource).getBitmap();
                }
                else if (resource instanceof GifDrawable) {
                    bitmap = ((GifDrawable) resource).getFirstFrame();
                }

                if (null != bitmap)
                    callbacks.onImageExtractionSuccess(bitmap, imageInfo.getImageType());
                else
                    callbacks.onImageExtractionError("Glide cannot load such type of image");
            }

            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {
                // Не знаю пока, что с этим делать
            }
        });
    }


    public static ImageInfo extractImageInfo(Context context, @Nullable Intent intent) throws
            ImageUtils_UnsupportedFormat_Exception,
            ImageUtils_NoImageInfo_Exception
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
            throw new ImageUtils_NoImageInfo_Exception("");
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
            case WEBP:
                compressFormat = Bitmap.CompressFormat.WEBP;
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
    private static boolean pickImage(Activity activity, String mimeType) {
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

    private static ImageType detectImageType(@Nullable String imageTypeString) throws ImageUtils_UnsupportedFormat_Exception {
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
            case "gif":
                return ImageType.GIF;
            default:
                throw new ImageUtils_UnsupportedFormat_Exception("Unsupported image type: "+imgType);
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

    public static Drawable getDrawableFromResource(@NonNull Context context, int drawableResourceId) {
        return ResourcesCompat.getDrawable(context.getResources(), drawableResourceId, null);
    }

    public static Bitmap drawable2bitmap(@NonNull Context context, Drawable drawable, int widthPixels, int heightPixels) {

        Bitmap mutableBitmap = Bitmap.createBitmap(widthPixels, heightPixels, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(mutableBitmap);

        drawable.setBounds(0, 0, widthPixels, heightPixels);
        drawable.draw(canvas);

        return mutableBitmap;
    }


    // Коллбеки
    public interface ImageExtractionCallbacks {
        void onImageExtractionSuccess(Bitmap bitmap, ImageType imageType);
        void onImageExtractionError(String errorMsg);
    }


    // Классы исключений
    public static class ImageUtils_Exception extends Exception {
        public ImageUtils_Exception(String message) {
            super(message);
        }
    }

    public static class ImageUtils_WrongArgument_Exception extends ImageUtils_Exception {
        public ImageUtils_WrongArgument_Exception(String message) {
            super(message);
        }
    }

    public static class ImageUtils_UnsupportedFormat_Exception extends ImageUtils_Exception {
        public ImageUtils_UnsupportedFormat_Exception(String message) {
            super(message);
        }
    }

    public static class ImageUtils_NoImageInfo_Exception extends ImageUtils_Exception {
        public ImageUtils_NoImageInfo_Exception(String message) {
            super(message);
        }
    }

    private static class ImageUtils_NoData_Exception extends ImageUtils_Exception {
        public ImageUtils_NoData_Exception(String message) {
            super(message);
        }
    }
}
