package ru.aakumykov.me.sociocat.card_show_2;


import ru.aakumykov.me.sociocat.models.Card;

public interface iCardController extends iController {

    interface LikeCardCallbacks {
        void onCardLikeSuccess();
        void onCardLikeError(String errorMsg);
    }

    void likeCard(Card card, LikeCardCallbacks cardCallbacks);

}
