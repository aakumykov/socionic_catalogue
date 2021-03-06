package ru.aakumykov.me.sociocat.utils;

import android.content.Intent;
import android.net.Uri;

import androidx.annotation.Nullable;

public class Intent_URIExtractor {


    public static Uri extractUriFromIntent(@Nullable Intent intent) throws Intent_URIExtractor_Exception {
        if (null == intent)
            throw new NoIntent_Exception("Intent is null");

        // ==== Выделяю данные, похожие на URI из Intent ====
        // Первый способ
        Object imageUriObject = intent.getParcelableExtra(Intent.EXTRA_STREAM);

        // Второй способ
        if (null == imageUriObject)
            imageUriObject = intent.getData();

        // Третий способ
        if (null == imageUriObject)
            imageUriObject = intent.getStringExtra(Intent.EXTRA_TEXT);

        if (null == imageUriObject)
            throw new NoUri_Exception("Uri not found in Intent");


        // ==== Преобразую их в URI ====
        Uri resultURI;

        if (imageUriObject instanceof Uri) {
            resultURI = (Uri) imageUriObject;
        }
        else if (imageUriObject instanceof String) {
            try {
                resultURI = Uri.parse((String) imageUriObject);
            } catch (java.lang.Exception e) {
                throw new ParseError_Exception(e);
            }
        }
        else {
            throw new IllegalData_Exception("Data type in Intent cannot be converted to Uri");
        }

        return resultURI;
    }


    // Классы исключений
    public static class Intent_URIExtractor_Exception extends java.lang.Exception {
        public Intent_URIExtractor_Exception(String message) {
            super(message);
        }

        public Intent_URIExtractor_Exception(Throwable cause) {
            super(cause);
        }
    }

    public static class NoIntent_Exception extends Intent_URIExtractor_Exception {
        public NoIntent_Exception(String message) {
            super(message);
        }
    }

    public static class NoUri_Exception extends Intent_URIExtractor_Exception {
        public NoUri_Exception(String message) {
            super(message);
        }
    }

    public static class IllegalData_Exception extends Intent_URIExtractor_Exception {
        public IllegalData_Exception(String message) {
            super(message);
        }
    }

    public static class ParseError_Exception extends Intent_URIExtractor_Exception {
        public ParseError_Exception(Throwable cause) {
            super(cause);
        }
    }
}
