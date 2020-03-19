package ru.aakumykov.me.sociocat;

import androidx.annotation.Nullable;

public interface iViewStates {
    <T> void setState(T viewState, int messageId);
    <T> void setState(T viewState, int messageId, @Nullable String errorMsg);
}