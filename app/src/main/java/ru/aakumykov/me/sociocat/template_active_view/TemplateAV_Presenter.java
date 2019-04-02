package ru.aakumykov.me.sociocat.template_active_view;

import ru.aakumykov.me.sociocat.interfaces.iAuthSingleton;
import ru.aakumykov.me.sociocat.interfaces.iCardsSingleton;

public class TemplateAV_Presenter implements
        iTemplateAV.Presenter
{
    private final static String TAG = "Register_Presenter";
    private iTemplateAV.View view;
    private iCardsSingleton model;
    private iAuthSingleton authSingleton;


    // Интерфейсные методы
    @Override
    public void load() {

    }


    // Системные методы
    @Override
    public void linkView(iTemplateAV.View view) {
        this.view = view;
    }
    @Override
    public void unlinkView() {
        this.view = null;
    }

    @Override
    public void linkCardsService(iCardsSingleton model) {
        this.model = model;
    }
    @Override
    public void unlinkCardsService() {
        this.model = null;
    }

    @Override
    public void linkAuth(iAuthSingleton authSingleton) {
        this.authSingleton = authSingleton;
    }
    @Override
    public void unlinkAuthService() {
        this.authSingleton = null;
    }

}
