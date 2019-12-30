package ru.aakumykov.me.sociocat.user_edit;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

import ru.aakumykov.me.sociocat.Config;
import ru.aakumykov.me.sociocat.Constants;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.models.User;
import ru.aakumykov.me.sociocat.singletons.AuthSingleton;
import ru.aakumykov.me.sociocat.singletons.StorageSingleton;
import ru.aakumykov.me.sociocat.singletons.UsersSingleton;
import ru.aakumykov.me.sociocat.singletons.iStorageSingleton;
import ru.aakumykov.me.sociocat.singletons.iUsersSingleton;
import ru.aakumykov.me.sociocat.user_edit.stubs.UserEdit_ViewStub;
import ru.aakumykov.me.sociocat.utils.ImageInfo;
import ru.aakumykov.me.sociocat.utils.ImageType;
import ru.aakumykov.me.sociocat.utils.ImageUtils;
import ru.aakumykov.me.sociocat.utils.MyUtils;


class UserEdit_Presenter implements iUserEdit.iPresenter {

    private final static String TAG = "UserEdit_Presenter";
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
    public void onImageSelected(@Nullable Intent data) {
        if (null == data) {
            view.showErrorMsg(R.string.error_selecting_image, "Intent data is null");
            return;
        }

        try {
            ImageInfo imageInfo = ImageUtils.extractImageInfo(this, data);

            Glide.with(view.getActivity())
                    .load(imageInfo.getLocalURI())
                    .into(new CustomTarget<Drawable>() {
                        @Override
                        public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                            Bitmap bitmap;

                            if (resource instanceof GifDrawable) {
                                GifDrawable gifDrawable = (GifDrawable) resource;
                                bitmap = gifDrawable.getFirstFrame();
                            }
                            else if (resource instanceof BitmapDrawable) {
                                BitmapDrawable bitmapDrawable = (BitmapDrawable) resource;
                                bitmap = bitmapDrawable.getBitmap();
                            }
                            else {
                                view.showToast(R.string.ERROR_unsupported_image_type);
                                return;
                            }

                            presenter.onImageSelected(bitmap, imageInfo.getImageType());
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {
                            Log.d(TAG, "onLoadCleared()");
                        }
                    });
        }
        catch (ImageUtils.ImageUtils_Exception e) {
//            showAvatarError();
            showErrorMsg(R.string.USER_EDIT_error_selecting_image, e.getMessage());
            MyUtils.printError(TAG, e);
        }

        this.avatarImageType = imageType;

        Bitmap bitmapCopy = bitmap.copy(bitmap.getConfig(), true);
        avatarBitmap = ImageUtils.scaleDownBitmap(bitmapCopy, Config.AVATAR_MAX_SIZE);

        view.hideAvatarError();
        view.displayAvatar(avatarBitmap);
    }

    @Override
    public void onUserLoggedOut() {
        view.showToast(R.string.you_are_logged_out);
        view.closePage();
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
                view.showAvatarError();
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
