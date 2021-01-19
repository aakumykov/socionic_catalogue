package io.gitlab.aakumykov.sociocat.b_cards_list;

import io.gitlab.aakumykov.sociocat.a_basic_mvp_list_components.BasicMVPList_DataAdapter;
import io.gitlab.aakumykov.sociocat.a_basic_mvp_list_components.adapter_utils.BasicMVPList_ViewHolderBinder;
import io.gitlab.aakumykov.sociocat.a_basic_mvp_list_components.adapter_utils.BasicMVPList_ViewHolderCreator;
import io.gitlab.aakumykov.sociocat.a_basic_mvp_list_components.adapter_utils.BasicMVPList_ViewTypeDetector;
import io.gitlab.aakumykov.sociocat.a_basic_mvp_list_components.enums.eSortingOrder;
import io.gitlab.aakumykov.sociocat.a_basic_mvp_list_components.interfaces.iBasicMVP_ItemClickListener;
import io.gitlab.aakumykov.sociocat.a_basic_mvp_list_components.interfaces.iItemsComparator;
import io.gitlab.aakumykov.sociocat.a_basic_mvp_list_components.interfaces.iSortingMode;
import io.gitlab.aakumykov.sociocat.a_basic_mvp_list_components.view_modes.BasicViewMode;
import io.gitlab.aakumykov.sociocat.b_cards_list.adapter_utils.CardsList_ViewHolderBinder;
import io.gitlab.aakumykov.sociocat.b_cards_list.adapter_utils.CardsList_ViewHolderCreator;
import io.gitlab.aakumykov.sociocat.b_cards_list.adapter_utils.CardsList_ViewTypeDetector;
import io.gitlab.aakumykov.sociocat.b_cards_list.list_utils.CardsList_ItemsComparator;

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
    public iItemsComparator getItemsComparator(iSortingMode sortingMode, eSortingOrder sortingOrder) {
        return new CardsList_ItemsComparator(sortingMode, sortingOrder);
    }
}
