package ru.aakumykov.me.sociocat.cards_list2;

import ru.aakumykov.me.sociocat.b_basic_mvp_components2.adapter_utils.BasicMVP_ViewHolderBinder;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.adapter_utils.BasicMVP_ViewHolderCreator;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.adapter_utils.BasicMVP_ViewTypeDetector;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.interfaces.iBasicMVP_ItemClickListener;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.interfaces.iItemsComparator;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.view_modes.BasicViewMode;
import ru.aakumykov.me.sociocat.cards_list2.adapter_utils.CardsList2_ViewHolderBinder;
import ru.aakumykov.me.sociocat.cards_list2.adapter_utils.CardsList2_ViewHolderCreator;
import ru.aakumykov.me.sociocat.cards_list2.adapter_utils.CardsList2_ViewTypeDetector;
import ru.aakumykov.me.sociocat.cards_list2.list_utils.CardsList2_ItemsComparator;

public class CardsList2_DataAdapter extends ru.aakumykov.me.sociocat.b_basic_mvp_components2.BasicMVP_DataAdapter {

    public CardsList2_DataAdapter(BasicViewMode defaultViewMode,  iBasicMVP_ItemClickListener itemClickListener) {
        super(defaultViewMode, itemClickListener);
    }


    @Override
    protected BasicMVP_ViewHolderCreator prepareViewHolderCreator() {
        return new CardsList2_ViewHolderCreator(mItemClickListener);
    }

    @Override
    protected BasicMVP_ViewHolderBinder prepareViewHolderBinder() {
        return new CardsList2_ViewHolderBinder();
    }

    @Override
    protected BasicMVP_ViewTypeDetector prepareViewTypeDetector() {
        return new CardsList2_ViewTypeDetector();
    }

    @Override
    protected iItemsComparator getItemsComparator() {
        return new CardsList2_ItemsComparator();
    }


}
