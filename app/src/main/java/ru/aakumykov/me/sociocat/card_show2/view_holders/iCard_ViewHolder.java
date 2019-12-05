package ru.aakumykov.me.sociocat.card_show2.view_holders;

import androidx.annotation.Nullable;

import ru.aakumykov.me.sociocat.models.User;

public interface iCard_ViewHolder {

    void disableRatingControls();
    void enableRatingControls(int ratingValue);

    void setCardRatedUp();
    void setCardRatedDown();
    void setCardNotRated();
}
