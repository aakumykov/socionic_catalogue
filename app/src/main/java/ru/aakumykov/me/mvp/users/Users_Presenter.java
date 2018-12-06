package ru.aakumykov.me.mvp.users;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.InputStream;

import ru.aakumykov.me.mvp.Constants;
import ru.aakumykov.me.mvp.R;
import ru.aakumykov.me.mvp.interfaces.iAuthSingleton;
import ru.aakumykov.me.mvp.interfaces.iStorageSingleton;
import ru.aakumykov.me.mvp.interfaces.iUsersSingleton;
import ru.aakumykov.me.mvp.models.User;
import ru.aakumykov.me.mvp.services.AuthSingleton;
import ru.aakumykov.me.mvp.services.StorageSingleton;
import ru.aakumykov.me.mvp.services.UsersSingleton;
import ru.aakumykov.me.mvp.utils.MVPUtils.MVPUtils;
import ru.aakumykov.me.mvp.utils.MVPUtils.iMVPUtils;

public class Users_Presenter implements
        iUsers.Presenter,
        iUsersSingleton.ReadCallbacks,
        iUsersSingleton.SaveCallbacks,
        iStorageSingleton.FileUploadCallbacks
{

    private final static String TAG = "Users_Presenter";
    private iUsers.ShowView showView;
    private iUsers.ListView listView;
    private iUsers.EditView editView;
    private iUsersSingleton usersService = UsersSingleton.getInstance();
    private iAuthSingleton authService = AuthSingleton.getInstance();
    private iStorageSingleton storageService = StorageSingleton.getInstance();

    private User currentUser;
    private String editedUserId;
    private boolean imageSelected = false;

    // Системные методы
    @Override
    public void linkView(iUsers.View view) throws IllegalArgumentException {

        if (view instanceof iUsers.ListView) {
            Log.d(TAG, "linkView(ListView)");
            this.listView = (iUsers.ListView) view;
        }
        else if (view instanceof iUsers.ShowView) {
            Log.d(TAG, "linkView(ShowView)");
            this.showView = (iUsers.ShowView) view;
        }
        else if (view instanceof iUsers.EditView) {
            Log.d(TAG, "linkView(EditView)");
            this.editView = (iUsers.EditView) view;
        }
        else {
            throw new IllegalArgumentException("Unknown type of View '"+view.getClass()+"'");
        }
    }

    @Override
    public void unlinkView() {
        Log.d(TAG, "unlinkView()");
        this.listView = null;
        this.showView = null;
        this.editView = null;
    }


    // Пользовательские методы
    @Override
    public void prepareUserEdit(String userId) throws Exception {
        if (authService.isUserLoggedIn())
        {
            if (authService.currentUserId().equals(userId))
            {
                usersService.getUser(userId, this);
            }
            else {
                throw new IllegalAccessException("Cannot edit profile of another user.");
            }
        }
        else {
            throw new IllegalAccessException("You must be logged in.");
        }
    }

    @Override
    public void setImageSelected(boolean isSelected) {
        imageSelected = isSelected;
    }

    @Override
    public void loadUser(String userId, iUsersSingleton.ReadCallbacks callbacks) throws Exception {
        if (null == userId) {
            throw new Exception("userId == null");
        }
        usersService.getUser(userId, callbacks);
    }

    @Override
    public void cancelButtonClicked() {
        Log.d(TAG, "cancelButtonClicked()");
        editView.closePage();
    }

    @Override
    public void loadList(iUsersSingleton.ListCallbacks callbacks) {
        Log.d(TAG, "loadList()");
        usersService.listUsers(callbacks);
    }

    @Override
    public void listItemClicked(String key) {
        Log.d(TAG, "listItemClicked("+key+")");
        listView.goUserPage(key);
    }

    @Override
    public void saveProfile() throws Exception {

        if (!authService.isUserLoggedIn()) {
            throw new IllegalAccessException("You is not logged in.");
        }

        if (!authService.currentUserId().equals(editedUserId)) {
            throw new IllegalAccessException("Cannot save profile of another user.");
        }

        String name = editView.getName();
        if (TextUtils.isEmpty(name)) {
            editView.showErrorMsg(R.string.USER_EDIT_name_cannot_be_empty);
            return;
        }

        currentUser.setName(name);
        currentUser.setAbout(editView.getAbout());

        if (!currentUser.hasAvatar() && imageSelected) {
            try {
                byte[] imageByteArray = editView.getImageData();
                String fileName = authService.currentUserId() + ".jpg";

                editView.showAvatarThrobber();
                editView.disableEditForm();
                editView.showInfoMsg(R.string.USER_EDIT_saving_avatar);

                storageService.uploadAvatar(imageByteArray, fileName, this);

            } catch (Exception e) {
                onFileUploadFail(e.getMessage());
                e.printStackTrace();
            }
        }
        else {
            saveUser();
        }
    }

    @Override
    public void processSelectedImage(@Nullable Intent data) {

        if (null == data) {
            editView.showErrorMsg(R.string.USER_EDIT_error_selecting_image);
            return;
        }

        // Первый способ получить содержимое
        Uri imageURI = data.getParcelableExtra(Intent.EXTRA_STREAM);

        // Второй способ получить содержимое
        if (null == imageURI) {
            imageURI = data.getData();
            if (null == imageURI) {
                editView.showErrorMsg(R.string.USER_EDIT_no_image_data);
                return;
            }
        }

        setImageSelected(true);
        currentUser.setAvatarURL("");
        editView.displayAvatar(imageURI.toString(), true);
    }


    // Методы обратного вызова
    @Override
    public void onUserReadSuccess(final User user) {
        currentUser = user;
        editedUserId = user.getKey();

        editView.hideProgressBar();
        editView.fillUserForm(user);
    }

    @Override
    public void onUserReadFail(String errorMsg) {
        editView.hideProgressBar();
        editView.hideProgressBar();
        editView.showErrorMsg(R.string.USER_EDIT_error_loading_data, errorMsg);
    }

    @Override
    public void onFileUploadProgress(int progress) {

    }

    @Override
    public void onFileUploadSuccess(String downloadURL) {
        editView.hideAvatarThrobber();
        currentUser.setAvatarURL(downloadURL);
        saveUser();
    }

    @Override
    public void onFileUploadFail(String errorMsg) {
        editView.showErrorMsg(R.string.USER_EDIT_error_saving_avatar);
        editView.hideAvatarThrobber();
        editView.enableEditForm();
    }

    @Override
    public void onFileUploadCancel() {
        editView.hideAvatarThrobber();
        editView.enableEditForm();
    }

    @Override
    public void onUserSaveSuccess(User user) {
        authService.storeCurrentUser(user);
        editView.hideProgressBar();
        editView.finishEdit(user, true);
    }

    @Override
    public void onUserSaveFail(String errorMsg) {
        editView.hideProgressBar();
        editView.enableEditForm();
        editView.showErrorMsg(R.string.USER_EDIT_error_saving_profile, errorMsg);
    }


    // Внутренние методы
    private void saveUser() {
        editView.showProgressBar();
        editView.disableEditForm();
        editView.showInfoMsg(R.string.USER_EDIT_saving_profile);

        try {
            usersService.saveUser(currentUser, new iUsersSingleton.SaveCallbacks() {
                @Override
                public void onUserSaveSuccess(User user) {
                    editView.finishEdit(user, true);
                }

                @Override
                public void onUserSaveFail(String errorMsg) {
                    editView.showErrorMsg(R.string.USER_EDIT_error_saving_user, errorMsg);
                    editView.enableEditForm();
                }
            });
        } catch (Exception e) {
            onUserSaveFail(e.getMessage());
            e.printStackTrace();
        }
    }

    private void detectImageWidthAndHeight(Uri imageURI) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

//        BitmapFactory.decodeFile(imageURI)
    }
}
