package ru.aakumykov.me.sociocat.card_show.presenters;


import ru.aakumykov.me.sociocat.card_show.adapter.iListAdapter_Card;

public interface iCardPresenter extends iPresenter {
    void bindListAdapter(iListAdapter_Card listAdapter);
    void unbindListAdapter();

    void onWorkBegins();
    void onAddCommentClicked();
}
