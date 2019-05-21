package ru.aakumykov.me.sociocat.card_show.presenters;

import androidx.annotation.Nullable;

import java.util.List;

import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.card_show.adapter.iListAdapter_Comments;
import ru.aakumykov.me.sociocat.card_show.view_holders.iComment_ViewHolder;
import ru.aakumykov.me.sociocat.models.Comment;
import ru.aakumykov.me.sociocat.singletons.CommentsSingleton;
import ru.aakumykov.me.sociocat.singletons.iCommentsSingleton;

public class CommentsPresenter implements iCommentsPresenter{

    private iListAdapter_Comments listAdapter;
    private iCommentsSingleton commentsSingleton = CommentsSingleton.getInstance();

    @Override
    public void bindListAdapter(iListAdapter_Comments listAdapter) {
        this.listAdapter = listAdapter;
    }

    @Override
    public void unbindListAdapter() {
        this.listAdapter = null;
    }

    @Override
    public void onWorkBegins(@Nullable String cardKey, @Nullable String commentKey) {

        listAdapter.showCommentsThrobber();

        commentsSingleton.loadList(cardKey, new iCommentsSingleton.ListCallbacks() {
            @Override
            public void onCommentsLoadSuccess(List<Comment> list) {
                listAdapter.hideCommentsThrobber();
                listAdapter.setList(list);
            }

            @Override
            public void onCommentsLoadError(String errorMessage) {
                listAdapter.hideCommentsThrobber();
                listAdapter.showCommentsError(R.string.COMMENTS_error_loading_comments, errorMessage);
            }
        });
    }

    @Override
    public void onReplyToCommentClicked(iComment_ViewHolder viewHolder, String text) {

    }
}
