package ru.aakumykov.me.sociocat.cards_list2;

import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.BasicMVPList_DataAdapter;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.adapter_utils.BasicMVPList_ViewHolderBinder;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.adapter_utils.BasicMVPList_ViewHolderCreator;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.adapter_utils.BasicMVPList_ViewTypeDetector;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.interfaces.iBasicMVP_ItemClickListener;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.interfaces.iItemsComparator;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.view_modes.BasicViewMode;
import ru.aakumykov.me.sociocat.cards_list2.adapter_utils.CardsList2_ViewHolderBinder;
import ru.aakumykov.me.sociocat.cards_list2.adapter_utils.CardsList2_ViewHolderCreator;
import ru.aakumykov.me.sociocat.cards_list2.adapter_utils.CardsList2_ViewTypeDetector;
import ru.aakumykov.me.sociocat.cards_list2.list_utils.CardsList2_ItemsComparator;

public class CardsList2_DataAdapter extends BasicMVPList_DataAdapter {

    public CardsList2_DataAdapter(BasicViewMode defaultViewMode,  iBasicMVP_ItemClickListener itemClickListener) {
        super(defaultViewMode, itemClickListener);
    }


    @Override
    protected BasicMVPList_ViewHolderCreator prepareViewHolderCreator() {
        return new CardsList2_ViewHolderCreator(mItemClickListener);
    }

    @Override
    protected BasicMVPList_ViewHolderBinder prepareViewHolderBinder() {
        return new CardsList2_ViewHolderBinder();
    }

    @Override
    protected BasicMVPList_ViewTypeDetector prepareViewTypeDetector() {
        return new CardsList2_ViewTypeDetector();
    }

    @Override
    protected iItemsComparator getItemsComparator() {
        return new CardsList2_ItemsComparator();
    }


}
