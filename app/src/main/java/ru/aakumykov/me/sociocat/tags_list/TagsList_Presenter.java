package ru.aakumykov.me.sociocat.tags_list;

import java.util.ArrayList;
import java.util.List;

import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.a_basic_mvp_components.BasicMVP_Presenter;
import ru.aakumykov.me.sociocat.a_basic_mvp_components.enums.eBasicViewStates;
import ru.aakumykov.me.sociocat.a_basic_mvp_components.list_Items.BasicMVP_ListItem;
import ru.aakumykov.me.sociocat.a_basic_mvp_components.view_holders.BasicMVP_DataViewHolder;
import ru.aakumykov.me.sociocat.a_basic_mvp_components.view_holders.BasicMVP_ViewHolder;
import ru.aakumykov.me.sociocat.models.Tag;
import ru.aakumykov.me.sociocat.singletons.TagsSingleton;
import ru.aakumykov.me.sociocat.singletons.iTagsSingleton;

public class TagsList_Presenter extends BasicMVP_Presenter {

    private final TagsSingleton mTagsSingleton;

    public TagsList_Presenter() {
        mTagsSingleton = TagsSingleton.getInstance();
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
}
