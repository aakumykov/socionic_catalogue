package ru.aakumykov.me.mvp.register;

import ru.aakumykov.me.mvp.interfaces.iAuthService;
import ru.aakumykov.me.mvp.interfaces.iCardsService;

public class Register_Presenter implements
        iRegister.Presenter
{
    private final static String TAG = "Register_Presenter";
    private iRegister.View view;
    private iCardsService model;
    private iAuthService authService;


    // Интерфейсные методы
    @Override
    public void load() {

    }


    // Системные методы
    @Override
    public void linkView(iRegister.View view) {
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
