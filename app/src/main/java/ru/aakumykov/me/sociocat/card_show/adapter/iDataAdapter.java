package ru.aakumykov.me.sociocat.card_show.adapter;

import ru.aakumykov.me.sociocat.card_show.iCardShow;
import ru.aakumykov.me.sociocat.card_show.iCardShow_View;
import ru.aakumykov.me.sociocat.card_show.iPageView;

public interface iDataAdapter {

    void bindPresenters(iCardShow.iCardPresenter cardPresenter, iCardShow.iCommentsPresenter commentPresenter);
    void unbindPresenters();

    void bindView(iPageView pageView, iCardShow_View cardShowView);
    void unbindView();
}
