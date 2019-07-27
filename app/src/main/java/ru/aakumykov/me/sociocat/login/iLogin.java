package ru.aakumykov.me.sociocat.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import ru.aakumykov.me.sociocat.interfaces.iBaseView;

public interface iLogin {

    interface View extends iBaseView {
        Activity getActivity();
        void disableForm();
        void enableForm();
        void finishLogin(boolean isCancelled, Intent transitIntent, @Nullable Bundle transitArguments);
        void notifyToConfirmEmail(String userId);
    }

    interface Presenter {
        void doLogin(String email, String password);
        void cancelLogin();
        void processInputIntent(@Nullable Intent intent);

        void onVKLoginButtonClicked();
        void processVKLogin(int vk_user_id, String vk_access_token);

        // TODO: вынести в общий интерфейс
        void linkView(iLogin.View view);
        void unlinkView();
    }
}
