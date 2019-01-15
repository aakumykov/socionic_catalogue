package ru.aakumykov.me.mvp.register_confirmation;

import android.os.Bundle;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.aakumykov.me.mvp.BaseView;
import ru.aakumykov.me.mvp.R;

public class RegisterConfirmation_View extends BaseView implements
        iRegisterConfirmation.View
{
    @BindView(R.id.textView) TextView textView;
    private iRegisterConfirmation.Presenter presenter;


    // Системные методы
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_confirmation_activity);
        ButterKnife.bind(this);

        setPageTitle(R.string.REGISTER2_email_confirmation);
        activateUpButton();

        presenter = new RegisterConfirmation_Presenter();
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


    // Интерфейсные методы
    @Override
    public void showNeedsConfirmationMessage() {
        textView.setText(R.string.REGISTER2_check_your_email);
    }

    @Override
    public void showConfirmationSuccessMessage() {
        textView.setText(R.string.REGISTER2_registration_confirm_success);
    }

    @Override
    public void showConfirmationErrorMessage() {
        textView.setText(R.string.REGISTER2_registration_confirm_error);
    }
}
