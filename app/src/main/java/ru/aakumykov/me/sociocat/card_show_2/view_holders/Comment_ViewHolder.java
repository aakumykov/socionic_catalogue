package ru.aakumykov.me.sociocat.card_show_2.view_holders;

import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.card_show_2.iCommentsController;
import ru.aakumykov.me.sociocat.models.Comment;


public class Comment_ViewHolder extends Base_ViewHolder {

    @BindView(R.id.commentRow) LinearLayout commentRow;
    @BindView(R.id.textView) TextView textView;
    @BindView(R.id.replyWidget) TextView replyWidget;

    private final static String TAG = "Comment_ViewHolder";
    private iCommentsController commentsController;
    private Drawable originalBackground = null;


    public Comment_ViewHolder(View itemView) {
        super(itemView);
        Log.d(TAG, "new Comment_ViewHolder()");
        ButterKnife.bind(this, itemView);
    }


    public void initialize(Comment comment, iCommentsController commentsController) {
        Log.d(TAG, "Comment_ViewHolder.initialize("+comment.getText()+")");

        this.commentsController = commentsController;

        textView.setText(comment.getText());

        commentRow.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showPopupMenu(v, comment);
                return true;
            }
        });

        replyWidget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startReplyToComment(comment);
            }
        });
    }

    private void showPopupMenu(View view, Comment comment) {

        PopupMenu popupMenu = new PopupMenu(view.getContext(), view);

        popupMenu.inflate(R.menu.edit);
        popupMenu.inflate(R.menu.share);

        setBackgroundPressed();

        popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {
            @Override
            public void onDismiss(PopupMenu menu) {
                restoreOriginalBackground();
            }
        });

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.actionEdit:
                        commentsController.editComment(comment);
                        break;
                    case R.id.actionShare:
                        break;
                    default:
                        break;
                }

                return true;
            }
        });

        popupMenu.show();
    }

    private void startReplyToComment(Comment comment) {
        commentsController.showCommentForm(comment);
    }


    public void setBackgroundPressed() {
        saveOriginalBackground();

        int color = commentRow.getResources().getColor(R.color.comment_pressed_color);
        commentRow.setBackgroundColor(color);
    }

    public void restoreOriginalBackground() {
        if (null != originalBackground) {
            commentRow.setBackground(originalBackground);
        }
    }

    private void saveOriginalBackground() {
        originalBackground = commentRow.getBackground();
    }

}

