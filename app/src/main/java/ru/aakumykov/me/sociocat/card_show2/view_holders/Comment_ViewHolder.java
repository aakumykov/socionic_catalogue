package ru.aakumykov.me.sociocat.card_show2.view_holders;

import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.Glide;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.card_show2.iCardShow2;
import ru.aakumykov.me.sociocat.card_show2.list_items.iList_Item;
import ru.aakumykov.me.sociocat.models.Comment;
import ru.aakumykov.me.sociocat.singletons.iCommentsSingleton;
import ru.aakumykov.me.sociocat.utils.MyUtils;

public class Comment_ViewHolder extends Base_ViewHolder implements
        iComment_ViewHolder
{
    @BindView(R.id.commentRow) ConstraintLayout commentRow;

    @BindView(R.id.imageView) ImageView userAvatarView;
    @BindView(R.id.userNameView) TextView userNameView;

    @BindView(R.id.cTimeView) TextView cTimeView;
    @BindView(R.id.mTimeView) TextView mTimeView;

    @BindView(R.id.quoteView) TextView quoteView;
    @BindView(R.id.commentTextView) TextView commentTextView;

    @BindView(R.id.rateUpWidget) ImageView rateUpWidget;
    @BindView(R.id.rateDownWidget) ImageView rateDownWidget;
    @BindView(R.id.ratingView) TextView ratingView;
    @BindView(R.id.ratingThrobber) ProgressBar ratingThrobber;

    @BindView(R.id.replyWidget) TextView replyWidget;
    @BindView(R.id.editWidget) TextView editWidget;
    @BindView(R.id.deleteWidget) TextView deleteWidget;

    private final iCardShow2.iPresenter presenter;
    private iList_Item currentListItem;


    public Comment_ViewHolder(@NonNull View itemView, iCardShow2.iPresenter presenter) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        this.presenter = presenter;
    }

    @Override
    public void initialize(iList_Item listItem) {
        this.currentListItem = listItem;
        Comment comment = (Comment) listItem.getPayload();
        unfadeBackground();
        displayComment(comment);
    }


    // Нажатия
    @OnClick(R.id.editWidget)
    void onEditCommentClicked() {
        presenter.onEditCommentClicked(currentListItem);
    }

    @OnClick(R.id.replyWidget)
    void onReplyToCommentClicked() {
        presenter.onReplyClicked(this.currentListItem);
    }

    @OnClick(R.id.deleteWidget)
    void onDeleteCommentClicked() {
        presenter.onDeleteCommentClicked(this.currentListItem, this);
    }

    @OnClick(R.id.rateUpWidget)
    void onCommentRateUpClicked() {
        presenter.onCommentRateUpClicked(this, currentListItem);
    }

    @OnClick(R.id.rateDownWidget)
    void onCommentRateDownClicked() {
        presenter.onCommentRateDownClicked(this, currentListItem);
    }



    // iCommentViewHolder
    @Override
    public void fadeBackground() {
        commentRow.setBackgroundResource(R.drawable.shape_comment_background_faded);
        MyUtils.disable(replyWidget);
        MyUtils.disable(editWidget);
        MyUtils.disable(deleteWidget);
    }

    @Override
    public void unfadeBackground() {
        commentRow.setBackgroundResource(R.drawable.shape_comment_background);
        MyUtils.enable(replyWidget);
        MyUtils.enable(editWidget);
        MyUtils.enable(deleteWidget);
    }

    @Override
    public void disableRatingControls() {
        MyUtils.disable(rateUpWidget);
        MyUtils.disable(rateDownWidget);

        MyUtils.hide(ratingView);
        MyUtils.show(ratingThrobber);
    }

    @Override
    public void enablRatingControls() {
        MyUtils.enable(rateUpWidget);
        MyUtils.enable(rateDownWidget);

        MyUtils.hide(ratingThrobber);
        MyUtils.show(ratingView);
    }

    @Override
    public void setRating(int value) {
        ratingView.setText(String.valueOf(value));
    }

    @Override
    public void colorizeRatingWidget(@Nullable iCommentsSingleton.CommentRatingAction commentRatingAction) {
        if (null != commentRatingAction) {
            switch (commentRatingAction) {
                case UNRATE_UP:
                case UNRATE_DOWN:
                    showNotRated();
                    break;
                case RATE_UP:
                    showRatedUp();
                    break;
                case RATE_DOWN:
                    showRatedDown();
                    break;
                default:
                    break;
            }
        }
        else {
            showNotRated();
        }
    }

    // Внутренние методы
    private void displayComment(Comment comment) {

        // Текст комментария
        commentTextView.setText(comment.getText());

        // Текст родительского комментария
        String parentText = comment.getParentText();
        if (null != parentText) {
            quoteView.setText(parentText);
            MyUtils.show(quoteView);
        }
        else {
            MyUtils.hide(quoteView);
        }

        // Время создания, правки
        Long createdAt = comment.getCreatedAt();
        Long editedAt = comment.getEditedAt();

        if (null != editedAt && editedAt > 0L) {
            String editedAgoString = MyUtils.getHumanTimeAgo(commentTextView.getContext(), createdAt, R.string.COMMENT_edited_at);
            mTimeView.setText(editedAgoString);
            MyUtils.show(mTimeView);
            MyUtils.hide(cTimeView);
        }
        else if (null != createdAt && createdAt > 0L) {
            String createdAgoString = MyUtils.getHumanTimeAgo(commentTextView.getContext(), createdAt, R.string.COMMENT_created_at);
            cTimeView.setText(createdAgoString);
            MyUtils.show(cTimeView);
            MyUtils.hide(mTimeView);
        }
        else {
            String createdAgoString = MyUtils.getString(cTimeView.getContext(), R.string.COMMENT_unknown_create_time);
            cTimeView.setText(createdAgoString);
            MyUtils.show(cTimeView);
            MyUtils.hide(mTimeView);
        }

        // Имя автора
        userNameView.setText(comment.getUserName());

        // Изображение автора
        Glide.with(userAvatarView)
                .load(comment.getUserAvatarURL())
                .placeholder(R.drawable.ic_user)
                .into(userAvatarView);


        // Кнопки управления
        MyUtils.show(replyWidget);
        MyUtils.show(editWidget);
        MyUtils.show(deleteWidget);
    }

    private void showRatedUp() {
        rateUpWidget.setImageResource(R.drawable.ic_thumb_up_colored);
        rateDownWidget.setImageResource(R.drawable.ic_thumb_down_neutral);
    }

    private void showRatedDown() {
        rateUpWidget.setImageResource(R.drawable.ic_thumb_up_neutral);
        rateDownWidget.setImageResource(R.drawable.ic_thumb_down_colored);
    }

    private void showNotRated() {
        rateUpWidget.setImageResource(R.drawable.ic_thumb_up_neutral);
        rateDownWidget.setImageResource(R.drawable.ic_thumb_down_neutral);
    }

}
