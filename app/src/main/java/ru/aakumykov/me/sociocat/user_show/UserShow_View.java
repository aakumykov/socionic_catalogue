package ru.aakumykov.me.sociocat.user_show;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.aakumykov.me.sociocat.Constants;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.models.User;
import ru.aakumykov.me.sociocat.user_change_password.UserChangePassword_View;
import ru.aakumykov.me.sociocat.user_edit.UserEdit_View;
import ru.aakumykov.me.sociocat.user_show.view_model.UserShow_ViewModel;
import ru.aakumykov.me.sociocat.user_show.view_model.UserShow_ViewModelFactory;
import ru.aakumykov.me.sociocat.utils.ImageLoader;
import ru.aakumykov.me.sociocat.utils.MyUtils;
import ru.aakumykov.me.sociocat.z_base_view.BaseView;

public class UserShow_View extends BaseView implements iUserShow.iView
{
    @BindView(R.id.swipeRefreshLayout) SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.nameView) TextView nameView;
    @BindView(R.id.emailLabel) TextView emailLabel;
    @BindView(R.id.emailView) TextView emailView;
    @BindView(R.id.avatarView) ImageView avatarView;
    @BindView(R.id.avatarThrobber) ProgressBar avatarThrobber;
    @BindView(R.id.aboutView) TextView aboutView;
    @BindView(R.id.changePasswordWidget) View changePasswordWidget;
    @BindView(R.id.showUserCardsButton) Button showUserCardsButton;
    @BindView(R.id.showUserCommentsButton) Button showUserCommentsButton;

    private iUserShow.iPresenter presenter;
    private boolean isReturnFromActivity = false;


    // Activity
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_show_activity);
        ButterKnife.bind(this);

        setPageTitle(R.string.USER_SHOW_simple_page_title);
        activateUpButton();

        UserShow_ViewModel viewModel = new ViewModelProvider(this, new UserShow_ViewModelFactory())
                .get(UserShow_ViewModel.class);

        if (viewModel.hasPresenter()) {
            this.presenter = viewModel.getPresenter();
        } else {
            this.presenter = new UserShow_Presenter();
            viewModel.storePresenter(this.presenter);
        }

        configureSwipeRefresh();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        isReturnFromActivity = true;

        switch (requestCode) {
            case Constants.CODE_LOGIN:
                processLogin(resultCode, data);
                break;

            case Constants.CODE_USER_EDIT:
                processUserEditResult(resultCode, data);
                break;

            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        presenter.linkView(this);

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
    public boolean onCreateOptionsMenu(Menu menu) {
        if (presenter.canEditUser())
            getMenuInflater().inflate(R.menu.edit_user, menu);

//        return super.onCreateOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.actionEditUser:
                presenter.onEditClicked();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void goUserEdit(String userId) {
        Intent intent = new Intent(this, UserEdit_View.class);
        intent.putExtra(Constants.USER_ID, userId);
        startActivityForResult(intent, Constants.CODE_USER_EDIT);
    }

    // BaseView
    @Override
    public void onUserLogin() {
        presenter.onUserLoggedIn();
    }

    @Override
    public void onUserLogout() {
        presenter.onUserLoggedOut();
    }


    // iUserShow.iView
    @Override
    public void displayUser(@Nullable User user, boolean isPrivateMode) {
        if (null == user) {
            showErrorMsg(R.string.USER_SHOW_data_error, "User is null");
            return;
        }

        setPageTitle(R.string.USER_SHOW_complex_page_title, user.getName());

        loadAndShowAvatar(user);

        nameView.setText(user.getName());

        if (isPrivateMode) {
            emailView.setText(user.getEmail());
            MyUtils.show(emailLabel);
            MyUtils.show(emailView);
        }
        else {
            emailView.setText("");
            MyUtils.hide(emailLabel);
            MyUtils.hide(emailView);
        }

        aboutView.setText(user.getAbout());

        if (presenter.canEditUser())
            MyUtils.show(changePasswordWidget);
        else
            MyUtils.hide(changePasswordWidget);

        showUserCardsCount(user);
        showUserCommentsCount(user);

        refreshMenu();
    }

    private void showUserCardsCount(@NonNull User user) {
        showUserCardsButton.setText( getString(R.string.USER_SHOW_cards_of_user, user.getCardsKeys().size()) );
    }

    private void showUserCommentsCount(@NonNull User user) {
        showUserCommentsButton.setText( getString(R.string.USER_SHOW_comments_of_user, user.getCommentsKeys().size()) );
    }

    @Override
    public void setState(iUserShow.ViewState viewState, int messageId) {
        setState(viewState, messageId, null);
    }

    @Override
    public void setState(iUserShow.ViewState viewState, int messageId, @Nullable Object messagePayload) {
        presenter.storeViewState(viewState, messageId, messagePayload);

        switch (viewState) {
            case PROGRESS:
                hideProgressMessage();
                showRefreshThrobber();
                break;

            case SHOW_PUBLIC:
                hideRefreshThrobber();
                hideProgressMessage();
                displayUser((User) messagePayload, false);
                break;

            case SHOW_PRIVATE:
                hideRefreshThrobber();
                hideProgressMessage();
                displayUser((User) messagePayload, true);
                break;

            case ERROR:
                hideRefreshThrobber();
                showErrorMsg(messageId, (String) messagePayload);
                break;
        }
    }

    @Override
    public void showRefreshThrobber() {
        swipeRefreshLayout.setRefreshing(true);
    }

    @Override
    public void hideRefreshThrobber() {
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void showAvatarTrobber() {
        MyUtils.show(avatarThrobber);
        avatarView.setAlpha(0.5f);
    }

    @Override
    public void hideAvatarThrobber() {
        MyUtils.hide(avatarThrobber, true);
        avatarView.setAlpha(1.0f);
    }


    // Нажатия
    @OnClick(R.id.changePasswordWidget)
    void onChangePasswordClicked() {
        startActivityForResult(new Intent(this, UserChangePassword_View.class), Constants.CODE_CHANGE_PASSWORD);
    }

    @OnClick(R.id.showUserCardsButton)
    void onShowUserCardsButtonClicked() {

    }

    @OnClick(R.id.showUserCommentsButton)
    void onShowUserCommentsButtonClicked() {

    }


    // Внутренние методы
    private void configureSwipeRefresh() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override public void onRefresh() {
                presenter.onRefreshRequested();
            }
        });

        swipeRefreshLayout.setColorSchemeResources(R.color.blue_swipe, R.color.green_swipe, R.color.orange_swipe, R.color.red_swipe);
    }

    private void processLoginRequest(int resultCode, Intent data) {
        switch (resultCode) {
            case RESULT_OK:
                presenter.onFirstOpen(data);
                break;
            default:
                finish();
        }
    }

    private void processUserEditResult(int resultCode, @Nullable Intent data) {
        switch (resultCode) {
            case RESULT_FIRST_USER:
                showErrorMsg(R.string.USER_SHOW_data_error, "Unknown result code "+resultCode);
                return;
            case RESULT_CANCELED:
                return;
        }

        if (null == data) {
            showErrorMsg(R.string.USER_SHOW_data_error, "Intent data is null");
            return;
        }

        presenter.onUserEdited(data);
    }

    private void processLogin(int resultCode, @Nullable Intent data) {
        if (RESULT_OK == resultCode) {
            presenter.onUserLoggedIn();
        }
    }

    private void loadAndShowAvatar(User user) {

        if (null == user.getAvatarURL()) {
            avatarView.setImageResource(R.drawable.ic_avatar_placeholder);
            return;
        }

        showAvatarTrobber();

        ImageLoader.loadImage(this, user.getAvatarURL(), new ImageLoader.LoadImageCallbacks() {
            @Override
            public void onImageLoadSuccess(Bitmap imageBitmap) {
                hideAvatarThrobber();
                avatarView.setImageBitmap(imageBitmap);
            }

            @Override
            public void onImageLoadError(String errorMsg) {
                hideAvatarThrobber();
                avatarView.setImageResource(R.drawable.ic_avatar_placeholder);
            }
        });
    }



}
