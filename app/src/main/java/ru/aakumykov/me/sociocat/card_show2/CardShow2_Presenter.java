package ru.aakumykov.me.sociocat.card_show2;

import java.util.List;

import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.models.Card;
import ru.aakumykov.me.sociocat.models.Comment;
import ru.aakumykov.me.sociocat.singletons.CommentsSingleton;
import ru.aakumykov.me.sociocat.singletons.iCommentsSingleton;

public class CardShow2_Presenter implements iCardShow2.iPresenter {

    private final static String TAG = "CardShow2_Presenter";
    private CommentsSingleton commentsSingleton = CommentsSingleton.getInstance();
    private iCardShow2.iPageView pageView = null;


    @Override
    public void bindView(iCardShow2.iPageView view) {
        this.pageView = view;
    }

    @Override
    public void unbindView() {
        this.pageView = new CardShow2_ViewStub();
    }

    @Override
    public void onCardLoaded(Card card) {
        loadComments(card.getKey());
    }


    // Внутренние методы
    private void loadComments(String cardKey) {

        commentsSingleton.loadList(cardKey, null, null, new iCommentsSingleton.ListCallbacks() {
            @Override
            public void onCommentsLoadSuccess(List<Comment> list) {
                pageView.displayComments(list);
            }

            @Override
            public void onCommentsLoadError(String errorMessage) {
                pageView.showErrorMsg(R.string.CARD_SHOW_error_displaying_comments, errorMessage);
            }
        });
    }
}
