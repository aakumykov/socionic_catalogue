package ru.aakumykov.me.sociocat.template_active_view;

import android.os.Bundle;
import android.support.annotation.Nullable;

import butterknife.ButterKnife;
import ru.aakumykov.me.sociocat.BaseView;
import ru.aakumykov.me.sociocat.R;

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


    // Обязательные методы
    @Override
    public void onUserLogin() {

    }

    @Override
    public void onUserLogout() {

    }


    // Интерфейсные методы
    @Override
    public void display() {

    }

}
