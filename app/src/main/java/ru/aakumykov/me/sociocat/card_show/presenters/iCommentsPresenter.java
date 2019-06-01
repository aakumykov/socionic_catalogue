package ru.aakumykov.me.sociocat.card_show.presenters;

import androidx.annotation.Nullable;

import ru.aakumykov.me.sociocat.card_show.adapter.iCommentsView;
import ru.aakumykov.me.sociocat.card_show.iPageView;
import ru.aakumykov.me.sociocat.card_show.list_items.ListItem;
import ru.aakumykov.me.sociocat.models.Comment;

public interface iCommentsPresenter {

    void bindPageView(iPageView pageView);
    void unbindPageView();

    void bindCommentsView(iCommentsView listAdapter);
    void unbindCommentsView();

    void onWorkBegins(String cardKey, @Nullable String scrollToCommentKey);

    void onLoadMoreClicked(String cardKey, @Nullable String lastVisibleCommentKey);

    void onReplyToCommentClicked(String commentKey);

    void onSendCommentClicked(String text, ListItem repliedItem, ru.aakumykov.me.sociocat.utils.comment_form.iCommentForm commentForm);

    void onEditCommentClicked(Comment comment);
}
