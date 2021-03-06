package ru.aakumykov.me.sociocat.login;

import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import ru.aakumykov.me.sociocat.z_base_view.iBaseView;

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

        void startLoginWithGoogle();
        void finishLogin(boolean isCancelled, @Nullable Intent transitIntent);

        String getEmail();
        String getPassword();

        void go2finishRegistration(@NonNull String userId);
    }

    interface Presenter {
        void cancelLogin();
        void processInputIntent(@Nullable Intent intent);

        // TODO: вынести в общий интерфейс
        void linkView(iLogin.View view);
        void unlinkView();

        boolean isVirgin();
        void onConfigChanged();

        void storeViewState(ViewState state, int messageId, String messageDetails);

        void onFormIsValid();

        void onLoginWithGoogleClicked();
        void onGoogleLoginResult(@Nullable Intent data);
        void onLoginWithGoogleCancelled();
    }
}
