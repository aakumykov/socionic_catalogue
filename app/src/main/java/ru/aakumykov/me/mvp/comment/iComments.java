package ru.aakumykov.me.mvp.comment;

import android.view.View;

import ru.aakumykov.me.mvp.models.Comment;

public interface iComments {

    interface commentClickListener extends View.OnClickListener {
        void onCommentMenuClicked(View view, Comment comment);
    }
}
