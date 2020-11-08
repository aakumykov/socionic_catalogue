package ru.aakumykov.me.sociocat.tags_list;

import ru.aakumykov.me.sociocat.a_basic_mvp_components.BasicMVP_DataAdapter;
import ru.aakumykov.me.sociocat.a_basic_mvp_components.adapter_utils.Basic_ViewHolderBinder;
import ru.aakumykov.me.sociocat.a_basic_mvp_components.adapter_utils.Basic_ViewHolderCreator;
import ru.aakumykov.me.sociocat.a_basic_mvp_components.adapter_utils.Basic_ViewTypeDetector;
import ru.aakumykov.me.sociocat.a_basic_mvp_components.interfaces.iBasic_ItemClickListener;
import ru.aakumykov.me.sociocat.a_basic_mvp_components.interfaces.iItemsComparator;

public class TagsList_DataAdapter extends BasicMVP_DataAdapter {

    public TagsList_DataAdapter(iBasic_ItemClickListener itemClickListener) {
        super(itemClickListener);
    }

    @Override
    protected Basic_ViewHolderCreator prepareViewHolderCreator() {
        return null;
    }

    @Override
    protected Basic_ViewHolderBinder prepareViewHolderBinder() {
        return null;
    }

    @Override
    protected Basic_ViewTypeDetector prepareViewTypeDetector() {
        return null;
    }

    @Override
    protected iItemsComparator getItemsComparator() {
        return null;
    }
}
