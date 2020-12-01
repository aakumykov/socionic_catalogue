package ru.aakumykov.me.sociocat.cards_list2.adapter_utils;

import androidx.annotation.NonNull;

import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.adapter_utils.BasicMVPList_ViewTypeDetector;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.data_types.BasicMVPList_ItemTypes;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.list_items.BasicMVPList_ListItem;
import ru.aakumykov.me.sociocat.cards_list2.list_items.Card_ListItem;

public class CardsList2_ViewTypeDetector extends BasicMVPList_ViewTypeDetector {

    @Override
    public int getItemType(@NonNull BasicMVPList_ListItem listItem) {

        if (listItem instanceof Card_ListItem)
            return BasicMVPList_ItemTypes.DATA_ITEM;

        return super.getItemType(listItem);
    }
}
