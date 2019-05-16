package ru.aakumykov.me.sociocat.card_show.controllers;

import ru.aakumykov.me.sociocat.card_show.iCardShow_View;

public interface iController {

    void bindView(iCardShow_View view);
    void unbindView();

}
