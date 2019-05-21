package ru.aakumykov.me.sociocat.card_show.presenters;


import androidx.annotation.Nullable;

import ru.aakumykov.me.sociocat.card_show.adapter.iCard_ViewAdapter;
import ru.aakumykov.me.sociocat.card_show.iReplyView;

public interface iCardPresenter extends iPresenter {

    void bindReplyView(iReplyView replyView);
    void unbindReplyView();

    void bindViewAdapter(iCard_ViewAdapter listAdapter);
    void unbindViewAdapter();

    void onWorkBegins(@Nullable String cardKey, @Nullable String commentKey);
    void onErrorOccurs();

    void onAddCommentClicked();
}
