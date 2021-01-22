package io.gitlab.aakumykov.sociocat.b_tags_list;

import io.gitlab.aakumykov.sociocat.a_basic_mvp_list_components.BasicMVPList_DataAdapter;
import io.gitlab.aakumykov.sociocat.a_basic_mvp_list_components.adapter_utils.BasicMVPList_ViewHolderBinder;
import io.gitlab.aakumykov.sociocat.a_basic_mvp_list_components.adapter_utils.BasicMVPList_ViewHolderCreator;
import io.gitlab.aakumykov.sociocat.a_basic_mvp_list_components.adapter_utils.BasicMVPList_ViewTypeDetector;
import io.gitlab.aakumykov.sociocat.a_basic_mvp_list_components.enums.eSortingOrder;
import io.gitlab.aakumykov.sociocat.a_basic_mvp_list_components.interfaces.iBasicMVP_ItemClickListener;
import io.gitlab.aakumykov.sociocat.a_basic_mvp_list_components.interfaces.iItemsComparator;
import io.gitlab.aakumykov.sociocat.a_basic_mvp_list_components.interfaces.iSortingMode;
import io.gitlab.aakumykov.sociocat.a_basic_mvp_list_components.view_modes.BasicViewMode;
import io.gitlab.aakumykov.sociocat.b_tags_list.adapter_utils.TagsList_ViewHolderBinder;
import io.gitlab.aakumykov.sociocat.b_tags_list.adapter_utils.TagsList_ViewHolderCreator;
import io.gitlab.aakumykov.sociocat.b_tags_list.adapter_utils.TagsList_ViewTypeDetector;
import io.gitlab.aakumykov.sociocat.b_tags_list.interfaces.iTagsList_ItemClickListener;
import io.gitlab.aakumykov.sociocat.b_tags_list.list_utils.TagsList_ItemsComparator;

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
    public iItemsComparator getItemsComparator(iSortingMode sortingMode, eSortingOrder sortingOrder) {
        return new TagsList_ItemsComparator(sortingMode, sortingOrder);
    }
}
