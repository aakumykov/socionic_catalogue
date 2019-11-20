package ru.aakumykov.me.sociocat.card_show2.list_items;

public interface iList_Item {

    int CARD = 10;
    int COMMENT = 20;
    int LOAD_MORE = 30;

    int getItemType();

    Object getPayload();
}
