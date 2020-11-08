package ru.aakumykov.me.sociocat.tags_list;

import ru.aakumykov.me.sociocat.a_basic_mvp_components.BasicMVP_DataAdapter;
import ru.aakumykov.me.sociocat.a_basic_mvp_components.adapter_utils.BasicMVP_ViewHolderBinder;
import ru.aakumykov.me.sociocat.a_basic_mvp_components.adapter_utils.BasicMVP_ViewHolderCreator;
import ru.aakumykov.me.sociocat.a_basic_mvp_components.adapter_utils.BasicMVP_ViewTypeDetector;
import ru.aakumykov.me.sociocat.a_basic_mvp_components.interfaces.iBasic_ItemClickListener;
import ru.aakumykov.me.sociocat.a_basic_mvp_components.interfaces.iItemsComparator;

public class TagsList_DataAdapter extends BasicMVP_DataAdapter {

    public TagsList_DataAdapter(iBasic_ItemClickListener itemClickListener) {
        super(itemClickListener);
    }

    @Override
    protected BasicMVP_ViewHolderCreator prepareViewHolderCreator() {
        return new TagsList_ViewHolderCreator(mItemClickListener);
    }

    @Override
    protected BasicMVP_ViewHolderBinder prepareViewHolderBinder() {
        return new TagsList_ViewHolderBinder();
    }

    @Override
    protected BasicMVP_ViewTypeDetector prepareViewTypeDetector() {
        return new TagsList_ViewTypeDetector();
    }

    @Override
    protected iItemsComparator getItemsComparator() {
        return null;
    }
}
