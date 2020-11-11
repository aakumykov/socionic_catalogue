package ru.aakumykov.me.sociocat.tags_list;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.a_basic_mvp_components.BasicMVP_Presenter;
import ru.aakumykov.me.sociocat.a_basic_mvp_components.enums.eBasicViewStates;
import ru.aakumykov.me.sociocat.a_basic_mvp_components.enums.eBasicSortingMode;
import ru.aakumykov.me.sociocat.a_basic_mvp_components.enums.eSortingOrder;
import ru.aakumykov.me.sociocat.a_basic_mvp_components.interfaces.iSortingMode;
import ru.aakumykov.me.sociocat.a_basic_mvp_components.list_Items.BasicMVP_DataItem;
import ru.aakumykov.me.sociocat.a_basic_mvp_components.list_Items.BasicMVP_ListItem;
import ru.aakumykov.me.sociocat.a_basic_mvp_components.view_holders.BasicMVP_DataViewHolder;
import ru.aakumykov.me.sociocat.a_basic_mvp_components.view_holders.BasicMVP_ViewHolder;
import ru.aakumykov.me.sociocat.models.Tag;
import ru.aakumykov.me.sociocat.singletons.TagsSingleton;
import ru.aakumykov.me.sociocat.singletons.iTagsSingleton;
import ru.aakumykov.me.sociocat.tags_list.enums.eTagsList_SortingMode;
import ru.aakumykov.me.sociocat.tags_list.interfaces.iTagsList_ClickListener;
import ru.aakumykov.me.sociocat.tags_list.interfaces.iTagsList_View;
import ru.aakumykov.me.sociocat.tags_list.list_parts.Tag_ListItem;
import ru.aakumykov.me.sociocat.tags_list.list_parts.Tag_ViewHolder;
import ru.aakumykov.me.sociocat.tags_list.stubs.TagsList_ViewStub;

public class TagsList_Presenter
        extends BasicMVP_Presenter
        implements iTagsList_ClickListener
{
    private final TagsSingleton mTagsSingleton;


    public TagsList_Presenter(iSortingMode defaultSortingMode) {
        super(defaultSortingMode);
        mTagsSingleton = TagsSingleton.getInstance();
    }


    @Override
    protected eSortingOrder getDefaultSortingOrderForSortingMode(iSortingMode sortingMode) {

        Map<iSortingMode,eSortingOrder> sortingOrderMap = new HashMap<>();
        sortingOrderMap.put(eTagsList_SortingMode.BY_CARDS_COUNT, eSortingOrder.REVERSE);
        sortingOrderMap.put(eBasicSortingMode.BY_NAME, eSortingOrder.DIRECT);

        if (sortingOrderMap.containsKey(sortingMode))
            return sortingOrderMap.get(sortingMode);

        return eSortingOrder.DIRECT;
    }

    @Override
    public void unbindViews() {
        mPageView = new TagsList_ViewStub();
    }

    @Override
    protected void onRefreshRequested() {
        loadList();
    }

    @Override
    public void onItemLongClicked(BasicMVP_DataViewHolder basicViewHolder) {
        onSelectItemClicked(basicViewHolder);
    }

    @Override
    public void onLoadMoreClicked(BasicMVP_ViewHolder basicViewHolder) {

    }

    @Override
    protected void onColdStart() {
        super.onColdStart();
        loadList();
    }

    @Override
    protected void onConfigChanged() {
        super.onConfigChanged();
    }


    // iTagsList_ClickListener
    @Override
    public void onTagClicked(Tag_ViewHolder tagViewHolder) {
        if (mListView.isSelectionMode()) {
            onSelectItemClicked(tagViewHolder);
        }
        else {
            int position = tagViewHolder.getAdapterPosition();
            BasicMVP_DataItem basicDataItem = (BasicMVP_DataItem) mListView.getItem(position);
            Tag tag = (Tag) basicDataItem.getPayload();
            ((iTagsList_View) mPageView).goShowCardsWithTag(tag);
        }
    }


    // Внутренние
    private void loadList() {
        //        setViewState(eBasicViewStates.PROGRESS, R.string.TAGS_LIST_loading_list);
        setViewState(eBasicViewStates.REFRESHING, R.string.TAGS_LIST_loading_list);

        mTagsSingleton.listTags(new iTagsSingleton.ListCallbacks() {
            @Override
            public void onTagsListSuccess(List<Tag> tagsList) {
                setViewState(eBasicViewStates.NEUTRAL, null);

                mListView.setList(
                        incapsulate2dataListItems(tagsList)
                );
            }

            @Override
            public void onTagsListFail(String errorMsg) {
                setErrorViewState(R.string.TAGS_LIST_error_loading_list, errorMsg);
            }
        });
    }

    private List<BasicMVP_ListItem> incapsulate2dataListItems(List<Tag> tagsList) {
        List<BasicMVP_ListItem> dataItemList = new ArrayList<>();
        for (Tag tag : tagsList)
            dataItemList.add(new Tag_ListItem(tag));
        return dataItemList;
    }


    public void onDeleteMenuItemClicked() {
        int selectedItemsCount = mListView.getSelectedItemsCount();
        List<BasicMVP_DataItem> selectedItemsList = mListView.getSelectedItems();
    }
}
