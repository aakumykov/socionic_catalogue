package ru.aakumykov.me.sociocat.card_show.presenters;

import androidx.annotation.Nullable;

import ru.aakumykov.me.sociocat.card_show.adapter.iComments_ViewAdapter;
import ru.aakumykov.me.sociocat.card_show.iReplyView;
import ru.aakumykov.me.sociocat.card_show.view_holders.iComment_ViewHolder;
import ru.aakumykov.me.sociocat.models.Comment;

public interface iCommentsPresenter extends iPresenter {

    void bindViewAdapter(iComments_ViewAdapter viewAdapter);
    void unbindViewAdapter();

    void bindReplyView(iReplyView replyView);
    void unbindReplyView();

    void onWorkBegins(@Nullable String cardKey, @Nullable String commentKey);

    void onReplyToCommentClicked(iComment_ViewHolder commentViewHolder, Comment comment);
}
