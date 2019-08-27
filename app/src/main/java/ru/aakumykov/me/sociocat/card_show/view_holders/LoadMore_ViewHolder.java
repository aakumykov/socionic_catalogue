package ru.aakumykov.me.sociocat.card_show.view_holders;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.card_show.iCardShow;
import ru.aakumykov.me.sociocat.card_show.list_items.LoadMore_Item;
import ru.aakumykov.me.sociocat.models.Comment;

public class LoadMore_ViewHolder extends Base_ViewHolder
{
    private final static String TAG = "LoadMore_ViewHolder";
    private iCardShow.iCommentsPresenter commentsPresenter;
    private Comment startAtComment;
    private int position;

    @BindView(R.id.loadMoreTextView) TextView textView;


    public LoadMore_ViewHolder(View itemView, iCardShow.iCommentsPresenter commentsPresenter) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        this.commentsPresenter = commentsPresenter;
    }

    public void initialize(@Nullable LoadMore_Item loadMoreItem, int position) {

        this.position = position;

        if (null != loadMoreItem) {
            startAtComment = loadMoreItem.getLastVisibleComment();
            textView.setText(R.string.COMMENTS_load_more_comments);
        }
        else {
            textView.setText(R.string.COMMENTS_there_is_no_comments_yet);
        }
    }

    @OnClick(R.id.loadMoreTextView)
    void loadMoreClicked() {
        commentsPresenter.onLoadMoreClicked(position, startAtComment);
    }
}

