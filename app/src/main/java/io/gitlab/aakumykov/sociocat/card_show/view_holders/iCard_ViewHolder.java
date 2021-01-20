package io.gitlab.aakumykov.sociocat.card_show.view_holders;

public interface iCard_ViewHolder {

    void disableRatingControls();
    void enableRatingControls();
    void setRating(int value);

    void setCardRatedUp();
    void setCardRatedDown();
    void setCardNotRated();
}
