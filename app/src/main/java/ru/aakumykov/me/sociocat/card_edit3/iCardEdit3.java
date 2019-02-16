package ru.aakumykov.me.sociocat.card_edit3;

import android.content.Intent;
import android.support.annotation.Nullable;

import ru.aakumykov.me.sociocat.models.Card;

public interface iCardEdit3 {

    interface View {
        void displayCard(Card card);

//        void showTitleError();
//        void showQuoteError();
//        void showQuoteSourceError();
//        void showImageError();
//        void showVideoError();
//        void showDescriptionError();
    }

    interface Presenter {
        void linkView(View view);
        void unlinkView();

        void processInputIntent(@Nullable Intent intent) throws Exception;
        void saveCard();
    }
}
