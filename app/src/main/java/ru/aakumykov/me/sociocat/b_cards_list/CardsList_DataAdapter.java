package ru.aakumykov.me.sociocat.b_cards_list;

import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.BasicMVPList_DataAdapter;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.adapter_utils.BasicMVPList_ViewHolderBinder;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.adapter_utils.BasicMVPList_ViewHolderCreator;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.adapter_utils.BasicMVPList_ViewTypeDetector;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.interfaces.iBasicMVP_ItemClickListener;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.interfaces.iItemsComparator;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.view_modes.BasicViewMode;
import ru.aakumykov.me.sociocat.b_cards_list.adapter_utils.CardsList_ViewHolderBinder;
import ru.aakumykov.me.sociocat.b_cards_list.adapter_utils.CardsList_ViewHolderCreator;
import ru.aakumykov.me.sociocat.b_cards_list.adapter_utils.CardsList_ViewTypeDetector;
import ru.aakumykov.me.sociocat.b_cards_list.list_utils.CardsList_ItemsComparator;

public class CardsList_DataAdapter extends BasicMVPList_DataAdapter {

    public CardsList_DataAdapter(BasicViewMode defaultViewMode, iBasicMVP_ItemClickListener itemClickListener) {
        super(defaultViewMode, itemClickListener);
    }


    @Override
    protected BasicMVPList_ViewHolderCreator prepareViewHolderCreator() {
        return new CardsList_ViewHolderCreator(mItemClickListener);
    }

    @Override
    protected BasicMVPList_ViewHolderBinder prepareViewHolderBinder() {
        return new CardsList_ViewHolderBinder();
    }

    @Override
    protected BasicMVPList_ViewTypeDetector prepareViewTypeDetector() {
        return new CardsList_ViewTypeDetector();
    }

    @Override
    protected iItemsComparator getItemsComparator() {
        return new CardsList_ItemsComparator();
    }


}
