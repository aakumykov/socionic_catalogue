package ru.aakumykov.me.sociocat.users.edit;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;
import ru.aakumykov.me.sociocat.BaseView;
import ru.aakumykov.me.sociocat.Constants;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.models.User;
import ru.aakumykov.me.sociocat.users.Users_Presenter;
import ru.aakumykov.me.sociocat.users.iUsers;
import ru.aakumykov.me.sociocat.users.view_model.Users_ViewModel;
import ru.aakumykov.me.sociocat.users.view_model.Users_ViewModelFactory;
import ru.aakumykov.me.sociocat.utils.ImageInfo;
import ru.aakumykov.me.sociocat.utils.ImageSelector;
import ru.aakumykov.me.sociocat.utils.MyUtils;

// TODO: выбрасывание со страницы при разлогинивании

@RuntimePermissions
public class UserEdit_View extends BaseView implements
        iUsers.EditView
{
    @BindView(R.id.progressBar) ProgressBar progressBar;
    @BindView(R.id.commentTextView) TextView messageView;

    @BindView(R.id.avatarThrobber) ProgressBar avatarThrobber;
    @BindView(R.id.avatarView) ImageView avatarView;

    @BindView(R.id.nameInput) EditText nameInput;
    @BindView(R.id.emailInput) EditText emailInput;
    @BindView(R.id.aboutInput) EditText aboutInput;

    @BindView(R.id.saveButton) Button saveButton;
    @BindView(R.id.cancelButton) Button cancelButton;

    private final static String TAG = "UserEdit_View";
    private iUsers.Presenter presenter;
    private boolean isImageSelectionMode = false;


    // Системные методы
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_edit_activity);
        ButterKnife.bind(this);

        activateUpButton();
        setPageTitle(R.string.USER_EDIT_page_title);

        UserEdit_ViewPermissionsDispatcher.checkPermissionsWithPermissionCheck(this);

        Users_ViewModel usersViewModel = new ViewModelProvider(this, new Users_ViewModelFactory()).get(Users_ViewModel.class);
        if (usersViewModel.hasPresenter()) {
            presenter = usersViewModel.getPresenter();
        } else {
            presenter = new Users_Presenter();
            usersViewModel.storePresenter(presenter);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        presenter.linkView(this);

        if (isImageSelectionMode) {
            isImageSelectionMode = false;
            return;
        }

        if (presenter.hasUser())
            presenter.onConfigurationChanged();
        else
            presenter.onFirstOpen();
    }

    @Override
    protected void onStop() {
        super.onStop();
        presenter.unlinkView();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        presenter.linkView(this);

        switch (requestCode) {
            case ImageSelector.CODE_SELECT_IMAGE:
                processImageSelection(resultCode, data);
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
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
                onSaveButtonClicked();
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
        presenter.onUserLoggedOut();
    }


    @Override
    public <T> void displayUser(User user) {
        displayUser(user, user.getAvatarURL());
    }

    // Интерфейсные методы
    @Override
    public <T> void displayUser(User user, @Nullable T avatar) {
        nameInput.setText(user.getName());
        emailInput.setText(user.getEmail());
        aboutInput.setText(user.getAbout());

        Object userAvatar = (null != avatar) ? avatar : user.getAvatarURL();
        displayAvatar(userAvatar);
    }

    @Override
    public <T> void displayAvatar(T avatar) {
        try {
            Glide.with(this)
                    .load(avatar)
                    .placeholder(R.drawable.ic_avatar_placeholder)
                    .error(R.drawable.ic_image_error)
                    .into(avatarView);

            avatar = null;
        }
        catch (Exception e) {
            avatarView.setImageResource(R.drawable.ic_image_error);
            MyUtils.printError(TAG, e);
        }
    }

    @Override
    public String getName() {
        return nameInput.getText().toString();
    }

    @Override
    public String getEmail() {
        return emailInput.getText().toString();
    }

    @Override
    public String getAbout() {
        return aboutInput.getText().toString();
    }

    @Override
    public void finishEdit(User user, boolean isSuccessfull) {
        showToast(R.string.USER_EDIT_profile_saved);

        Intent intent = new Intent();
        intent.putExtra(Constants.USER, user);

        if (isSuccessfull) setResult(RESULT_OK, intent);
        else setResult(RESULT_CANCELED, intent);

        finish();
    }


    // Нажатия
    @OnClick(R.id.avatarView)
    void onAvatarClicked() {
        isImageSelectionMode = true;
        if (! ImageSelector.selectImage(this) ) {
            showErrorMsg(R.string.error_selecting_image, "Cannot launch file selector");
        }
    }

    @OnClick(R.id.saveButton)
    void onSaveButtonClicked() {
        presenter.onSaveUserClicked();
    }

    @OnClick(R.id.cancelButton)
    void onCancelButtonClicked() {
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
    private void processImageSelection(int resultCode, @Nullable Intent data) {
        switch (resultCode) {
            case RESULT_OK:
                processSelectedImage(data);
                break;
            case RESULT_CANCELED:
                break;
            default:
                showErrorMsg(R.string.error_selecting_image, "Unknown result code");
        }
    }

    private void processSelectedImage(@Nullable Intent data) {
        if (null == data) {
            showErrorMsg(R.string.error_selecting_image, "Intent data is null");
            return;
        }

        ImageInfo imageInfo = ImageSelector.extractImageInfo(this, data);
        if (null == imageInfo) {
            showErrorMsg(R.string.error_processing_image, "ImageInfo is null");
            return;
        }

        Glide.with(this)
                .load(imageInfo.getLocalURI())
                .into(new CustomTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        BitmapDrawable bitmapDrawable = (BitmapDrawable) resource;
                        Bitmap bitmap = bitmapDrawable.getBitmap();

                        presenter.onImageSelected(bitmap);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                        Log.d(TAG, "onLoadCleared()");
                    }
                });
    }
}
