package ru.aakumykov.me.mvp;

import ru.aakumykov.me.mvp.interfaces.iAuthSingleton;

public interface iBaseView extends iBaseMethods {

    void setPageTitle(int titleId);
    void setPageTitle(String title);
    void activateUpButton();
    void closePage();
}
