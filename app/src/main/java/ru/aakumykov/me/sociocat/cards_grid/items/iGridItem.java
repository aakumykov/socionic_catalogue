package ru.aakumykov.me.sociocat.cards_grid.items;

public interface iGridItem {

    int LOAD_MORE_VIEW_TYPE = 10;
    int THROBBER_VIEW_TYPE = 20;

    int TEXT_CARD_VIEW_TYPE = 40;
    int IMAGE_CARD_VIEW_TYPE = 50;
    int AUDIO_CARD_VIEW_TYPE = 60;
    int VIDEO_CARD_VIEW_TYPE = 70;

    void setPayload(Object payload);
    Object getPayload();

    void setIsPressed(boolean value);
    boolean isPressed();
}