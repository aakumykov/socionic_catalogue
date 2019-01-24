package ru.aakumykov.me.mvp.TEMPLATES.simple_page;

import android.os.Bundle;
import android.view.Menu;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.aakumykov.me.mvp.BaseView;
import ru.aakumykov.me.mvp.R;

public class SimplePage_View extends BaseView implements
        iSimplePage.View
{
    @BindView(R.id.textView) TextView textView;
    private iSimplePage.Presenter presenter;


    // Системные методы
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.register_confirmation_activity);
        ButterKnife.bind(this);

        setPageTitle(R.string.REGISTER_CONFIRMATION_page_title);
        activateUpButton();

        presenter = new SimplePage_Presenter();
    }

    @Override
    public void onUserLogin() {

    }

    @Override
    public void onUserLogout() {

    }

    @Override
    protected void onStart() {
        super.onStart();
        presenter.linkView(this);
        presenter.processInputIntent(getIntent());
    }

    @Override
    protected void onStop() {
        super.onStop();
        presenter.unlinkView();
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
//        return true;
    }


    // Интерфейсные методы
    @Override
    public void showNeedsConfirmationMessage() {
        textView.setText(R.string.REGISTER_CONFIRMATION_check_your_email);
    }

    @Override
    public void showConfirmationSuccessMessage() {
        textView.setText(R.string.REGISTER_CONFIRMATION_registration_confirm_success);
    }

    @Override
    public void showConfirmationErrorMessage() {
        textView.setText(R.string.REGISTER_CONFIRMATION_registration_confirm_error);
    }
}
