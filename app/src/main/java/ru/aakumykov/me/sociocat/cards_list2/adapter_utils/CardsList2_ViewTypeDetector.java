package ru.aakumykov.me.sociocat.cards_list2.adapter_utils;

import androidx.annotation.NonNull;

import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.adapter_utils.BasicMVP_ViewTypeDetector;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.data_types.BasicMVP_ItemTypes;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.list_items.BasicMVP_ListItem;
import ru.aakumykov.me.sociocat.cards_list2.list_items.Card_ListItem;

public class CardsList2_ViewTypeDetector extends BasicMVP_ViewTypeDetector {

    @Override
    public int getItemType(@NonNull BasicMVP_ListItem listItem) {

        if (listItem instanceof Card_ListItem)
            return BasicMVP_ItemTypes.DATA_ITEM;

        return super.getItemType(listItem);
    }
}
