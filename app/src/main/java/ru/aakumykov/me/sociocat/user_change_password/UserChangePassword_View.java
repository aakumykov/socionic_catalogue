package ru.aakumykov.me.sociocat.user_change_password;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.aakumykov.me.sociocat.BaseView;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.user_change_password.view_model.UserChangePassword_ViewModel;
import ru.aakumykov.me.sociocat.user_change_password.view_model.UserChangePassword_ViewModelFactory;

public class UserChangePassword_View extends BaseView implements iUserChangePassword.iView {

    private iUserChangePassword.iPresenter presenter;


    // Activity
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_change_password_activity);
        ButterKnife.bind(this);

        setPageTitle(R.string.USER_CHANGE_PASSWORD_page_title);
        activateUpButton();

        UserChangePassword_ViewModel viewModel = new ViewModelProvider(this, new UserChangePassword_ViewModelFactory())
                .get(UserChangePassword_ViewModel.class);

        if (viewModel.hasPresenter()) {
            this.presenter = viewModel.getPresenter();
        } else {
            this.presenter = new UserChangePassword_Presenter();
            viewModel.storePresenter(this.presenter);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }

    @Override
    protected void onStart() {
        super.onStart();
        presenter.linkView(this);

        if (presenter.hasItem())
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

    }


    // Нажатия
    @OnClick(R.id.saveButton)
    void onButtonClicked() {
        presenter.onButtonClicked();
    }

    @OnClick(R.id.cancelButton)
    void onCancelButtonClicked() {
        
    }
}
