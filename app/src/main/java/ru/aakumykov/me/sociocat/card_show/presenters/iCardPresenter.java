package ru.aakumykov.me.sociocat.card_show.presenters;

import androidx.annotation.Nullable;

import ru.aakumykov.me.sociocat.card_show.adapter.iCardAdapter;
import ru.aakumykov.me.sociocat.card_show.iPageView;
import ru.aakumykov.me.sociocat.models.Card;

public interface iCardPresenter {

    void bindPageView(iPageView pageView);
    void unbindPageView();

    void bindListAdapter(iCardAdapter listAdapter);
    void unbindListAdapter();

    void onWorkBegins(@Nullable String cardKey, @Nullable String commentKey);
    void onErrorOccurs();

    void onReplyClicked();

    Card getCard();
}
