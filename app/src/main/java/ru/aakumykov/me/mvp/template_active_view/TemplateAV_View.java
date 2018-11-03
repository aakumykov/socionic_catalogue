package ru.aakumykov.me.mvp.template_active_view;

import android.os.Bundle;
import android.support.annotation.Nullable;

import butterknife.ButterKnife;
import ru.aakumykov.me.mvp.BaseView;
import ru.aakumykov.me.mvp.R;

public class TemplateAV_View  extends BaseView implements
        iTemplateAV.View
{
    private final static String TAG = "Register_View";
    private iTemplateAV.Presenter presenter;


    // Системные методы
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.template_activity);
        ButterKnife.bind(this);

        setPageTitle("TemplateAV_View");

        presenter = new TemplateAV_Presenter();
    }

    @Override
    public void onServiceBounded() {
        presenter.linkView(this);
        presenter.linkCardsService(getCardsService());
        presenter.linkAuth(getAuthService());

        presenter.load();
    }

    @Override
    public void onServiceUnbounded() {
        presenter.unlinkView();
        presenter.unlinkCardsService();
        presenter.unlinkAuthService();
    }

    @Override
    public void processLogin() {

    }

    @Override
    public void processLogout() {

    }


    // Интерфейсные методы
    @Override
    public void display() {

    }

}
