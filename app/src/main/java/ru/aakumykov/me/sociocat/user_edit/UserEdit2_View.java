package ru.aakumykov.me.sociocat.user_edit;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.aakumykov.me.sociocat.BaseView;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.models.User;
import ru.aakumykov.me.sociocat.user_edit.view_model.UserEdit_ViewModel;
import ru.aakumykov.me.sociocat.user_edit.view_model.UserEdit_ViewModelFactory;
import ru.aakumykov.me.sociocat.utils.ImageUtils;
import ru.aakumykov.me.sociocat.utils.MyUtils;

public class UserEdit2_View extends BaseView implements iUserEdit.iView {

    @BindView(R.id.avatarThrobber) ProgressBar avatarThrobber;
    @BindView(R.id.avatarView) ImageView avatarView;

    @BindView(R.id.nameInput) EditText nameInput;
    @BindView(R.id.emailInput) EditText emailInput;
    @BindView(R.id.aboutInput) EditText aboutInput;

    @BindView(R.id.saveButton) Button saveButton;
    @BindView(R.id.cancelButton) Button cancelButton;

    private static final String TAG = "UserEdit2_View";
    private iUserEdit.iPresenter presenter;
    private boolean isImageSelectionMode;


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
        super.onActivityResult(requestCode, resultCode, data);
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
    public void enableEditForm() {
        nameInput.setEnabled(true);
        aboutInput.setEnabled(true);
        saveButton.setEnabled(true);
        avatarView.setEnabled(true);
    }

    @Override
    public void disableEditForm() {
        nameInput.setEnabled(false);
        aboutInput.setEnabled(false);
        saveButton.setEnabled(false);
        avatarView.setEnabled(false);
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

    
    // Нажатия
    @OnClick(R.id.avatarView)
    void onAvatarClicked() {
        isImageSelectionMode = true;
        if (! ImageUtils.pickImage(this) ) {
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
        presenter.onCancelButtonClicked();
    }

}
