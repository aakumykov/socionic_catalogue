package ru.aakumykov.me.sociocat;

import android.view.View;

import androidx.annotation.Nullable;

import java.util.List;

import ru.aakumykov.me.sociocat.card_show2.iCardShow2_View;
import ru.aakumykov.me.sociocat.card_show2.iCommentsController;
import ru.aakumykov.me.sociocat.interfaces.iCommentsSingleton;
import ru.aakumykov.me.sociocat.models.Comment;
import ru.aakumykov.me.sociocat.models.Item;
import ru.aakumykov.me.sociocat.singletons.CommentsSingleton;
import ru.aakumykov.me.sociocat.utils.MyUtils;

public class CommentsController implements iCommentsController {

    private iCardShow2_View view;


    // Родительские методы
    @Override public void bindView(iCardShow2_View view) {
        this.view = view;
    }

    @Override public void unbindView() {

    }


    // Собственные методы
    @Override public void loadComments(String parentCardId, @Nullable String start, int count) {
//        dataAdapter.showCommentsThrobber();

        CommentsSingleton.getInstance().loadList(parentCardId, new iCommentsSingleton.ListCallbacks() {
            @Override
            public void onCommentsLoadSuccess(List<Comment> list) {
//                dataAdapter.hideCommentsThrobber();
//                displayComments(list);
            }

            @Override
            public void onCommentsLoadError(String errorMsg) {
//                dataAdapter.hideCommentsThrobber();
//                showErrorMsg(R.string.CARD_SHOW_error_loading_comments, errorMsg);
            }
        });
    }

    @Override public void editComment(Comment comment) {

    }

}
