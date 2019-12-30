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

        String getName();
        String getEmail();
        String getAbout();
    }

    interface iPresenter {
        void linkView(iView view);
        void unlinkView();

        boolean hasUser();

        void onFirstOpen(@Nullable Intent intent);

        void onConfigChanged();

        void onImageSelected(Bitmap bitmap, ImageType imageType);

        void onUserLoggedOut();

        void onSaveUserClicked();

        void onCancelButtonClicked();

        void onBackPressed();
    }

}
