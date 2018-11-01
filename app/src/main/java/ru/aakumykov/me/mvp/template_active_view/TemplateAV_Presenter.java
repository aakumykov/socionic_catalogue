package ru.aakumykov.me.mvp.template_active_view;

import ru.aakumykov.me.mvp.cards_list_av.iCardsListAV;
import ru.aakumykov.me.mvp.interfaces.iAuthService;
import ru.aakumykov.me.mvp.interfaces.iCardsService;

public class TemplateAV_Presenter implements
        iTemplateAV.Presenter
{
    private final static String TAG = "TemplateAV_Presenter";
    private iTemplateAV.View view;
    private iCardsService model;
    private iAuthService authService;


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
    public void linkModel(iCardsService model) {
        this.model = model;
    }
    @Override
    public void unlinkModel() {
        this.model = null;
    }

    @Override
    public void linkAuth(iAuthService authService) {
        this.authService = authService;
    }
    @Override
    public void unlinkAuth() {
        this.authService = null;
    }

}
