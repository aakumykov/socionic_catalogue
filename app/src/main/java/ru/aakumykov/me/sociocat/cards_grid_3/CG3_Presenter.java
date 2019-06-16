package ru.aakumykov.me.sociocat.cards_grid_3;

import ru.aakumykov.me.sociocat.singletons.CardsSingleton;
import ru.aakumykov.me.sociocat.singletons.iCardsSingleton;

public class CG3_Presenter implements iCG3.Presenter {

    private iCG3.View view;
    private iCardsSingleton cardsSingleton = CardsSingleton.getInstance();


    @Override
    public void linkView(iCG3.View view) {
        this.view = view;
    }

    @Override
    public void unlinkView() {
        this.view = null;
    }

    @Override
    public void onWorkBegins() {

    }
}
