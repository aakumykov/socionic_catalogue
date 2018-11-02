package ru.aakumykov.me.mvp.login;

import android.os.Bundle;
import android.support.annotation.Nullable;

import butterknife.ButterKnife;
import ru.aakumykov.me.mvp.BaseView;
import ru.aakumykov.me.mvp.R;

public class Login_View extends BaseView implements
        iLogin.View
{
    private final static String TAG = "Login_View";
    private iLogin.Presenter presenter;


    // Интерфейсные методы
    @Override
    public void disableForm() {

    }

    @Override
    public void enableForm() {

    }


    // Системные методы
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        ButterKnife.bind(this);

        setPageTitle("Login_View");

        presenter = new Login_Presenter();
    }

    @Override
    public void onServiceBounded() {
        presenter.linkView(this);
        presenter.linkModel(getCardsService());
        presenter.linkAuth(getAuthService());
    }

    @Override
    public void onServiceUnbounded() {
        presenter.unlinkView();
        presenter.unlinkModel();
        presenter.unlinkAuth();
    }

}
