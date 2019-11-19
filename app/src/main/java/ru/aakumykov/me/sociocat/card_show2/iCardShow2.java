package ru.aakumykov.me.sociocat.card_show2;

import ru.aakumykov.me.sociocat.models.Card;

public interface iCardShow2 {

    interface iDataAdapter {

    }

    interface iPageView {
        void showCardThrobber();
        void hideCardThrobber();

        void showCommentsThrobber();
        void hideCommentsThrobber();

        void displayCard(Card card);
        void displayComments(String cardKey);

        void refreshCard(Card card);
        void refreshComments(String cardKey);
    }
}
