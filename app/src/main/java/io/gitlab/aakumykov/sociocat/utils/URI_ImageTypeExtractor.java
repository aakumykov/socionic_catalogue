package io.gitlab.aakumykov.sociocat.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.webkit.MimeTypeMap;

public class URI_ImageTypeExtractor {


    public static ImageType extractImageTypeFromURI(Context context, Uri imageURI) throws URIImageTypeExtractor_Exception {
        ContentResolver contentResolver = context.getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        String mimeType = contentResolver.getType(imageURI);
        String imageTypeString = (null == mimeType) ? null : mimeTypeMap.getExtensionFromMimeType(mimeType);
        return stringType2ImageType(imageTypeString);
    }


    // Внутренние методы
    private static ImageType stringType2ImageType(String imageTypeString) {

        imageTypeString = String.valueOf(imageTypeString).trim().toLowerCase();

        switch (imageTypeString) {
            case "jpeg":
            case "jpg":
                return ImageType.JPEG;
            case "png":
                return ImageType.PNG;
            case "bmp":
                return ImageType.BMP;
            case "webp":
                return ImageType.WEBP;
            case "gif":
                return ImageType.GIF;
            default:
                return ImageType.UNSUPPORTED_IMAGE_TYPE;
        }
    }


    // Классы исключений
    public static class URIImageTypeExtractor_Exception extends Exception {
        public URIImageTypeExtractor_Exception(String message) {
            super(message);
        }

        public URIImageTypeExtractor_Exception(Throwable cause) {
            super(cause);
        }
    }
}
