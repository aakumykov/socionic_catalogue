package io.gitlab.aakumykov.sociocat.deep_links_receiver;

import android.net.Uri;

import androidx.annotation.Nullable;

public class ContinueUrlProcessor {

    public static void process(@Nullable String continueUrl) throws ContinueUrlProcessorException {

        Uri uri = Uri.parse(continueUrl);


    }



    public static class ContinueUrlProcessorException extends Exception {
        public ContinueUrlProcessorException(String message) {
            super(message);
        }
    }
}
