package ru.aakumykov.me.mvp.card_edit2;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import ru.aakumykov.me.mvp.iBaseView;
import ru.aakumykov.me.mvp.interfaces.iCardsSingleton;
import ru.aakumykov.me.mvp.models.Card;

public interface iCardEdit2 {

    interface View extends iBaseView {
        void switchTextMode(@Nullable Card card);
        void switchImageMode(@Nullable Card card);
        void switchVideoMode(@Nullable Card card);
    }

    interface Presenter {
        void linkView(iCardEdit2.View view);
        void unlinkView();

        void saveCard();
    }
}
