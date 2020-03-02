package ru.aakumykov.me.sociocat.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import ru.aakumykov.me.sociocat.interfaces.iBaseView;

public interface iLogin {

    enum ViewState {
        INITIAL,
        INFO,
        PROGRESS,
        SUCCESS,
        ERROR
    }

    interface View extends iBaseView {

        void setState(ViewState state, int messageId);
        void setState(ViewState state, int messageId, @Nullable String messageDetails);

        void disableForm();
        void enableForm();

        void finishLogin(boolean isCancelled, Intent transitIntent);

        void notifyToConfirmEmail(String userId);

        String getEmail();
        String getPassword();

        void go2finishRegistration(@NonNull String userId);
    }

    interface Presenter {
        void cancelLogin();
        void processInputIntent(@Nullable Intent intent);

        void onVKLoginButtonClicked();
        void processVKLogin(int vk_user_id, String vk_access_token);

        // TODO: вынести в общий интерфейс
        void linkView(iLogin.View view);
        void unlinkView();

        boolean isVirgin();
        void onConfigChanged();

        void storeViewState(ViewState state, int messageId, String messageDetails);

        void onFormIsValid();

        void onGoogleLoginResult(@Nullable Intent data);
    }
}
