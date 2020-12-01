package ru.aakumykov.me.sociocat.tags_list;

import android.util.Log;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.aakumykov.me.sociocat.Constants;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.BasicMVPList_Presenter;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.enums.eBasicSortingMode;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.enums.eSortingOrder;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.interfaces.iBasicList;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.interfaces.iSortingMode;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.list_items.BasicMVPList_DataItem;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.list_items.BasicMVPList_ListItem;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.utils.TextUtils;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.view_holders.BasicMVPList_DataViewHolder;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.view_holders.BasicMVPList_ViewHolder;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.view_modes.BasicViewMode;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.view_states.CancelableProgressViewState;
import ru.aakumykov.me.sociocat.models.Tag;
import ru.aakumykov.me.sociocat.singletons.ComplexSingleton;
import ru.aakumykov.me.sociocat.singletons.TagsSingleton;
import ru.aakumykov.me.sociocat.singletons.UsersSingleton;
import ru.aakumykov.me.sociocat.singletons.iTagsSingleton;
import ru.aakumykov.me.sociocat.tags_list.enums.eTagsList_SortingMode;
import ru.aakumykov.me.sociocat.tags_list.interfaces.iTagsList_ItemClickListener;
import ru.aakumykov.me.sociocat.tags_list.interfaces.iTagsList_View;
import ru.aakumykov.me.sociocat.tags_list.list_parts.Tag_ListItem;
import ru.aakumykov.me.sociocat.tags_list.stubs.TagsList_ViewStub;
import ru.aakumykov.me.sociocat.tags_list.view_holders.TagViewHolder;
import ru.aakumykov.me.sociocat.utils.SimpleYesNoDialog;

public class TagsList_Presenter
        extends BasicMVPList_Presenter
        implements iTagsList_ItemClickListener
{
    private static final String TAG = TagsList_Presenter.class.getSimpleName();
    private final TagsSingleton mTagsSingleton = TagsSingleton.getInstance();
    private final UsersSingleton mUsersSingleton = UsersSingleton.getInstance();
    private final ComplexSingleton mComplexSingleton = ComplexSingleton.getInstance();
    private final boolean mInterruptFlag = false;

    public TagsList_Presenter(BasicViewMode defaultViewMode, iSortingMode defaultSortingMode) {
        super(defaultViewMode, defaultSortingMode);
    }


    @Override
    protected void onColdStart() {
        super.onColdStart();
        loadList();
    }

    @Override
    protected void onRefreshRequested() {
        loadList();
    }

    @Override
    public void unbindViews() {
        mPageView = new TagsList_ViewStub();
    }

    @Override
    public void onItemClicked(BasicMVPList_DataViewHolder basicDataViewHolder) {

        TagViewHolder tagViewHolder = (TagViewHolder) basicDataViewHolder;

        if (mListView.isSelectionMode()) {
            onSelectItemClicked(tagViewHolder);
            return;
        }

        int position = tagViewHolder.getAdapterPosition();
        BasicMVPList_DataItem basicDataItem = (BasicMVPList_DataItem) mListView.getItem(position);
        Tag tag = (Tag) basicDataItem.getPayload();

        ((iTagsList_View) mPageView).goShowCardsWithTag(tag);
    }

    @Override
    public void onItemLongClicked(BasicMVPList_DataViewHolder basicDataViewHolder) {
        onSelectItemClicked(basicDataViewHolder);
    }

    @Override
    public void onLoadMoreClicked(BasicMVPList_ViewHolder basicViewHolder) {

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

    /*@Override
    public void onInterruptRunningProcessClicked() {
        super.onInterruptRunningProcessClicked();
        mInterruptFlag = true;
    }*/


    // iTagsList_ItemClickListener
    @Override
    public void onEditTagClicked(TagViewHolder tagViewHolder) {
        int adapterPosition = tagViewHolder.getAdapterPosition();

        if (!mUsersSingleton.currentUserIsAdmin()) {
            mPageView.showToast(R.string.action_denied);
            return;
        }

        BasicMVPList_DataItem dataItem = (BasicMVPList_DataItem) mListView.getItem(adapterPosition);
        Tag tag = (Tag) dataItem.getPayload();

        ((iTagsList_View) mPageView).goEditTag(tag);
    }


    // Внутренние
    private void loadList() {

        setRefreshingViewState();

        mTagsSingleton.listTags(new iTagsSingleton.ListCallbacks() {
            @Override
            public void onTagsListSuccess(List<Tag> tagsList) {
                setNeutralViewState();

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

    private List<BasicMVPList_ListItem> incapsulate2dataListItems(List<Tag> tagsList) {
        List<BasicMVPList_ListItem> dataItemList = new ArrayList<>();
        for (Tag tag : tagsList)
            dataItemList.add(new Tag_ListItem(tag));
        return dataItemList;
    }


    public void onDeleteMenuItemClicked() {

        int count = mListView.getSelectedItemsCount();

        if (count > Constants.MAX_TAGS_AT_ONCE_DELETE_COUNT) {
            String msg = TextUtils.getPluralString(
                    mPageView.getAppContext(),
                    R.plurals.TAGS_LIST_cannot_delete_more_tags_at_once,
                    Constants.MAX_TAGS_AT_ONCE_DELETE_COUNT
            );
            mPageView.showToast(msg);
            return;
        }

        List<BasicMVPList_DataItem> selectedItems = mListView.getSelectedItems();
        StringBuilder messageBuilder = new StringBuilder();
        for (BasicMVPList_DataItem dataItem : selectedItems) {
            Tag tag = (Tag) dataItem.getPayload();
            messageBuilder.append(tag.getName());
            messageBuilder.append("\n");
        }

        String title = TextUtils.getPluralString(
                mPageView.getAppContext(),
                R.plurals.TAGS_LIST_deleting_dialog_title,
                count
        );

        SimpleYesNoDialog.show(
                mPageView.getPageContext(),
                title,
                messageBuilder.toString(),
                new SimpleYesNoDialog.AbstractCallbacks() {
                    @Override
                    public void onYes() {
                        super.onYes();
                        onTagsDeletionConfirmed();
                    }
                }
        );
    }

    private void onTagsDeletionConfirmed() {
        List<BasicMVPList_DataItem> selectedItemsList = mListView.getSelectedItems();

        if (!mUsersSingleton.currentUserIsAdmin()) {
            mPageView.showSnackbar(R.string.TAGS_LIST_you_cannot_delete_tags, R.string.SNACKBAR_got_it, 10000);
            mListView.clearSelection();
            setNeutralViewState();
            return;
        }

        deleteTagsFromList(selectedItemsList);
    }

    private void deleteTagsFromList(List<BasicMVPList_DataItem> tagsList) {

        if (hasInterruptFlag()) {
            mPageView.showToast(R.string.TAGS_LIST_deletion_process_interrupted);
            mListView.clearSelection();
            setNeutralViewState();
            clearInterruptFlag();
            return;
        }

        if (0 == tagsList.size()) {
            mPageView.showToast(R.string.TAGS_LIST_selected_tags_are_processed);
            setNeutralViewState();
            return;
        }

        BasicMVPList_DataItem dataItem = tagsList.get(0);
        tagsList.remove(0);
        Tag tag = (Tag) dataItem.getPayload();

        String msg = TextUtils.getText(mPageView.getAppContext(), R.string.TAGS_LIST_deleting_tag, tag.getName());
        setViewState(new CancelableProgressViewState(msg));

        mComplexSingleton.deleteTag(tag, new ComplexSingleton.iComplexSingleton_TagDeletionCallbacks() {
            @Override
            public void onTagDeleteSuccess(@NonNull Tag tag) {
                mListView.removeItem(dataItem);
                deleteTagsFromList(tagsList);
            }

            @Override
            public void onTagDeleteError(@NonNull String errorMsg) {
                mPageView.showToast(mPageView.getText(R.string.TAGS_LIST_error_deleting_tag, tag.getName()));
                Log.e(TAG, errorMsg);

                deleteTagsFromList(tagsList);
            }
        });
    }


    public void onTagEdited(Tag oldTag, Tag newTag) {
        if (null != oldTag && null != newTag)
        {
//            int position = ((TagsList_DataAdapter) mListView).updateItemInList(oldTag, newTag);

            Tag_ListItem newTagListItem = new Tag_ListItem(newTag);

            int position = mListView.updateItemInList(newTagListItem, new iBasicList.iFindItemComparisionCallback() {
                @Override
                public boolean onCompareWithListItemPayload(Object itemPayload) {
                    Tag tagFromList = (Tag) itemPayload;
                    return tagFromList.getKey().equals(oldTag.getKey());
                }
            });

            mPageView.scroll2position(position);
            mListView.highlightItem(position);
        }
    }

    public boolean canDeleteTag() {
        return mUsersSingleton.currentUserIsAdmin();
    }
}









