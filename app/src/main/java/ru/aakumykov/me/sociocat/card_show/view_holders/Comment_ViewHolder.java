package ru.aakumykov.me.sociocat.card_show.view_holders;

import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.card_show.presenters.iCommentsPresenter;
import ru.aakumykov.me.sociocat.models.Comment;
import ru.aakumykov.me.sociocat.utils.MyUtils;

public class Comment_ViewHolder  extends Base_ViewHolder implements
        View.OnLongClickListener,
        PopupMenu.OnMenuItemClickListener
{
    @BindView(R.id.commentRow) LinearLayout commentRow;
    @BindView(R.id.quoteView) TextView quoteView;
    @BindView(R.id.textView) TextView textView;
    @BindView(R.id.replyWidget) TextView replyWidget;

    private final static String TAG = "Comment_ViewHolder";
    private iCommentsPresenter commentsPresenter;
    private Drawable originalBackground = null;
    private Comment currentComment;


    public Comment_ViewHolder(View itemView, iCommentsPresenter commentsPresenter) {
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

        textView.setText(comment.getText());

        commentRow.setOnLongClickListener(this);
    }


    // Нажатия
    @OnClick(R.id.replyWidget)
    void openCommentForm() {
        commentsPresenter.onReplyClicked(currentComment);
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
            default:
                break;
        }
        return true;
    }


    // Внутренние методы
    private void showPopupMenu(View view, Comment comment) {

        PopupMenu popupMenu = new PopupMenu(view.getContext(), view);

        popupMenu.inflate(R.menu.edit);

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

