package ru.aakumykov.me.sociocat.card_show.presenters;

import ru.aakumykov.me.sociocat.card_show.adapter.iListAdapter_Comments;
import ru.aakumykov.me.sociocat.card_show.view_holders.iComment_ViewHolder;

public interface iCommentsPresenter extends iPresenter {
    void bindListAdapter(iListAdapter_Comments listAdapter);
    void unbindListAdapter();

    void onWorkBegins();
    void onReplyToCommentClicked(iComment_ViewHolder viewHolder, String text);
}
