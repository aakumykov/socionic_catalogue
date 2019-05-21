package ru.aakumykov.me.sociocat.card_show.presenters;

import androidx.annotation.Nullable;

import java.util.List;

import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.card_show.adapter.iListAdapter_Comments;
import ru.aakumykov.me.sociocat.card_show.iReplyView;
import ru.aakumykov.me.sociocat.card_show.view_holders.iComment_ViewHolder;
import ru.aakumykov.me.sociocat.models.Comment;
import ru.aakumykov.me.sociocat.singletons.CommentsSingleton;
import ru.aakumykov.me.sociocat.singletons.iCommentsSingleton;

public class CommentsPresenter implements iCommentsPresenter{

    private iListAdapter_Comments viewAdapter;
    private iReplyView replyView;
    private iCommentsSingleton commentsSingleton = CommentsSingleton.getInstance();

    @Override
    public void bindViewAdapter(iListAdapter_Comments viewAdapter) {
        this.viewAdapter = viewAdapter;
    }

    @Override
    public void unbindViewAdapter() {
        this.viewAdapter = null;
    }

    @Override
    public void bindReplyView(iReplyView replyView) {
        this.replyView = replyView;
    }

    @Override
    public void unbindReplyView() {
        this.replyView = null;
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
    public void onReplyToCommentClicked(iComment_ViewHolder commentViewHolder, Comment comment) {
        replyView.showCommentForm(comment);
    }

    @Override
    public void onSendComment(String text, @Nullable Comment parentComment) {



    }
}
