package ru.aakumykov.me.sociocat.user_edit;

import android.content.Intent;
import android.graphics.Bitmap;

import androidx.annotation.Nullable;

import ru.aakumykov.me.sociocat.interfaces.iBaseView;
import ru.aakumykov.me.sociocat.models.User;
import ru.aakumykov.me.sociocat.utils.ImageType;


public interface iUserEdit {

    interface iView extends iBaseView {
        <T> void fillEditForm(User user, T avatar);
        <T> void displayAvatar(T avatar);

        void disableEditForm();
        void enableEditForm();

        void showAvatarThrobber();
        void hideAvatarThrobber();

        void showAvatarError(int messageId, String consoleMessage);
        void hideAvatarError();

        String getName();
        String getEmail();
        String getAbout();

        void pickImage();

        void finishEdition(@Nullable User user);
        void cancelEdition();
    }

    interface iPresenter {
        void linkView(iView view);
        void unlinkView();

        boolean hasUser();

        void onFirstOpen(@Nullable Intent intent);

        void onConfigChanged();

        void onImageSelectionSuccess(Bitmap bitmap, ImageType imageType);
        void onImageSelectionError(String errorMsg);

        void onUserLoggedOut();

        void onAvatarClicked();
        void onSaveUserClicked();
        void onCancelButtonClicked();

        void onBackPressed();
    }

}
