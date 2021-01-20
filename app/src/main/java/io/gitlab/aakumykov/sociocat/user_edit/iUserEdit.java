package io.gitlab.aakumykov.sociocat.user_edit;

import android.content.Intent;
import android.graphics.Bitmap;

import androidx.annotation.Nullable;

import io.gitlab.aakumykov.sociocat.models.User;
import io.gitlab.aakumykov.sociocat.utils.ImageType;
import io.gitlab.aakumykov.sociocat.z_base_view.iBaseView;


public interface iUserEdit {

    enum ViewState {
        INITIAL,
        PROGRESS,
        SUCCESS,
        ERROR
    }

    interface iView extends iBaseView {

        void setState(ViewState state, int messageId);
        void setState(ViewState state, int messageId, @Nullable String messageDetails);

        <T> void fillEditForm(User user, T avatar);
        <T> void displayAvatar(T avatar);
        void removeAvatar();

        void disableEditForm();
        void enableEditForm();

        void showAvatarThrobber();
        void hideAvatarThrobber();

        void showAvatarError();
        void hideAvatarError();

        String getName();
        String getEmail();
        String getAbout();

        void pickImage();

        void showAvatarRemoveDialog();
        void showCancelEditionDialog();

        void validateForm();
        void finishEdition(User user);

        String getPassword();

        void showPasswordError(String errorMsg);

        void goToEditEmail(User user);
    }

    interface iPresenter {
        void linkView(iView view);
        void unlinkView();

        void storeViewState(ViewState state, int messageId, String messageDetails);

        boolean hasUser();

        void onFirstOpen(@Nullable Intent intent);
        void onConfigChanged();

        void onImageSelectionSuccess(Bitmap bitmap, ImageType imageType);
        void onImageSelectionError(String errorMsg);

        void onUserLoggedOut();

        void onAvatarClicked();
        void onAvatarRemoveClicked();
        void onAvatarRemoveConfirmed();

        void onEmailEditClicked();

        void onSaveUserClicked();
        void onFormValidationSuccess();

        void onCancelButtonClicked();
        void onCancelEditionConfirmed();

        void onBackPressed();
    }

}
