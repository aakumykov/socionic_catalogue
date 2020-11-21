package ru.aakumykov.me.sociocat.tags_list;

import ru.aakumykov.me.sociocat.b_basic_mvp_components2.BasicMVP_DataAdapter;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.adapter_utils.BasicMVP_ViewHolderBinder;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.adapter_utils.BasicMVP_ViewHolderCreator;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.adapter_utils.BasicMVP_ViewTypeDetector;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.interfaces.iBasicMVP_ItemClickListener;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.interfaces.iItemsComparator;
import ru.aakumykov.me.sociocat.tags_list.adapter_utils.TagsList_ViewHolderBinder;
import ru.aakumykov.me.sociocat.tags_list.adapter_utils.TagsList_ViewHolderCreator;
import ru.aakumykov.me.sociocat.tags_list.adapter_utils.TagsList_ViewTypeDetector;
import ru.aakumykov.me.sociocat.tags_list.interfaces.iTagsList_ItemClickListener;
import ru.aakumykov.me.sociocat.tags_list.list_utils.TagsList_ItemsComparator;

public class TagsList_DataAdapter extends BasicMVP_DataAdapter {

    public TagsList_DataAdapter(iBasicMVP_ItemClickListener itemClickListener) {
        super(itemClickListener);
    }

    @Override
    protected BasicMVP_ViewHolderCreator prepareViewHolderCreator() {
        return new TagsList_ViewHolderCreator((iTagsList_ItemClickListener) mItemClickListener);
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
        return new TagsList_ItemsComparator();
    }


    /*public int updateItemInList(@NonNull Tag oldTag, @NonNull Tag newTag) {

        Tag_ListItem tagListItem = new Tag_ListItem(newTag);

        iBasicList.iFindItemComparisionCallback comparisionCallback = new iFindItemComparisionCallback() {
            @Override
            public boolean onCompareFindingOldItemPosition(Object objectFromList) {
                Tag tagFromList = (Tag) objectFromList;
                return tagFromList.getKey().equals(oldTag.getKey());
            }
        };

        int visiblePosition = findVisibleObjectPosition(comparisionCallback);
        updateItemInVisibleList(visiblePosition, tagListItem);

        int originalPosition = findOriginalObjectPosition(comparisionCallback);
        updateItemInOriginalList(originalPosition, tagListItem);

        return visiblePosition;
    }*/
}
