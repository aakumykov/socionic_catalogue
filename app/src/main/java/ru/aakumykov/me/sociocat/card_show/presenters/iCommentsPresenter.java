package ru.aakumykov.me.sociocat.card_show.presenters;

import androidx.annotation.Nullable;

import ru.aakumykov.me.sociocat.card_show.adapter.iCommentsView;
import ru.aakumykov.me.sociocat.card_show.iPageView;
import ru.aakumykov.me.sociocat.card_show.list_items.ListItem;

public interface iCommentsPresenter {

    void bindPageView(iPageView pageView);
    void unbindPageView();

    void bindCommentsView(iCommentsView listAdapter);
    void unbindCommentsView();

    void onWorkBegins(@Nullable String cardKey, @Nullable String commentKey);

    void onReplyToCommentClicked(String commentKey);

    void onSendCommentClicked(String text, ListItem repliedItem, ru.aakumykov.me.sociocat.utils.comment_form.iCommentForm commentForm);
}
