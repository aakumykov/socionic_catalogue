package ru.aakumykov.me.sociocat.cards_grid;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.interfaces.iAuthSingleton;
import ru.aakumykov.me.sociocat.interfaces.iCardsSingleton;
import ru.aakumykov.me.sociocat.interfaces.iCommentsSingleton;
import ru.aakumykov.me.sociocat.interfaces.iStorageSingleton;
import ru.aakumykov.me.sociocat.interfaces.iTagsSingleton;
import ru.aakumykov.me.sociocat.interfaces.iUsersSingleton;
import ru.aakumykov.me.sociocat.models.Card;
import ru.aakumykov.me.sociocat.singletons.AuthSingleton;
import ru.aakumykov.me.sociocat.singletons.CardsSingleton;
import ru.aakumykov.me.sociocat.singletons.CommentsSingleton;
import ru.aakumykov.me.sociocat.singletons.StorageSingleton;
import ru.aakumykov.me.sociocat.singletons.TagsSingleton;
import ru.aakumykov.me.sociocat.singletons.UsersSingleton;

public class CardsGrid_Presenter implements
        iCardsGrid.Presenter,
        iCardsSingleton.DeleteCallbacks
{
    private iAuthSingleton authSingleton = AuthSingleton.getInstance();
    private iUsersSingleton usersSingleton = UsersSingleton.getInstance();
    private iCardsSingleton cardsSingleton = CardsSingleton.getInstance();
    private iTagsSingleton tagsSingleton = TagsSingleton.getInstance();
    private iCommentsSingleton commentsSingleton = CommentsSingleton.getInstance();
    private iStorageSingleton storageSingleton = StorageSingleton.getInstance();
    private iCardsGrid.View view;


    // Системные методы
    @Override
    public void linkView(iCardsGrid.View view) {
        this.view = view;
    }
    @Override
    public void unlinkView() {
        this.view = null;
    }


    // Интерфейсные методы
    @Override
    public void loadCards() {
        cardsSingleton.loadList(new iCardsSingleton.ListCallbacks() {
            @Override
            public void onListLoadSuccess(List<Card> list) {
                if (null != view) view.displayList(list);
            }

            @Override
            public void onListLoadFail(String errorMessage) {
                if (null != view) view.showErrorMsg(errorMessage);
            }
        });
    }

    @Override
    public void loadNewCards(long newerThanTime) {

        view.showProgressMessage(R.string.CARDS_GRID_loading_new_cards);

        cardsSingleton.loadNewCards(newerThanTime, new iCardsSingleton.ListCallbacks() {
            @Override
            public void onListLoadSuccess(List<Card> list) {
                if (null != view) {
                    if (0 == list.size()) {
                        view.hideProgressBar();
                        view.showInfoMsg(R.string.CARDS_GRID_no_new_cards);
                    } else {
                        view.displayList(list);
                    }
                }
            }

            @Override
            public void onListLoadFail(String errorMessage) {
                if (null != view)
                    view.showErrorMsg(errorMessage);
            }
        });
    }

    @Override
    public void loadNewCards() {
        long timeNow = new Date().getTime();
        long milliSecondsInDay = TimeUnit.HOURS.toMillis(1);
        long oneDayAgo = timeNow - milliSecondsInDay;
        loadNewCards(oneDayAgo);
    }

    @Override
    public void deleteCard(final Card card) {

        if (!usersSingleton.isCardOwner(card) && !usersSingleton.currentUserIsAdmin()) {
            view.showErrorMsg(R.string.CARDS_LIST_you_cannot_delete_this_card);
            return;
        }

        try {
            cardsSingleton.deleteCard(card, this);
        } catch (Exception e) {
            onCardDeleteError(e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void onCardDeleteSuccess(Card card) {
        view.hideProgressBar();
        view.showToast(R.string.card_deleted);
        view.removeGridItem(card);

        deleteCardTags(card);
        deleteCardComments(card);
        deleteCardImage(card);
    }

    @Override
    public void onCardDeleteError(String msg) {
        view.hideProgressBar();
        view.showErrorMsg(R.string.CARDS_LIST_error_deleting_card, msg);
    }


    // Внутренние методы
    private void deleteCardTags(Card card){
        try {
            tagsSingleton.updateCardTags(
                    card.getKey(),
                    card.getTags(),
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
