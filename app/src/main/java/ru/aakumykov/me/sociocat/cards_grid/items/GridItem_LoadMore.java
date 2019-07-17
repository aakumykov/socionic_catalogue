package ru.aakumykov.me.sociocat.cards_grid.items;

public class GridItem_LoadMore extends GridItem {

    private int messageId = -1;
    private boolean clickListenerEnabled = true;

    public GridItem_LoadMore(int messageId, boolean clickListenerEnabled) {
        this.messageId = messageId;
        this.clickListenerEnabled = clickListenerEnabled;
    }

    public int getMessageId() {
        return messageId;
    }

    public boolean isClickListenerEnabled() {
        return clickListenerEnabled;
    }
}
