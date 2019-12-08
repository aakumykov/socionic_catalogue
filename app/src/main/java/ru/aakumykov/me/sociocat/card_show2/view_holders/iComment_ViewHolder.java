package ru.aakumykov.me.sociocat.card_show2.view_holders;

public interface iComment_ViewHolder {
    void fadeBackground();
    void unfadeBackground();

    void disableRatingControls();
    void enablRatingControls();

    void setRating(int value);

    void setRatedUp();
    void setRatedDown();
    void setNotRated();
}
