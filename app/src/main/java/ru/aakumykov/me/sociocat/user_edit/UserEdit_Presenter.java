package ru.aakumykov.me.sociocat.user_edit;

import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;

import androidx.annotation.Nullable;

import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.constants.Constants;
import ru.aakumykov.me.sociocat.models.User;
import ru.aakumykov.me.sociocat.singletons.AuthSingleton;
import ru.aakumykov.me.sociocat.singletons.StorageSingleton;
import ru.aakumykov.me.sociocat.singletons.UsersSingleton;
import ru.aakumykov.me.sociocat.singletons.iAuthSingleton;
import ru.aakumykov.me.sociocat.singletons.iStorageSingleton;
import ru.aakumykov.me.sociocat.singletons.iUsersSingleton;
import ru.aakumykov.me.sociocat.user_edit.stubs.UserEdit_ViewStub;
import ru.aakumykov.me.sociocat.utils.ImageType;
import ru.aakumykov.me.sociocat.utils.ImageUtils;


class UserEdit_Presenter implements iUserEdit.iPresenter {

    private iUserEdit.ViewState currentViewState;
    private int currentMessageId;
    private String currentMessageDetails;


    private interface iAvatarUploadCallbacks {
        void onAvatarUploaded();
    }

    private final static String TAG = "UserEdit_Presenter";

    private iUserEdit.iView view;

    private iUsersSingleton usersSingleton = UsersSingleton.getInstance();
    private iStorageSingleton storageSingleton = StorageSingleton.getInstance();
    private iAuthSingleton authSingleton = AuthSingleton.getInstance();

    private User editedUser;
    private ImageType avatarImageType;
    private Bitmap avatarBitmap;
    private String oldAvatarFileName;

    private int errorMessageId = -1;
    private String consoleErrorMessage;

    private boolean saveInProgress = false;


    @Override
    public void linkView(iUserEdit.iView view) {
        this.view = view;
    }

    @Override
    public void unlinkView() {
        this.view = new UserEdit_ViewStub();
    }

    @Override
    public void storeViewState(iUserEdit.ViewState state, int messageId, String messageDetails) {
        currentViewState = state;
        currentMessageId = messageId;
        currentMessageDetails = messageDetails;
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

        if (null == intent) {
            showError(R.string.USER_EDIT_data_error, "There is no Intent");
            return;
        }

        String action = intent.getAction() + "";
        boolean isNewUser = Constants.ACTION_FILL_NEW_USER_PROFILE.equals(action);

        String userId = intent.getStringExtra(Constants.USER_ID);

        loadAndShowUser(userId, isNewUser);
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
            this.oldAvatarFileName = editedUser.getAvatarFileName();
            editedUser.setAvatarFileName(null);
        }

        hideError();
        view.removeAvatar();
    }

    @Override
    public void onEmailEditClicked() {
        view.goToEditEmail(usersSingleton.getCurrentUser());
    }

    @Override
    public void onSaveUserClicked() {
        view.validateForm();
    }

    @Override
    public void onFormValidationSuccess() {

        view.disableEditForm();
        view.showProgressMessage(R.string.USER_EDIT_checking_password);

        String email = usersSingleton.getCurrentUser().getEmail();
        String password = view.getPassword();

        AuthSingleton.checkPassword(email, password, new iAuthSingleton.CheckPasswordCallbacks() {
            @Override
            public void onUserCredentialsOk() {
                uploadAvatarAndSaveUser();
            }

            @Override
            public void onUserCredentialsNotOk(String errorMsg) {
                view.enableEditForm();
                view.showPasswordError(errorMsg);
            }
        });
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

    private void loadAndShowUser(String userId, boolean isNewUser) {

        if (isNewUser) {
            view.setState(iUserEdit.ViewState.INITIAL, -1);
            return;
        }

        view.setState(iUserEdit.ViewState.PROGRESS, R.string.USER_EDIT_loading_user_profile);

        usersSingleton.getUserById(userId, new iUsersSingleton.iReadCallbacks() {
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

    private void uploadAvatarAndSaveUser() {

        if (null != avatarBitmap && null != avatarImageType) {
            uploadAvatar(new iAvatarUploadCallbacks() {
                @Override
                public void onAvatarUploaded() {
                    saveUser();
                }
            });
        }
        else if (null == editedUser.getAvatarURL()) {
            view.disableEditForm();
            view.showProgressMessage(R.string.USER_EDIT_deleting_avatar_file);

            storageSingleton.deleteAvatar(oldAvatarFileName, new iStorageSingleton.FileDeletionCallbacks() {
                @Override
                public void onDeleteSuccess() {
                    saveUser();
                }

                @Override
                public void onDeleteFail(String errorMSg) {
                    Log.d(TAG, "Error deleting avatar file ("+oldAvatarFileName+"): "+errorMSg);
                    view.showToast(R.string.USER_EDIT_error_deleting_avatar_file);
                    saveUser();
                }
            });
        }
        else {
            saveUser();
        }
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

    private void saveUser() {

        // TODO: проверка!
        editedUser.setName(view.getName());
        editedUser.setEmail(view.getEmail());
        editedUser.setAbout(view.getAbout());

        view.disableEditForm();
        view.showProgressMessage(R.string.USER_EDIT_saving_user_profile);

        usersSingleton.saveUser(editedUser, new iUsersSingleton.iSaveCallbacks() {
            @Override
            public void onUserSaveSuccess(User user) {
                usersSingleton.storeCurrentUser(user);
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
        if (saveInProgress)
            return;

        view.showCancelEditionDialog();
    }

    private void showError(int errorMessageId, String errorMessageDetails) {
        view.setState(iUserEdit.ViewState.ERROR, errorMessageId, errorMessageDetails);
    }

    private void hideError() {
        this.errorMessageId = -1;
        this.consoleErrorMessage = null;
        view.hideMessage();
    }

    private void checkUserPassword(String password, PasswordCheckCallbacks callbacks) {


    }


    private interface PasswordCheckCallbacks {
        void onPasswordOk();
        void onPasswordNotOk(String errorMsg);
    }
}
