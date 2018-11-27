package ru.aakumykov.me.mvp.users.edit;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;
import ru.aakumykov.me.mvp.BaseView;
import ru.aakumykov.me.mvp.Constants;
import ru.aakumykov.me.mvp.R;
import ru.aakumykov.me.mvp.models.User;
import ru.aakumykov.me.mvp.users.Users_Presenter;
import ru.aakumykov.me.mvp.users.iUsers;

// TODO: выбрасывание со страницы при разлогинивании

@RuntimePermissions
public class UserEdit_View extends BaseView implements
        iUsers.EditView
{
    @BindView(R.id.progressBar) ProgressBar progressBar;
    @BindView(R.id.messageView) TextView messageView;
    @BindView(R.id.avatarView) ImageView avatarView;
    @BindView(R.id.nameInput) EditText nameInput;
    @BindView(R.id.aboutInput) EditText aboutInput;
    @BindView(R.id.saveButton) Button saveButton;
    @BindView(R.id.cancelButton) Button cancelButton;

    private final static String TAG = "UserEdit_View";
    private iUsers.Presenter presenter;
    private User currentUser;

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
            hideProgressBar();
            showErrorMsg(R.string.USER_EDIT_error_loading_data);
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
        hideProgressBar();
        nameInput.setText(user.getName());
        aboutInput.setText(user.getAbout());
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
        selectImage();
    }

    @OnClick(R.id.saveButton)
    void saveUser() {
        String name = nameInput.getText().toString();
        String about = aboutInput.getText().toString();
        presenter.updateUser(name, about);
    }

    @OnClick(R.id.cancelButton)
    void cancelEdit() {
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
    public void displayAvatar(Uri imageURI) {
        Picasso.get()
                .load(imageURI)
                .into(avatarView, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError(Exception e) {

                    }
                });

    }


    // Внутренние методы
    public void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);

        if (null != intent.resolveActivity(getPackageManager())) {
            startActivityForResult(
                    Intent.createChooser(intent, getResources().getString(R.string.select_image)),
                    Constants.CODE_SELECT_IMAGE
            );
        }
        else {
            showErrorMsg(R.string.USER_EDIT_error_selecting_image);
            Log.e(TAG, "Error resolving activity for Intent.ACTION_GET_CONTENT");
        }
    }


    // TODO: делать это во время выбора
    @NeedsPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
    void checkPermissions() {

    }
}
