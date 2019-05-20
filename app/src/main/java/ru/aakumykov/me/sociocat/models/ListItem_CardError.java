package ru.aakumykov.me.sociocat.models;

public class ListItem_CardError extends ListItem {

    private String errorMsg;

    public ListItem_CardError(String errorMsg) {
        setItemType(ItemType.CARD_ERROR_ITEM);
        this.errorMsg = errorMsg;
    }

    public String getErrorMsg() {
        return errorMsg;
    }
}
