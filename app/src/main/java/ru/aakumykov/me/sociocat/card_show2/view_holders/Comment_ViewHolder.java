package ru.aakumykov.me.sociocat.card_show2.view_holders;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.Glide;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.card_show2.iCardShow2;
import ru.aakumykov.me.sociocat.card_show2.list_items.iList_Item;
import ru.aakumykov.me.sociocat.models.Comment;
import ru.aakumykov.me.sociocat.utils.MyUtils;

public class Comment_ViewHolder extends Base_ViewHolder implements
        iCardShow2.iCommentViewHolder
{

    @BindView(R.id.commentRow) ConstraintLayout commentRow;

    @BindView(R.id.imageView) ImageView userAvatarView;
    @BindView(R.id.userNameView) TextView userNameView;

    @BindView(R.id.cTimeView) TextView cTimeView;
    @BindView(R.id.mTimeView) TextView mTimeView;

    @BindView(R.id.quoteView) TextView quoteView;
    @BindView(R.id.messageView) TextView messageView;

    @BindView(R.id.addCommentWidget) TextView replyWidget;
    @BindView(R.id.editWidget) TextView editWidget;
    @BindView(R.id.deleteWidget) TextView deleteWidget;

    private final iCardShow2.iPresenter presenter;
    private int position = -1;
    private Drawable initialBackground;


    public Comment_ViewHolder(@NonNull View itemView, iCardShow2.iPresenter presenter) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        this.presenter = presenter;
    }

    @Override
    public void initialize(iList_Item listItem, int position) {
        this.position = position;
        Comment comment = (Comment) listItem.getPayload();
        displayComment(comment);
    }


    // Нажатия
    @OnClick(R.id.deleteWidget)
    void onDeleteCommentClicked() {
        presenter.onDeleteCommentClicked(this.position, this);
    }


    // iCommentViewHolder
    @Override
    public void fadeBackground() {
        this.initialBackground = commentRow.getBackground();
        commentRow.setBackgroundResource(R.drawable.shape_comment_background_faded);
    }

    @Override
    public void unfadeBackground() {
        commentRow.setBackground(this.initialBackground);
    }


    // Внутренние методы
    private void displayComment(Comment comment) {

        // Текст комментария
        messageView.setText(comment.getText());

        // Текст родительского комментария
        String parentText = comment.getParentText();
        if (null != parentText) {
            quoteView.setText(parentText);
            MyUtils.show(quoteView);
        }

        // Время создания, правки
        Long createdAt = comment.getCreatedAt();
        Long editedAt = comment.getEditedAt();

        if (null != editedAt && editedAt > 0L) {
            String editedAgoString = MyUtils.getHumanTimeAgo(messageView.getContext(), createdAt, R.string.COMMENT_edited_at);
            mTimeView.setText(editedAgoString);
            MyUtils.show(mTimeView);
        }
        else if (null != createdAt && createdAt > 0L) {
            String createdAgoString = MyUtils.getHumanTimeAgo(messageView.getContext(), createdAt, R.string.COMMENT_created_at);
            cTimeView.setText(createdAgoString);
            MyUtils.show(cTimeView);
        }
        else {
            String createdAgoString = MyUtils.getString(cTimeView.getContext(), R.string.COMMENT_unknown_create_time);
            cTimeView.setText(createdAgoString);
            MyUtils.show(cTimeView);
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

}
