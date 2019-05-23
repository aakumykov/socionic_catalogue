package ru.aakumykov.me.sociocat.card_show.presenters;

import androidx.annotation.Nullable;

import ru.aakumykov.me.sociocat.card_show.adapter.iListAdapter_Card;

public interface iCardPresenter {

    void bindListAdapter(iListAdapter_Card listAdapter);
    void unbindListAdapter();

    void onWorkBegins(@Nullable String cardKey, @Nullable String commentKey);
    void onErrorOccurs();

    void onReplyClicked();
}
