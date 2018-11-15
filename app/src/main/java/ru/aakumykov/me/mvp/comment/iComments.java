package ru.aakumykov.me.mvp.comment;

import android.view.View;

public interface iComments {

    interface commentClickListener extends View.OnClickListener {
        void onCommentClicked(View view);
    }
}
