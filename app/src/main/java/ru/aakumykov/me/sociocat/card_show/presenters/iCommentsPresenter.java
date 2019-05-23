package ru.aakumykov.me.sociocat.card_show.presenters;

import androidx.annotation.Nullable;

import ru.aakumykov.me.sociocat.card_show.adapter.iListAdapter_Comments;
import ru.aakumykov.me.sociocat.card_show.iCommentFormView;
import ru.aakumykov.me.sociocat.card_show.list_items.ListItem;
import ru.aakumykov.me.sociocat.card_show.view_holders.iComment_ViewHolder;
import ru.aakumykov.me.sociocat.models.Comment;

public interface iCommentsPresenter extends iPresenter {

    void bindViewAdapter(iListAdapter_Comments viewAdapter);
    void unbindViewAdapter();

    void bindReplyView(iCommentFormView replyView);
    void unbindReplyView();


    void onWorkBegins(@Nullable String cardKey, @Nullable String commentKey);

    void onReplyToCommentClicked(iComment_ViewHolder commentViewHolder, Comment comment);

    void sendCommentClicked(String text, ListItem repliedItem, ru.aakumykov.me.sociocat.card_show.comment_form.iCommentForm commentForm);
}
