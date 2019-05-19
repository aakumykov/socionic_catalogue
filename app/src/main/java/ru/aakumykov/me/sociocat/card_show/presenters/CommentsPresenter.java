package ru.aakumykov.me.sociocat.card_show.presenters;

import ru.aakumykov.me.sociocat.card_show.adapter.iListAdapter;
import ru.aakumykov.me.sociocat.card_show.adapter.iListAdapter_Comments;
import ru.aakumykov.me.sociocat.card_show.view_holders.iComment_ViewHolder;

public class CommentsPresenter implements iCommentsPresenter{

    private iListAdapter_Comments listAdapter;

    @Override
    public void bindListAdapter(iListAdapter_Comments listAdapter) {
        this.listAdapter = (iListAdapter_Comments) listAdapter;
    }

    @Override
    public void unbindListAdapter() {
        this.listAdapter = null;
    }

    @Override
    public void onWorkBegins() {

    }

    @Override
    public void onReplyToCommentClicked(iComment_ViewHolder viewHolder, String text) {

    }
}
