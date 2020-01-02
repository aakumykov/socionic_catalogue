package ru.aakumykov.me.sociocat.user_edit;

import android.content.Intent;
import android.graphics.Bitmap;

import androidx.annotation.Nullable;

import ru.aakumykov.me.sociocat.Constants;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.models.User;
import ru.aakumykov.me.sociocat.singletons.AuthSingleton;
import ru.aakumykov.me.sociocat.singletons.StorageSingleton;
import ru.aakumykov.me.sociocat.singletons.UsersSingleton;
import ru.aakumykov.me.sociocat.singletons.iStorageSingleton;
import ru.aakumykov.me.sociocat.singletons.iUsersSingleton;
import ru.aakumykov.me.sociocat.user_edit.stubs.UserEdit_ViewStub;
import ru.aakumykov.me.sociocat.utils.ImageType;
import ru.aakumykov.me.sociocat.utils.ImageUtils;
import ru.aakumykov.me.sociocat.utils.MyDialogs;
import ru.aakumykov.me.sociocat.utils.MyUtils;


class UserEdit_Presenter implements iUserEdit.iPresenter {

    private iUserEdit.iView view;

    private iUsersSingleton usersSingleton = UsersSingleton.getInstance();
    private iStorageSingleton storageSingleton = StorageSingleton.getInstance();

    private User editedUser;
    private ImageType avatarImageType;
    private Bitmap avatarBitmap;
    private int errorMessageId = -1;
    private String consoleErrorMessage;


    @Override
    public void linkView(iUserEdit.iView view) {
        this.view = view;
    }

    @Override
    public void unlinkView() {
        this.view = new UserEdit_ViewStub();
    }

    @Override
    public boolean hasUser() {
        return null != editedUser;
    }

    @Override
    public void onFirstOpen(@Nullable Intent intent) {
        if (isGuest()) {
            view.requestLogin(intent);
            return;
        }

        // Не буду проверять Intent на null, так как страница используется внутри приложения.

        String userId = intent.getStringExtra(Constants.USER_ID);
        loadAndShowUser(userId);
    }

    @Override
    public void onConfigChanged() {
        if (isGuest()) {
            Intent intent = new Intent();
            intent.putExtra(Constants.USER_ID, editedUser.getKey());
            view.requestLogin(intent);
            return;
        }

        view.fillEditForm(editedUser, avatarBitmap);
        if (null != consoleErrorMessage && errorMessageId > -1) {
            view.showErrorMsg(errorMessageId, consoleErrorMessage);
        }
    }

    @Override
    public void onImageSelectionSuccess(Bitmap bitmap, ImageType imageType) {
        avatarBitmap = bitmap.copy(bitmap.getConfig(), true);
        avatarImageType = imageType;

        view.displayAvatar(bitmap);
    }

    @Override
    public void onImageSelectionError(String errorMsg) {
        avatarBitmap = null;
        avatarImageType = null;

        showError(R.string.USER_EDIT_error_selecting_image, errorMsg);
    }

    @Override
    public void onUserLoggedOut() {
        view.showToast(R.string.you_are_logged_out);
        view.closePage();
    }

    @Override
    public void onAvatarClicked() {
        if (null != editedUser) {
            hideError();
            view.hideAvatarError();
            view.pickImage();
        }
    }

    @Override
    public void onAvatarRemoveClicked() {
        if (null != editedUser)
            view.showAvatarRemoveDialog();
    }

    @Override
    public void onAvatarRemoveConfirmed() {
        if (null != avatarBitmap) {
            avatarBitmap = null;
            avatarImageType = null;
        }
        else {
            editedUser.setAvatarURL(null);
            editedUser.setAvatarFileName(null);
        }

        hideError();
        view.removeAvatar();
    }

    @Override
    public void onSaveUserClicked() {
        if (null != avatarBitmap || null != avatarImageType) {
            uploadAvatar(new iAvatarUploadCallbacks() {
                @Override
                public void onAvatarUploaded() {
                    saveUser();
                }
            });
        }
        else {
            saveUser();
        }
    }

    @Override
    public void onCancelButtonClicked() {
        cancelEdition();
    }

    @Override
    public void onCancelEditionConfirmed() {
        view.closePage();
    }

    @Override
    public void onBackPressed() {
        cancelEdition();
    }


    // Внутренние методы
    private boolean isGuest() {
        return !AuthSingleton.isLoggedIn();
    }

    private void loadAndShowUser(String userId) {
        view.disableEditForm();
        view.showProgressBar();

        usersSingleton.getUserById(userId, new iUsersSingleton.ReadCallbacks() {
            @Override
            public void onUserReadSuccess(User user) {
                editedUser = user;
                view.hideProgressBar();
                view.fillEditForm(user, user.getAvatarURL());
                view.enableEditForm();
            }

            @Override
            public void onUserReadFail(String errorMsg) {
                showError(R.string.USER_EDIT_error_loading_data, errorMsg);
                // TODO: показать кнопку "Попробовать ещё" или закрыть страницу, показав Toast?
            }
        });
    }

    private void uploadAvatar(iAvatarUploadCallbacks callbacks) {

        byte[] imageBytes = ImageUtils.compressImage(avatarBitmap, avatarImageType);
        String fileName = ImageUtils.makeFileName(editedUser.getKey(), avatarImageType);

        view.disableEditForm();
        view.showAvatarThrobber();
        view.showProgressMessage(R.string.USER_EDIT_saving_avatar);

        storageSingleton.uploadAvatar(imageBytes, fileName, new iStorageSingleton.FileUploadCallbacks() {
            @Override
            public void onFileUploadProgress(int progress) {

            }

            @Override
            public void onFileUploadSuccess(String fileName, String downloadURL) {
                editedUser.setAvatarFileName(fileName);
                editedUser.setAvatarURL(downloadURL);
                view.hideAvatarThrobber();
                callbacks.onAvatarUploaded();
            }

            @Override
            public void onFileUploadFail(String errorMsg) {
                showError(R.string.USER_EDIT_error_saving_avatar, errorMsg);
                view.hideAvatarThrobber();
                view.showAvatarError();
            }

            @Override
            public void onFileUploadCancel() {
                view.hideAvatarThrobber();
                view.enableEditForm();
                view.showToast(R.string.USER_EDIT_avatar_uploading_cancelled);
            }
        });
    }

    private interface iAvatarUploadCallbacks {
        void onAvatarUploaded();
    }

    private void saveUser() {

        // TODO: проверка!
        editedUser.setName(view.getName());
        editedUser.setEmail(view.getEmail());
        editedUser.setAbout(view.getAbout());

        view.disableEditForm();
        view.showProgressMessage(R.string.USER_EDIT_saving_user_profile);

        usersSingleton.saveUser(editedUser, new iUsersSingleton.SaveCallbacks() {
            @Override
            public void onUserSaveSuccess(User user) {
                view.finishEdition(user);
            }

            @Override
            public void onUserSaveFail(String errorMsg) {
                view.enableEditForm();
                showError(R.string.USER_EDIT_error_saving_user, errorMsg);
            }
        });

    }

    private void cancelEdition() {
        view.showCancelEditionDialog();
    }

    private void showError(int errorMessageId, String consoleMessage) {
        this.errorMessageId = errorMessageId;
        this.consoleErrorMessage = consoleMessage;

        view.showErrorMsg(errorMessageId, consoleMessage);
        view.enableEditForm();
    }

    private void hideError() {
        this.errorMessageId = -1;
        this.consoleErrorMessage = null;
        view.hideMessage();
    }
}
