package ru.aakumykov.me.sociocat.card_edit3;

public class CardEdit3_Presenter implements iCardEdit3.Presenter {

    private iCardEdit3.View view;

    @Override
    public void linkView(iCardEdit3.View view) {
        this.view = view;
    }

    @Override
    public void unlinkView() {
        this.view = null;
    }

    @Override
    public void saveCard() {

    }
}
