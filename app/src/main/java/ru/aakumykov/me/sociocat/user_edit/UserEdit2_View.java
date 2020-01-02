package ru.aakumykov.me.sociocat.user_edit;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.aakumykov.me.sociocat.BaseView;
import ru.aakumykov.me.sociocat.Constants;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.interfaces.iDialogCallbacks;
import ru.aakumykov.me.sociocat.models.User;
import ru.aakumykov.me.sociocat.user_edit.view_model.UserEdit_ViewModel;
import ru.aakumykov.me.sociocat.user_edit.view_model.UserEdit_ViewModelFactory;
import ru.aakumykov.me.sociocat.utils.ImageType;
import ru.aakumykov.me.sociocat.utils.ImageUtils;
import ru.aakumykov.me.sociocat.utils.MyUtils;
import ru.aakumykov.me.sociocat.utils.YesNoDialog;

public class UserEdit2_View extends BaseView implements iUserEdit.iView {

    @BindView(R.id.avatarView) ImageView avatarView;
    @BindView(R.id.avatarThrobber) ProgressBar avatarThrobber;
    @BindView(R.id.avatarRemoveWidget) ImageView avatarRemoveWidget;

    @BindView(R.id.nameInput) EditText nameInput;
    @BindView(R.id.emailInput) EditText emailInput;
    @BindView(R.id.aboutInput) EditText aboutInput;

    @BindView(R.id.saveButton) Button saveButton;
    @BindView(R.id.cancelButton) Button cancelButton;

    private static final String TAG = "UserEdit2_View";
    private iUserEdit.iPresenter presenter;
    private boolean isImageSelectionMode = false;
    private boolean isFormDisabled = false;


    // Activity
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_edit_activity);
        ButterKnife.bind(this);

        setPageTitle(R.string.PAGE_TEMPLATE_page_title);
        activateUpButton();

        UserEdit_ViewModel viewModel = new ViewModelProvider(this, new UserEdit_ViewModelFactory())
                .get(UserEdit_ViewModel.class);

        if (viewModel.hasPresenter()) {
            this.presenter = viewModel.getPresenter();
        } else {
            this.presenter = new UserEdit_Presenter();
            viewModel.storePresenter(this.presenter);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        presenter.linkView(this);

        switch (requestCode) {
            case ImageUtils.CODE_SELECT_IMAGE:
                processImageSelection(resultCode, data);
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
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
            presenter.onConfigChanged();
        else
            presenter.onFirstOpen(getIntent());
    }

    @Override
    protected void onStop() {
        super.onStop();
        presenter.unlinkView();
    }

    @Override
    public void onBackPressed() {
        presenter.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.save, menu);

        MenuItem menuItem =menu.findItem(R.id.actionSave);
        menuItem.setEnabled(!isFormDisabled);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.actionSave:
                presenter.onSaveUserClicked();
                break;
            case android.R.id.home:
                presenter.onCancelButtonClicked();
                break;
            default:
                super.onOptionsItemSelected(item);
        }
        return true;
    }


    // BaseView
    @Override
    public void onUserLogin() {

    }

    @Override
    public void onUserLogout() {
        presenter.onUserLoggedOut();
    }


    // iUserEdit.iView
    @Override
    public <T> void fillEditForm(User user, T avatar) {
        nameInput.setText(user.getName());
        emailInput.setText(user.getEmail());
        aboutInput.setText(user.getAbout());

        Object userAvatar = (null != avatar) ? avatar : user.getAvatarURL();
        displayAvatar(userAvatar);
    }

    @Override
    public <T> void displayAvatar(T avatar) {
        if (null == avatar) {
            hideAvatarThrobber();
            return;
        }

        if (avatar instanceof Bitmap) {
            avatarView.setImageBitmap((Bitmap) avatar);
            hideAvatarThrobber();
            avatar = null;
        }
        else {
            try {
                Glide.with(this)
                        .load(avatar)
                        .placeholder(R.drawable.ic_avatar_placeholder)
                        .error(R.drawable.ic_image_error)
                        .into(new CustomTarget<Drawable>() {
                            @Override
                            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                avatarView.setImageDrawable(resource);
                                hideAvatarThrobber();
                            }

                            @Override
                            public void onLoadCleared(@Nullable Drawable placeholder) {
                                hideAvatarThrobber();
                            }
                        });
            }
            catch (Exception e) {
                avatarView.setImageResource(R.drawable.ic_image_error);
                MyUtils.printError(TAG, e);
            }
        }
    }

    @Override
    public void removeAvatar() {
        avatarView.setImageResource(R.drawable.ic_add_avatar);
    }

    @Override
    public void disableEditForm() {
        isFormDisabled = true;
        refreshMenu();

        MyUtils.disable(nameInput);
        MyUtils.disable(emailInput);
        MyUtils.disable(aboutInput);
        MyUtils.disable(saveButton);

        MyUtils.disable(avatarView);
        avatarView.setAlpha(0.5f);
    }

    @Override
    public void enableEditForm() {
        isFormDisabled = false;
        refreshMenu();

        MyUtils.enable(nameInput);
        MyUtils.enable(emailInput);
        MyUtils.enable(aboutInput);
        MyUtils.enable(saveButton);

        MyUtils.enable(avatarView);
        avatarView.setAlpha(1.0f);
    }

    @Override
    public void showAvatarThrobber() {
        MyUtils.show(avatarThrobber);
    }

    @Override
    public void hideAvatarThrobber() {
        MyUtils.hide(avatarThrobber, true);
    }

    @Override
    public void showAvatarError() {
        avatarView.setBackgroundResource(R.drawable.shape_avatar_error_border);
    }

    @Override
    public void hideAvatarError() {
        avatarView.setBackgroundResource(R.drawable.shape_avatar_normal_border);
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
    public void pickImage() {
        if (! ImageUtils.pickImage(this) )
            showErrorMsg(R.string.error_selecting_image, "Cannot launch file selector");
        else
            isImageSelectionMode = true;
    }

    @Override
    public void showAvatarRemoveDialog() {

        new YesNoDialog(this, R.string.USER_EDIT_remove_avatar_question, null, new iDialogCallbacks.Delete() {
            @Override
            public boolean deleteDialogCheck() {
                return true;
            }

            @Override
            public void deleteDialogYes() {
                presenter.onAvatarRemoveConfirmed();
            }

            @Override
            public void onDeleteDialogNo() {

            }
        }).show();
    }

    @Override
    public void finishEdition(User user) {
        Intent intent = new Intent();
        intent.putExtra(Constants.USER, user);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void cancelEdition() {
        setResult(RESULT_CANCELED);
        finish();
    }


    // Нажатия
    @OnClick(R.id.avatarView)
    void onAvatarClicked() {
        presenter.onAvatarClicked();
    }

    @OnClick(R.id.avatarRemoveWidget)
    void onAvatarRemoveClicked() {
        presenter.onAvatarRemoveClicked();
    }

    @OnClick(R.id.saveButton)
    void onSaveButtonClicked() {
        presenter.onSaveUserClicked();
    }

    @OnClick(R.id.cancelButton)
    void onCancelButtonClicked() {
        // TODO: останавливать отправку картинки... Но как?
        presenter.onCancelButtonClicked();
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
        try {
            ImageUtils.extractImageFromIntent(this, data, new ImageUtils.ImageExtractionCallbacks() {
                @Override
                public void onImageExtractionSuccess(Bitmap bitmap, ImageType imageType) {
                    presenter.onImageSelectionSuccess(bitmap, imageType);
                }

                @Override
                public void onImageExtractionError(String errorMsg) {
                    presenter.onImageSelectionError(errorMsg);
                }
            });
        }
        catch (ImageUtils.ImageUtils_Exception e) {
            presenter.onImageSelectionError(e.getMessage());
            MyUtils.printError(TAG, e);
        }
    }
}
