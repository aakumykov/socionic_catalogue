package ru.aakumykov.me.sociocat.user_show;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.aakumykov.me.sociocat.BaseView;
import ru.aakumykov.me.sociocat.Constants;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.models.User;
import ru.aakumykov.me.sociocat.user_edit.UserEdit_View;
import ru.aakumykov.me.sociocat.user_show.view_model.UserShow_ViewModel;
import ru.aakumykov.me.sociocat.user_show.view_model.UserShow_ViewModelFactory;
import ru.aakumykov.me.sociocat.utils.ImageBitmapLoader;
import ru.aakumykov.me.sociocat.utils.MyUtils;

public class UserShow_View extends BaseView implements iUserShow.iView {

    @BindView(R.id.swipeRefreshLayout) SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.nameView) TextView nameView;
    @BindView(R.id.emailView) TextView emailView;
    @BindView(R.id.aboutView) TextView aboutView;
    @BindView(R.id.avatarView) ImageView avatarView;
    @BindView(R.id.avatarThrobber) ProgressBar avatarThrobber;

    private iUserShow.iPresenter presenter;
    private boolean userEditMode = false;


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
        switch (requestCode) {
            case Constants.CODE_LOGIN_REQUEST:
                processLoginRequest(resultCode, data);
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

        if (!userEditMode)
        {
            if (presenter.hasUser())
                presenter.onConfigChanged();
            else
                presenter.onFirstOpen(getIntent());
        }
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
        userEditMode = true;

        Intent intent = new Intent(this, UserEdit_View.class);
        intent.putExtra(Constants.USER_ID, userId);
        startActivityForResult(intent, Constants.CODE_USER_EDIT);
    }

    // BaseView
    @Override
    public void onUserLogin() {

    }

    @Override
    public void onUserLogout() {
        presenter.onUserLoggedOut();
    }


    // iUserShow.iView
    @Override
    public void displayUser(@Nullable User user) {
        if (null == user) {
            showErrorMsg(R.string.USER_SHOW_error_displaying_user, "User is null");
            return;
        }

        setPageTitle(R.string.USER_SHOW_complex_page_title, user.getName());

        loadAndShowAvatar(user);

        nameView.setText(user.getName());
        emailView.setText(user.getEmail());
        aboutView.setText(user.getAbout());
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
                showErrorMsg(R.string.USER_SHOW_error_displaying_user, "Unknown result code "+resultCode);
                return;
            case RESULT_CANCELED:
                return;
        }

        if (null == data) {
            showErrorMsg(R.string.USER_SHOW_error_displaying_user, "Intent data is null");
            return;
        }

        displayUser(data.getParcelableExtra(Constants.USER));
    }

    private void loadAndShowAvatar(User user) {

        if (null == user.getAvatarURL()) {
            avatarView.setImageResource(R.drawable.ic_avatar_placeholder);
            return;
        }

        showAvatarTrobber();

        ImageBitmapLoader.loadImage(this, user.getAvatarURL(), new ImageBitmapLoader.LoadImageCallbacks() {
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
