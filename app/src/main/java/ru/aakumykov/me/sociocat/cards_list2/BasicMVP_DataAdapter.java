package ru.aakumykov.me.sociocat.cards_list2;

import ru.aakumykov.me.sociocat.b_basic_mvp_components2.adapter_utils.BasicMVP_ViewHolderBinder;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.adapter_utils.BasicMVP_ViewHolderCreator;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.adapter_utils.BasicMVP_ViewTypeDetector;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.interfaces.iBasicMVP_ItemClickListener;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.interfaces.iItemsComparator;

public class BasicMVP_DataAdapter extends ru.aakumykov.me.sociocat.b_basic_mvp_components2.BasicMVP_DataAdapter {

    public BasicMVP_DataAdapter(iBasicMVP_ItemClickListener itemClickListener) {
        super(itemClickListener);
    }

    @Override
    protected BasicMVP_ViewHolderCreator prepareViewHolderCreator() {
        return null;
    }

    @Override
    protected BasicMVP_ViewHolderBinder prepareViewHolderBinder() {
        return null;
    }

    @Override
    protected BasicMVP_ViewTypeDetector prepareViewTypeDetector() {
        return null;
    }

    @Override
    protected iItemsComparator getItemsComparator() {
        return null;
    }
}
