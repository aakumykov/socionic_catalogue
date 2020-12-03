package ru.aakumykov.me.sociocat.c_tags_list;

import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.BasicMVPList_DataAdapter;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.adapter_utils.BasicMVPList_ViewHolderBinder;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.adapter_utils.BasicMVPList_ViewHolderCreator;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.adapter_utils.BasicMVPList_ViewTypeDetector;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.interfaces.iBasicMVP_ItemClickListener;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.interfaces.iItemsComparator;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.view_modes.BasicViewMode;
import ru.aakumykov.me.sociocat.c_tags_list.adapter_utils.TagsList_ViewHolderBinder;
import ru.aakumykov.me.sociocat.c_tags_list.adapter_utils.TagsList_ViewHolderCreator;
import ru.aakumykov.me.sociocat.c_tags_list.adapter_utils.TagsList_ViewTypeDetector;
import ru.aakumykov.me.sociocat.c_tags_list.interfaces.iTagsList_ItemClickListener;
import ru.aakumykov.me.sociocat.c_tags_list.list_utils.TagsList_ItemsComparator;

public class TagsList_DataAdapter extends BasicMVPList_DataAdapter {

    public TagsList_DataAdapter(BasicViewMode defaultViewMode, iBasicMVP_ItemClickListener itemClickListener) {
        super(defaultViewMode, itemClickListener);
    }

    @Override
    protected BasicMVPList_ViewHolderCreator prepareViewHolderCreator() {
        return new TagsList_ViewHolderCreator((iTagsList_ItemClickListener) mItemClickListener);
    }

    @Override
    protected BasicMVPList_ViewHolderBinder prepareViewHolderBinder() {
        return new TagsList_ViewHolderBinder();
    }

    @Override
    protected BasicMVPList_ViewTypeDetector prepareViewTypeDetector() {
        return new TagsList_ViewTypeDetector();
    }

    @Override
    protected iItemsComparator getItemsComparator() {
        return new TagsList_ItemsComparator();
    }

}
