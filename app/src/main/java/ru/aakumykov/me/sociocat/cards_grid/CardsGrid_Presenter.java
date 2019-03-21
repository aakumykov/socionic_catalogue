package ru.aakumykov.me.sociocat.cards_grid;

import android.util.Log;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.interfaces.iAuthSingleton;
import ru.aakumykov.me.sociocat.interfaces.iCardsSingleton;
import ru.aakumykov.me.sociocat.interfaces.iCommentsSingleton;
import ru.aakumykov.me.sociocat.interfaces.iStorageSingleton;
import ru.aakumykov.me.sociocat.interfaces.iTagsSingleton;
import ru.aakumykov.me.sociocat.models.Card;
import ru.aakumykov.me.sociocat.services.AuthSingleton;
import ru.aakumykov.me.sociocat.services.CardsSingleton;
import ru.aakumykov.me.sociocat.services.CommentsSingleton;
import ru.aakumykov.me.sociocat.services.StorageSingleton;
import ru.aakumykov.me.sociocat.services.TagsSingleton;

public class CardsGrid_Presenter implements
        iCardsGrid.Presenter,
        iCardsSingleton.DeleteCallbacks
{
    private iAuthSingleton authService = AuthSingleton.getInstance();
    private iCardsSingleton cardsService = CardsSingleton.getInstance();
    private iTagsSingleton tagsService = TagsSingleton.getInstance();
    private iCommentsSingleton commentsService = CommentsSingleton.getInstance();
    private iStorageSingleton storageService = StorageSingleton.getInstance();
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
        cardsService.loadList(new iCardsSingleton.ListCallbacks() {
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

        cardsService.loadNewCards(newerThanTime, new iCardsSingleton.ListCallbacks() {
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

        if (!authService.isCardOwner(card) && !authService.isAdmin()) {
            view.showErrorMsg(R.string.CARDS_LIST_you_cannot_delete_this_card);
            return;
        }

        try {
            cardsService.deleteCard(card, this);
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
            tagsService.updateCardTags(
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

            storageService.deleteImage(card.getFileName(), new iStorageSingleton.FileDeletionCallbacks() {
                @Override
                public void onDeleteSuccess() {

                }

                @Override
                public void onDeleteFail(String errorMSg) {

                }
            });

            commentsService.deleteCommentsForCard(card.getKey());


        } catch (Exception e) {
            view.showErrorMsg(R.string.error_updating_card_tags, e.getMessage());
            e.printStackTrace();
        }
    }

    private void deleteCardComments(Card card) {
        try {
            commentsService.deleteCommentsForCard(card.getKey());
        } catch (Exception e) {
            view.showErrorMsg(R.string.error_deleting_card_comments, e.getMessage());
            e.printStackTrace();
        }
    }

    private void deleteCardImage(Card card) {
        try {
            storageService.deleteImage(card.getFileName(), new iStorageSingleton.FileDeletionCallbacks() {
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
