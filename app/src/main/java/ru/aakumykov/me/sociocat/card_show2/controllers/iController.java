package ru.aakumykov.me.sociocat.card_show2.controllers;

import ru.aakumykov.me.sociocat.card_show2.iCardShow2_View;

public interface iController {

    void bindView(iCardShow2_View view);
    void unbindView();

}
