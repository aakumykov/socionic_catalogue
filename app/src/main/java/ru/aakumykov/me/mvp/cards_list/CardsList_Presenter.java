package ru.aakumykov.me.mvp.cards_list;

import android.support.annotation.Nullable;
import android.util.Log;

import java.util.List;

import ru.aakumykov.me.mvp.R;
import ru.aakumykov.me.mvp.interfaces.iAuthSingleton;
import ru.aakumykov.me.mvp.interfaces.iCardsSingleton;
import ru.aakumykov.me.mvp.interfaces.iCommentsSingleton;
import ru.aakumykov.me.mvp.interfaces.iDialogCallbacks;
import ru.aakumykov.me.mvp.interfaces.iTagsSingleton;
import ru.aakumykov.me.mvp.models.Card;
import ru.aakumykov.me.mvp.services.AuthSingleton;
import ru.aakumykov.me.mvp.services.CardsSingleton;
import ru.aakumykov.me.mvp.services.CommentsSingleton;
import ru.aakumykov.me.mvp.services.TagsSingleton;

public class CardsList_Presenter implements
        iCardsList.Presenter,
        iCardsSingleton.ListCallbacks,
        iCardsSingleton.DeleteCallbacks
{
    private final static String TAG = "CardsList_Presenter";
    private iCardsList.View view;
    private iAuthSingleton authService = AuthSingleton.getInstance();
    private iCardsSingleton cardsService = CardsSingleton.getInstance();
    private iTagsSingleton tagsService = TagsSingleton.getInstance();
    private iCommentsSingleton commentsService = CommentsSingleton.getInstance();

    private Card currentCard = null;
    private String tagFilter = null;

    // Системные методы
    @Override
    public void linkView(iCardsList.View view) {
        this.view = view;
    }
    @Override
    public void unlinkView() {
        this.view = null;
    }


    // TODO: обрабатывать ошибку, когда нет интернета (сейчас бесконечно крутится строка ожидания)

    // Интерфейсные
    @Override
    public void loadList(@Nullable String tagFilter) {
        Log.d(TAG, "loadList()");

        if (null != tagFilter) {
            this.tagFilter = tagFilter;
            cardsService.loadList(tagFilter,this);
        }
        else {
            this.tagFilter = null;
            cardsService.loadList(this);
        }
    }

    @Override
    public void deleteCardConfigmed(final Card card) {
        this.currentCard = card;

        try {
            cardsService.deleteCard(card, this);
        } catch (Exception e) {
            onCardDeleteError(e.getMessage());
            e.printStackTrace();
        }
    }


    // Коллбеки
    @Override
    public void onListLoadSuccess(List<Card> list) {
        if (null != tagFilter)
            view.displayTagFilter(tagFilter);

        // При уходе со страницы view исчезает
        if (null != view)
            view.displayList(list);
    }

    @Override
    public void onListLoadFail(String errorMessage) {

    }


    @Override
    public void onCardDeleteSuccess(Card card) {
        Log.d(TAG, "onCardDeleteSuccess()");

        view.hideProgressBar();
        view.showToast(R.string.card_deleted);
        view.removeListItem(card);

        tagsService.updateCardTags(
                currentCard.getKey(),
                currentCard.getTags(),
                null,
                new iTagsSingleton.UpdateCallbacks() {
                    @Override
                    public void onUpdateSuccess() {

                    }

                    @Override
                    public void onUpdateFail(String errorMsg) {
                        view.showErrorMsg(R.string.error_deleting_card_tags, errorMsg);
                    }
                }
        );

        try {
            commentsService.deleteCommentsForCard(card.getKey());
        } catch (Exception e) {
            view.showErrorMsg(R.string.CARDS_LIST_error_deleting_card, e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void onCardDeleteError(String msg) {
        Log.d(TAG, "onCardDeleteError()");
        view.hideProgressBar();
        view.showErrorMsg(R.string.CARDS_LIST_error_deleting_card, msg);
    }


    // Внутренние методы
    private void performDeleteCard(Card card) {
        Log.d(TAG, "performDeleteCard(), "+card);

    }
}
