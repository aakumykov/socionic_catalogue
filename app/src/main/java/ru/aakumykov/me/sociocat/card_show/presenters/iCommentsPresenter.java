package ru.aakumykov.me.sociocat.card_show.presenters;

import androidx.annotation.Nullable;

import ru.aakumykov.me.sociocat.card_show.adapter.iListAdapter_Comments;
import ru.aakumykov.me.sociocat.card_show.list_items.ListItem;
import ru.aakumykov.me.sociocat.card_show.view_holders.iComment_ViewHolder;
import ru.aakumykov.me.sociocat.models.Comment;

public interface iCommentsPresenter {

    void bindListAdapter(iListAdapter_Comments listAdapter);
    void unbindListAdapter();

    void onWorkBegins(@Nullable String cardKey, @Nullable String commentKey);

    void onReplyToCommentClicked(iComment_ViewHolder commentViewHolder, Comment comment);

    void onSendCommentClicked(String text, ListItem repliedItem, ru.aakumykov.me.sociocat.card_show.comment_form.iCommentForm commentForm);
}
