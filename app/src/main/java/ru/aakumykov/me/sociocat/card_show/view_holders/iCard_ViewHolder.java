package ru.aakumykov.me.sociocat.card_show.view_holders;

public interface iCard_ViewHolder {

    void showRatingThrobber();
    void hideRatingThrobber();

    void disableRatingButtons();
    void enableRatingContols();

    void setRatingValue(int value);
}
