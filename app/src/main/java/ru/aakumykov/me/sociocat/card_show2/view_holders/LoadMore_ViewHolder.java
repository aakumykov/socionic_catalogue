package ru.aakumykov.me.sociocat.card_show2.view_holders;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.card_show2.controllers.iCommentsController;
import ru.aakumykov.me.sociocat.models.Comment;

public class LoadMore_ViewHolder extends Base_ViewHolder {

    private final static String TAG = "LoadMore_ViewHolder";
    @BindView(R.id.textView) TextView textView;


    public LoadMore_ViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void initialize(@Nullable Comment comment, iCommentsController commentsController) {

        if (null != comment) {
            textView.setText(R.string.COMMENTS_load_more_comments);

            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //commentsController.loadComments(comment.getKey(), 10);
                }
            });
        }
        else {
            textView.setText(R.string.COMMENTS_there_is_no_comments_yet);
        }
    }

    @Override
    public void onAttached() {

    }

    @Override
    public void onDetached() {

    }
}

