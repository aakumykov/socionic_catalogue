package ru.aakumykov.me.sociocat.b_comments_list.view_holders;

import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.list_items.BasicMVPList_DataItem;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.list_items.BasicMVPList_ListItem;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.utils.DateUtils;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.utils.ViewUtils;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.view_holders.BasicMVPList_DataViewHolder;
import ru.aakumykov.me.sociocat.b_comments_list.interfaces.iCommentsList_ItemClickListener;
import ru.aakumykov.me.sociocat.models.Comment;

public class CommentViewHolder extends BasicMVPList_DataViewHolder {

    @BindView(R.id.elementView) View elementView;
    @BindView(R.id.commentTextView) TextView commentTextView;
    @BindView(R.id.commentAuthorView) TextView commentAuthorView;
    @BindView(R.id.commentDateView) TextView commentDateView;
    @BindView(R.id.cardTitleView) TextView cardTitleView;

    @Nullable @BindView(R.id.selectingOverlay) View selectingOverlay;
    @Nullable @BindView(R.id.highlightingOverlay) View highlightingOverlay;


    public CommentViewHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    @Override
    public void initialize(BasicMVPList_ListItem basicListItem) {
        BasicMVPList_DataItem dataItem = (BasicMVPList_DataItem) basicListItem;

        Comment comment = (Comment) dataItem.getPayload();

        displayComment(comment);

        displayIsChecked(dataItem.isSelected());
        displayIsHighlighted(dataItem.isHighLighted());
    }

    @Override
    public void displayIsChecked(boolean selected) {
        ViewUtils.setVisibility(selectingOverlay, selected);
    }

    @Override
    public void displayIsHighlighted(boolean isHighLighted) {
        if (null != highlightingOverlay) {
            if (isHighLighted) {
                highlightingOverlay.setBackgroundResource(R.drawable.simple_list_item_background_highlighted);
                ViewUtils.show(highlightingOverlay);
            }
            else {
                highlightingOverlay.setBackground(null);
                ViewUtils.hide(highlightingOverlay);
            }
        }
    }



    @OnClick(R.id.elementView)
    void onItemClicked() {
        mItemClickListener.onItemClicked(this);
    }

    @OnLongClick(R.id.elementView)
    void onItemLongClicked() {
        mItemClickListener.onItemLongClicked(this);
    }

    @OnClick(R.id.cardTitleView)
    void onCardTitleClicked() {
        ((iCommentsList_ItemClickListener) mItemClickListener).onCardTitleClicked(this);
    }

    @OnClick(R.id.commentAuthorView)
    void onCardAuthorClicked() {
        ((iCommentsList_ItemClickListener) mItemClickListener).onCardAuthorClicked(this);
    }


    private void displayComment(@NonNull Comment comment) {
        displayCommentText(comment);
        displayCardAuthor(comment);
        displayCommentDate(comment);
        displayCardTitle(comment);
    }

    private void displayCommentText(@NonNull Comment comment) {
        commentTextView.setText(comment.getName());
    }

    private void displayCardAuthor(@NonNull Comment comment) {
        String userName = comment.getUserName();

        if (TextUtils.isEmpty(userName))
            userName = ru.aakumykov.me.sociocat.a_basic_mvp_list_components
                    .utils.TextUtils
                    .getText(commentAuthorView.getContext(), R.string.unknown_user);

        commentAuthorView.setText(userName);
    }

    private void displayCommentDate(@NonNull Comment comment) {
        commentDateView.setText(DateUtils.long2date(comment.getDate()));
    }

    private void displayCardTitle(@NonNull Comment comment) {
        String cardTitleLabel = cardTitleView.getResources().getString(
                R.string.COMMENTS_LIST_card_title);

        String cardTitle = comment.getCardTitle();
        if (TextUtils.isEmpty(null))
            cardTitle = String.valueOf(comment.getCardId());

        cardTitle = cardTitleLabel + " <b>" + cardTitle + "</b>";

        cardTitleView.setText(Html.fromHtml(cardTitle));
    }
}
