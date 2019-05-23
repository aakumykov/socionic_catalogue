package ru.aakumykov.me.sociocat.card_show.presenters;


import androidx.annotation.Nullable;

import ru.aakumykov.me.sociocat.card_show.adapter.iListAdapter_Card;
import ru.aakumykov.me.sociocat.card_show.iCommentFormView;

public interface iCardPresenter extends iPresenter {

    void bindReplyView(iCommentFormView replyView);
    void unbindReplyView();

    void bindViewAdapter(iListAdapter_Card listAdapter);
    void unbindViewAdapter();

    void onWorkBegins(@Nullable String cardKey, @Nullable String commentKey);
    void onErrorOccurs();

    void replyClicked();
}
