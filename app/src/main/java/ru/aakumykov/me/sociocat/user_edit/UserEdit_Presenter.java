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


class UserEdit_Presenter implements iUserEdit.iPresenter {

    private iUserEdit.iView view;

    private iUsersSingleton usersSingleton = UsersSingleton.getInstance();
    private iStorageSingleton storageSingleton = StorageSingleton.getInstance();

    private User editedUser;
    private ImageType avatarImageType;
    private Bitmap avatarBitmap;


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

        view.showAvatarError(R.string.USER_EDIT_error_selecting_image, errorMsg);
    }

    @Override
    public void onUserLoggedOut() {
        view.showToast(R.string.you_are_logged_out);
        view.closePage();
    }

    @Override
    public void onAvatarClicked() {
        view.hideAvatarError();
        view.pickImage();
    }

    @Override
    public void onSaveUserClicked() {
        if (null != avatarBitmap || null != avatarImageType) {
            uploadAvatar(new iAvatarUploadCallbacks() {
                @Override
                public void onAvatarUploaded() {
                    //saveUser();
                }
            });
        }
        else {
            //saveUser();
        }
    }

    @Override
    public void onCancelButtonClicked() {
        view.closePage();
    }

    @Override
    public void onBackPressed() {
        view.closePage();
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
                view.showErrorMsg(R.string.USER_EDIT_error_loading_data, errorMsg);
                // TODO: показать кнопку "Попробовать ещё" или закрыть страницу, показав Toast?
            }
        });
    }

    private void uploadAvatar(iAvatarUploadCallbacks callbacks) {

        byte[] imageBytes = ImageUtils.compressImage(avatarBitmap, avatarImageType);
        String fileName = ImageUtils.makeFileName(editedUser.getKey(), avatarImageType);

        view.disableEditForm();
        view.showAvatarThrobber();

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
                view.showErrorMsg(R.string.USER_EDIT_error_saving_avatar, errorMsg);
                view.hideAvatarThrobber();
                view.showAvatarError(R.string.USER_EDIT_error_selecting_image, errorMsg);
                view.enableEditForm();
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

}
