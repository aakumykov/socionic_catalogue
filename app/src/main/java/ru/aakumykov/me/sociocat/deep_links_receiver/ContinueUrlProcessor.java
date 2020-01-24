package ru.aakumykov.me.sociocat.deep_links_receiver;

import androidx.annotation.NonNull;

public class ContinueUrlProcessor {

    public static void process(@NonNull String continueUrl) throws ContinueUrlProcessorException {

    }

    public static class ContinueUrlProcessorException extends Exception {
        public ContinueUrlProcessorException(String message) {
            super(message);
        }
    }
}
