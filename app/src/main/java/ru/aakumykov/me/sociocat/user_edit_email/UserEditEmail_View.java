package ru.aakumykov.me.sociocat.user_edit_email;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.aakumykov.me.sociocat.BaseView;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.models.User;
import ru.aakumykov.me.sociocat.user_edit_email.view_model.UserEmailEdit_ViewModel;
import ru.aakumykov.me.sociocat.user_edit_email.view_model.UserEmailEdit_ViewModelFactory;

public class UserEditEmail_View extends BaseView implements iUserEditEmail.iView {

    @BindView(R.id.emailInput) EditText emailInput;

    private iUserEditEmail.iPresenter presenter;

    // Activity
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_edit_email_activity);
        ButterKnife.bind(this);

        setPageTitle(R.string.PAGE_TEMPLATE_page_title);
        activateUpButton();

        UserEmailEdit_ViewModel viewModel = new ViewModelProvider(this, new UserEmailEdit_ViewModelFactory())
                .get(UserEmailEdit_ViewModel.class);

        if (viewModel.hasPresenter()) {
            this.presenter = viewModel.getPresenter();
        } else {
            this.presenter = new UserEditEmail_Presenter();
            viewModel.storePresenter(this.presenter);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        presenter.linkView(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        presenter.linkView(this);

//        if (presenter.hasItem())
//            presenter.onConfigChanged();
//        else
//            presenter.onFirstOpen(getIntent());
    }

    @Override
    protected void onStop() {
        super.onStop();
        presenter.unlinkView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                return presenter.onHomePressed();

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        presenter.onBackPressed();
    }


    // BaseView
    @Override
    public void onUserLogin() {

    }

    @Override
    public void onUserLogout() {

    }


    // iUserEditEmail.iView
    @Override
    public void displayCurrentEmail(User user) {

    }


    // Нажатия
    @OnClick(R.id.saveButton)
    void onSaveButtonClicked() {
        presenter.onSaveButtonClicked();
    }

    @OnClick(R.id.cancelButton)
    void onCancelButtonClicked() {
        presenter.onCancelButtonClicked();
    }
}
