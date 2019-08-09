package ru.aakumykov.me.sociocat.cards_list;

import android.util.Log;

import androidx.annotation.Nullable;

import java.util.List;

import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.models.Card;
import ru.aakumykov.me.sociocat.singletons.AuthSingleton;
import ru.aakumykov.me.sociocat.singletons.CardsSingleton;
import ru.aakumykov.me.sociocat.singletons.CommentsSingleton;
import ru.aakumykov.me.sociocat.singletons.StorageSingleton;
import ru.aakumykov.me.sociocat.singletons.TagsSingleton;
import ru.aakumykov.me.sociocat.singletons.UsersSingleton;
import ru.aakumykov.me.sociocat.singletons.iAuthSingleton;
import ru.aakumykov.me.sociocat.singletons.iCardsSingleton;
import ru.aakumykov.me.sociocat.singletons.iCommentsSingleton;
import ru.aakumykov.me.sociocat.singletons.iStorageSingleton;
import ru.aakumykov.me.sociocat.singletons.iTagsSingleton;
import ru.aakumykov.me.sociocat.singletons.iUsersSingleton;

public class CardsList_Presenter implements
        iCardsList.Presenter,
        iCardsSingleton.ListCallbacks,
        iCardsSingleton.DeleteCallbacks
{
    private final static String TAG = "CardsList_Presenter";
    private iCardsList.View view;
    private iAuthSingleton authSingleton = AuthSingleton.getInstance();
    private iUsersSingleton usersSingleton = UsersSingleton.getInstance();
    private iCardsSingleton cardsSingleton = CardsSingleton.getInstance();
    private iTagsSingleton tagsSingleton = TagsSingleton.getInstance();
    private iCommentsSingleton commentsSingleton = CommentsSingleton.getInstance();
    private iStorageSingleton storageSingleton = StorageSingleton.getInstance();

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
            cardsSingleton.loadList(tagFilter,this);
        }
        else {
            this.tagFilter = null;
            cardsSingleton.loadList(this);
        }
    }

    @Override
    public void deleteCard(final Card card) {

        if (!usersSingleton.isCardOwner(card) && !usersSingleton.currentUserIsAdmin()) {
            view.showToast(R.string.CARDS_LIST_you_cannot_delete_this_card);
            return;
        }

        this.currentCard = card;

        try {
            cardsSingleton.deleteCard(card, this);
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
        view.hideProgressMessage();
        view.showToast(R.string.card_deleted);
        view.removeListItem(card);

        deleteCardTags(card);
        deleteCardComments(card);
        deleteCardImage(card);
    }

    @Override
    public void onCardDeleteError(String msg) {
        Log.d(TAG, "onCardDeleteError()");
        view.hideProgressMessage();
        view.showErrorMsg(R.string.CARDS_LIST_error_deleting_card, msg);
    }


    // Внутренние методы
    private void performDeleteCard(Card card) {
        Log.d(TAG, "performDeleteCard(), "+card);

    }

    private void deleteCardTags(Card card){
        try {
            tagsSingleton.updateCardTags(
                    currentCard.getKey(),
                    currentCard.getTagsHash(),
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

            storageSingleton.deleteImage(card.getFileName(), new iStorageSingleton.FileDeletionCallbacks() {
                @Override
                public void onDeleteSuccess() {

                }

                @Override
                public void onDeleteFail(String errorMSg) {

                }
            });

            commentsSingleton.deleteCommentsForCard(card.getKey());


        } catch (Exception e) {
            view.showErrorMsg(R.string.error_updating_card_tags, e.getMessage());
            e.printStackTrace();
        }
    }

    private void deleteCardComments(Card card) {
        try {
            commentsSingleton.deleteCommentsForCard(card.getKey());
        } catch (Exception e) {
            view.showErrorMsg(R.string.error_deleting_card_comments, e.getMessage());
            e.printStackTrace();
        }
    }

    private void deleteCardImage(Card card) {
        try {
            storageSingleton.deleteImage(card.getFileName(), new iStorageSingleton.FileDeletionCallbacks() {
                @Override
                public void onDeleteSuccess() {

                }

                @Override
                public void onDeleteFail(String errorMSg) {

                }
            });
        } catch (Exception e) {
            view.showErrorMsg(R.string.error_deleting_card_image, e.getMessage());
            e.printStackTrace();
        }
    }
}
