package ru.aakumykov.me.sociocat.card_show.presenters;

import androidx.annotation.Nullable;

import java.util.List;

import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.card_show.adapter.iComments_ViewAdapter;
import ru.aakumykov.me.sociocat.card_show.view_holders.iComment_ViewHolder;
import ru.aakumykov.me.sociocat.models.Comment;
import ru.aakumykov.me.sociocat.singletons.CommentsSingleton;
import ru.aakumykov.me.sociocat.singletons.iCommentsSingleton;

public class CommentsPresenter implements iCommentsPresenter{

    private iComments_ViewAdapter viewAdapter;
    private iCommentsSingleton commentsSingleton = CommentsSingleton.getInstance();

    @Override
    public void bindListAdapter(iComments_ViewAdapter listAdapter) {
        this.viewAdapter = listAdapter;
    }

    @Override
    public void unbindListAdapter() {
        this.viewAdapter = null;
    }

    @Override
    public void onWorkBegins(@Nullable String cardKey, @Nullable String commentKey) {

        viewAdapter.showCommentsThrobber();

        commentsSingleton.loadList(cardKey, new iCommentsSingleton.ListCallbacks() {
            @Override
            public void onCommentsLoadSuccess(List<Comment> list) {
                viewAdapter.hideCommentsThrobber();
                viewAdapter.setList(list);

                if (null != commentKey)
                    viewAdapter.scrollToComment(commentKey);
            }

            @Override
            public void onCommentsLoadError(String errorMessage) {
                viewAdapter.hideCommentsThrobber();
                viewAdapter.showCommentsError(R.string.COMMENTS_error_loading_comments, errorMessage);
            }
        });
    }

    @Override
    public void onReplyToCommentClicked(iComment_ViewHolder viewHolder, String text) {

    }
}
