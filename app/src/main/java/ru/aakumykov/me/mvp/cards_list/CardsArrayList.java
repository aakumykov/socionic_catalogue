package ru.aakumykov.me.mvp.cards_list;

import java.util.ArrayList;
import ru.aakumykov.me.mvp.models.Card;


public class CardsArrayList extends ArrayList<Card> {

    public Card findCardByKey(String key) {

        for (int i=0; i<size(); i++) {

            Card currentCard = get(i);

            if (key.equals(currentCard.getKey())) {
                return currentCard;
            }
        }
        return null;
    }

}
