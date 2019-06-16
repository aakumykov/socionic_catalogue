package ru.aakumykov.me.sociocat.users.edit;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;
import ru.aakumykov.me.sociocat.BaseView;
import ru.aakumykov.me.sociocat.Config;
import ru.aakumykov.me.sociocat.Constants;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.models.User;
import ru.aakumykov.me.sociocat.users.Users_Presenter;
import ru.aakumykov.me.sociocat.users.iUsers;
import ru.aakumykov.me.sociocat.utils.MVPUtils.FileInfo;
import ru.aakumykov.me.sociocat.utils.MVPUtils.MVPUtils;
import ru.aakumykov.me.sociocat.utils.MVPUtils.iMVPUtils;
import ru.aakumykov.me.sociocat.utils.MyUtils;

// TODO: выбрасывание со страницы при разлогинивании

@RuntimePermissions
public class UserEdit_View extends BaseView implements
        iUsers.EditView
{
    @BindView(R.id.progressBar1) ProgressBar progressBar;
    @BindView(R.id.messageView) TextView messageView;

    @BindView(R.id.avatarThrobber) ProgressBar avatarThrobber;
    @BindView(R.id.avatarView) ImageView avatarView;

    @BindView(R.id.nameInput) EditText nameInput;
    @BindView(R.id.emailInput) EditText emailInput;
    @BindView(R.id.aboutInput) EditText aboutInput;

    @BindView(R.id.saveButton) Button saveButton;
    @BindView(R.id.cancelButton) Button cancelButton;

    private final static String TAG = "UserEdit_View";
    private iUsers.Presenter presenter;


    // Системные методы
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_edit_activity);
        ButterKnife.bind(this);

        activateUpButton();
        setPageTitle(R.string.USER_EDIT_page_title);

        UserEdit_ViewPermissionsDispatcher.checkPermissionsWithPermissionCheck(this);

        presenter = new Users_Presenter();

        Intent intent = getIntent();
        String userId = intent.getStringExtra(Constants.USER_ID);

        try {
            presenter.prepareUserEdit(userId);
        } catch (Exception e) {
            hideProgressMessage();
            showErrorMsg(R.string.USER_EDIT_error_loading_data, e.getMessage());
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        presenter.linkView(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        presenter.unlinkView();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        presenter.linkView(this);

        if (RESULT_OK == resultCode)
        {
            if (Constants.CODE_SELECT_IMAGE == requestCode) {
                presenter.processSelectedImage(data);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.save, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.actionSave:
                saveUser();
                break;
            default:
                super.onOptionsItemSelected(item);
        }
        return true;
    }


    // Обязательные методы
    @Override
    public void onUserLogin() {
        // Он обязан быть уже залогиненным (!)
    }

    // TODO: подключено ли это (я менял)?
    @Override
    public void onUserLogout() {
        closePage();
    }


    // Интерфейсные методы
    @Override
    public void fillUserForm(User user) {
        Log.d(TAG, "fillUserForm()");
        hideProgressMessage();

        nameInput.setText(user.getName());
        emailInput.setText(user.getEmail());
        aboutInput.setText(user.getAbout());

        if (user.hasAvatar())
            displayAvatar(user.getAvatarURL(), false);
    }

    @Override
    public void displayAvatar(String imageURI, boolean justSelected) {
        try {
            Uri uri = Uri.parse(imageURI);
            showAvatarThrobber();

            MVPUtils.loadImageWithResizeInto(
                    uri,
                    avatarView,
                    justSelected,
                    Config.AVATAR_MAX_WIDTH,
                    Config.AVATAR_MAX_HEIGHT,
                    new iMVPUtils.ImageLoadWithResizeCallbacks() {
                        @Override
                        public void onImageLoadWithResizeSuccess(FileInfo fileInfo) {
                            hideAvatarThrobber();
                        }

                        @Override
                        public void onImageLoadWithResizeFail(String errorMsg) {
                            hideAvatarThrobber();
                            showImageIsBroken(avatarView);
                            Log.e(TAG, errorMsg);
                        }
                    }
            );

        } catch (Exception e) {
            hideAvatarThrobber();
            showImageIsBroken(avatarView);
            e.printStackTrace();
        }
    }

    @Override
    public String getName() {
        return nameInput.getText().toString();
    }

    @Override
    public String getAbout() {
        return aboutInput.getText().toString();
    }

    @Override
    public Bitmap getImageBitmap() {
        return ((BitmapDrawable) avatarView.getDrawable()).getBitmap();

    }

    @Override
    public void finishEdit(User user, boolean isSuccessfull) {
        Intent intent = new Intent();
        intent.putExtra(Constants.USER, user);

        if (isSuccessfull) setResult(RESULT_OK, intent);
        else setResult(RESULT_CANCELED, intent);

        finish();
    }


    // Нажатия
    @OnClick(R.id.avatarView)
    void selectAvatar() {
        Intent intent = new Intent();
        intent.setType("image/*");
//        intent.setType("*/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);

        if (null != intent.resolveActivity(getPackageManager())) {
            startActivityForResult(
                    Intent.createChooser(intent, getResources().getString(R.string.select_image)),
                    Constants.CODE_SELECT_IMAGE
            );
        }
        else {
            showErrorMsg(R.string.USER_EDIT_error_selecting_image, "Error resolving activity for Intent.ACTION_GET_CONTENT");
        }
    }

    @OnClick(R.id.saveButton)
    void saveUser() {
        try {
            hideMsg();
            presenter.saveProfile();
        } catch (Exception e) {
            showErrorMsg(R.string.USER_EDIT_error_saving_user, e.getMessage());
            e.printStackTrace();
        }
    }

    @OnClick(R.id.cancelButton)
    void cancelEdit() {
        // TODO: останавливать отправку картинки... Но как?
        presenter.cancelButtonClicked();
    }


    // Методы интерфейса
    @Override
    public void enableEditForm() {
        nameInput.setEnabled(true);
        aboutInput.setEnabled(true);
        saveButton.setEnabled(true);
    }

    @Override
    public void disableEditForm() {
        nameInput.setEnabled(false);
        aboutInput.setEnabled(false);
        saveButton.setEnabled(false);
    }

    @Override
    public void showAvatarThrobber() {
        MyUtils.show(avatarThrobber);
    }

    @Override
    public void hideAvatarThrobber() {
        MyUtils.hide(avatarThrobber);
    }


    // TODO: делать это во время выбора
    @NeedsPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
    void checkPermissions() {

    }


    // Внутренние методы
    private void showImageIsBroken(ImageView imageView) {
        Drawable brokenImage = imageView.getContext().getResources().getDrawable(R.drawable.ic_image_broken);
        imageView.setImageDrawable(brokenImage);
    }
}
