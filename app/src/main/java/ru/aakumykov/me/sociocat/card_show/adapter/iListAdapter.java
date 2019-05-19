package ru.aakumykov.me.sociocat.card_show.adapter;

import ru.aakumykov.me.sociocat.card_show.presenters.iCardPresenter;
import ru.aakumykov.me.sociocat.card_show.presenters.iCommentsPresenter;

public interface iListAdapter {
    void bindPresenters(iCardPresenter cardPresenter, iCommentsPresenter commentPresenter);
    void unbindPresenters();
}
