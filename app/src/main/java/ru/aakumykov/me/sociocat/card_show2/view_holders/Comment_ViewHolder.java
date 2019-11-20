package ru.aakumykov.me.sociocat.card_show2.view_holders;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.card_show2.list_items.iList_Item;
import ru.aakumykov.me.sociocat.models.Comment;
import ru.aakumykov.me.sociocat.utils.MyUtils;

public class Comment_ViewHolder extends Base_ViewHolder {

//    @BindView(R.id.commentRow) ConstraintLayout commentRow;

    @BindView(R.id.imageView) ImageView userAvatarView;
    @BindView(R.id.userNameView) TextView userNameView;

    @BindView(R.id.cTimeView) TextView cTimeView;
    @BindView(R.id.mTimeView) TextView mTimeView;

    @BindView(R.id.quoteView) TextView quoteView;
    @BindView(R.id.messageView) TextView messageView;

    @BindView(R.id.replyWidget) TextView replyWidget;
    @BindView(R.id.editWidget) TextView editWidget;
    @BindView(R.id.deleteWidget) TextView deleteWidget;


    public Comment_ViewHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    @Override
    public void initialize(iList_Item listItem) {
        Comment comment = (Comment) listItem.getPayload();
        displayComment(comment);
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
