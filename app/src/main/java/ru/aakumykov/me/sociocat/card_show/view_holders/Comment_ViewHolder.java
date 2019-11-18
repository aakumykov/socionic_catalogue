package ru.aakumykov.me.sociocat.card_show.view_holders;

import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.Glide;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.card_show.iCardShow;
import ru.aakumykov.me.sociocat.models.Comment;
import ru.aakumykov.me.sociocat.singletons.AuthSingleton;
import ru.aakumykov.me.sociocat.singletons.UsersSingleton;
import ru.aakumykov.me.sociocat.utils.MyUtils;

public class Comment_ViewHolder  extends Base_ViewHolder implements
        View.OnLongClickListener,
        PopupMenu.OnMenuItemClickListener
{
    @BindView(R.id.commentRow) ConstraintLayout commentRow;
    @BindView(R.id.imageView) ImageView userAvatarView;
    @BindView(R.id.userNameView) TextView userNameView;
    @BindView(R.id.cTimeView) TextView cTimeView;
    @BindView(R.id.mTimeView) TextView mTimeView;
    @BindView(R.id.quoteView) TextView quoteView;
    @BindView(R.id.messageView) TextView textView;
    @BindView(R.id.replyWidget) TextView replyWidget;
    @BindView(R.id.editWidget) TextView editWidget;
    @BindView(R.id.deleteWidget) TextView deleteWidget;

    private final static String TAG = "Comment_ViewHolder";
    private iCardShow.iCommentsPresenter commentsPresenter;
    private Drawable originalBackground = null;
    private Comment currentComment;


    public Comment_ViewHolder(View itemView, iCardShow.iCommentsPresenter commentsPresenter) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        this.commentsPresenter = commentsPresenter;
    }

    public void initialize(Comment comment) {
        currentComment = comment;

        MyUtils.hide(quoteView);

        String parentText = comment.getParentText();
        if (!TextUtils.isEmpty(parentText)) {
            quoteView.setText(parentText);
            MyUtils.show(quoteView);
        }

        userNameView.setText(comment.getUserName());

        String avatarURL = comment.getUserAvatarURL();
        if (!TextUtils.isEmpty(avatarURL)) {
            Glide.with(userAvatarView.getContext())
                    .load(avatarURL)
                    .placeholder(R.drawable.ic_image_placeholder)
                    .error(R.drawable.ic_image_error)
                    .into(userAvatarView);
        }

        // Время создания, правки
        Long createdAt = comment.getCreatedAt();
        Long editedAt = comment.getEditedAt();

        if (null != editedAt && editedAt > 0L) {
            String editedAgoString = MyUtils.getHumanTimeAgo(textView.getContext(), createdAt, R.string.COMMENT_edited_at);
            mTimeView.setText(editedAgoString);
            MyUtils.show(mTimeView);
        }
        else if (null != createdAt && createdAt > 0L) {
            String createdAgoString = MyUtils.getHumanTimeAgo(textView.getContext(), createdAt, R.string.COMMENT_created_at);
            cTimeView.setText(createdAgoString);
            MyUtils.show(cTimeView);
        }
        else {
            String createdAgoString = MyUtils.getString(cTimeView.getContext(), R.string.COMMENT_unknown_create_time);
            cTimeView.setText(createdAgoString);
            MyUtils.show(cTimeView);
        }

        textView.setText(comment.getText());

        String userId = AuthSingleton.currentUserId();
        String authorId = currentComment.getUserId();
        boolean isAdmin = UsersSingleton.getInstance().currentUserIsAdmin();

        if (null != userId) {
            MyUtils.show(replyWidget);

            if (userId.equals(authorId) || isAdmin) {
                MyUtils.show(editWidget);
                MyUtils.show(deleteWidget);
            }
        }

        commentRow.setOnLongClickListener(this);
    }

    // Нажатия
    @OnClick(R.id.replyWidget)
    void onReplyClicked() {
        commentsPresenter.onReplyClicked(currentComment);
    }

    @OnClick(R.id.editWidget)
    void onEditClicked() {
        commentsPresenter.onEditCommentClicked(currentComment);
    }

    @OnClick(R.id.deleteWidget)
    void onDeleteClicked() {
        commentsPresenter.onDeleteCommentClicked(currentComment);
    }

    @Override
    public boolean onLongClick(View v) {
        showPopupMenu(v, currentComment);
        return true;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.actionEdit:
                commentsPresenter.onEditCommentClicked(currentComment);
                break;
            case R.id.actionDelete:
                commentsPresenter.onDeleteCommentClicked(currentComment);
                break;
            default:
                break;
        }
        return true;
    }


    // Внутренние методы
    private void showPopupMenu(View view, Comment comment) {

        PopupMenu popupMenu = new PopupMenu(view.getContext(), view);

        popupMenu.inflate(R.menu.edit);
        popupMenu.inflate(R.menu.delete);

        setBackgroundPressed();

        popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {
            @Override
            public void onDismiss(PopupMenu menu) {
                restoreOriginalBackground();
            }
        });

        popupMenu.setOnMenuItemClickListener(this);

        popupMenu.show();
    }

    private void setBackgroundPressed() {
        saveOriginalBackground();

        int color = commentRow.getResources().getColor(R.color.comment_pressed_background_color);
        commentRow.setBackgroundColor(color);
    }

    private void restoreOriginalBackground() {
        if (null != originalBackground) {
            commentRow.setBackground(originalBackground);
        }
    }

    private void saveOriginalBackground() {
        originalBackground = commentRow.getBackground();
    }

}

