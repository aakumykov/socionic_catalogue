package ru.aakumykov.me.sociocat.card_show2.view_holders;

public interface iCard_ViewHolder {

    void disableRatingControls();
    void enableRatingControls();
    void setRating(int value);

    void setCardRatedUp();
    void setCardRatedDown();
    void setCardNotRated();
}
