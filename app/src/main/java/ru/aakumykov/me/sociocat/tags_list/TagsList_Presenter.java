package ru.aakumykov.me.sociocat.tags_list;

import android.util.Log;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.aakumykov.me.sociocat.Constants;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.a_basic_mvp_components.BasicMVP_Presenter;
import ru.aakumykov.me.sociocat.a_basic_mvp_components.enums.eBasicSortingMode;
import ru.aakumykov.me.sociocat.a_basic_mvp_components.enums.eBasicViewStates;
import ru.aakumykov.me.sociocat.a_basic_mvp_components.enums.eSortingOrder;
import ru.aakumykov.me.sociocat.a_basic_mvp_components.interfaces.iSortingMode;
import ru.aakumykov.me.sociocat.a_basic_mvp_components.list_Items.BasicMVP_DataItem;
import ru.aakumykov.me.sociocat.a_basic_mvp_components.list_Items.BasicMVP_ListItem;
import ru.aakumykov.me.sociocat.a_basic_mvp_components.utils.TextUtils;
import ru.aakumykov.me.sociocat.a_basic_mvp_components.view_holders.BasicMVP_DataViewHolder;
import ru.aakumykov.me.sociocat.a_basic_mvp_components.view_holders.BasicMVP_ViewHolder;
import ru.aakumykov.me.sociocat.models.Tag;
import ru.aakumykov.me.sociocat.singletons.ComplexSingleton;
import ru.aakumykov.me.sociocat.singletons.TagsSingleton;
import ru.aakumykov.me.sociocat.singletons.UsersSingleton;
import ru.aakumykov.me.sociocat.singletons.iTagsSingleton;
import ru.aakumykov.me.sociocat.tags_list.enums.eTagsList_SortingMode;
import ru.aakumykov.me.sociocat.tags_list.interfaces.iTagsList_ClickListener;
import ru.aakumykov.me.sociocat.tags_list.interfaces.iTagsList_View;
import ru.aakumykov.me.sociocat.tags_list.list_parts.Tag_ListItem;
import ru.aakumykov.me.sociocat.tags_list.list_parts.Tag_ViewHolder;
import ru.aakumykov.me.sociocat.tags_list.stubs.TagsList_ViewStub;
import ru.aakumykov.me.sociocat.utils.MyUtils;
import ru.aakumykov.me.sociocat.utils.SimpleYesNoDialog;

public class TagsList_Presenter
        extends BasicMVP_Presenter
        implements iTagsList_ClickListener
{
    private static final String TAG = TagsList_Presenter.class.getSimpleName();
    private final TagsSingleton mTagsSingleton = TagsSingleton.getInstance();
    private final UsersSingleton mUsersSingleton = UsersSingleton.getInstance();
    private final ComplexSingleton mComplexSingleton = ComplexSingleton.getInstance();
    private boolean mInterruptFlag = false;

    public TagsList_Presenter(iSortingMode defaultSortingMode) {
        super(defaultSortingMode);
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
    public void onItemClicked(BasicMVP_DataViewHolder basicDataViewHolder) {

        Tag_ViewHolder tagViewHolder = (Tag_ViewHolder) basicDataViewHolder;

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

    @Override
    protected void onRefreshRequested() {
        loadList();
    }

    @Override
    public void onItemLongClicked(BasicMVP_DataViewHolder basicDataViewHolder) {
        onSelectItemClicked(basicDataViewHolder);
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

    @Override
    public void onInterruptRunningProcessClicked() {
        super.onInterruptRunningProcessClicked();
        mInterruptFlag = true;
    }


    // iTagsList_ClickListener
    @Override
    public void onEditTagClicked(Tag_ViewHolder tagViewHolder) {
        int adapterPosition = tagViewHolder.getAdapterPosition();

        if (!mUsersSingleton.currentUserIsAdmin()) {
            mPageView.showToast(R.string.action_denied);
            return;
        }

        BasicMVP_DataItem dataItem = (BasicMVP_DataItem) mListView.getItem(adapterPosition);
        Tag tag = (Tag) dataItem.getPayload();

        ((iTagsList_View) mPageView).goEditTag(tag);
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

        int count = mListView.getSelectedItemsCount();

        if (count > Constants.MAX_TAGS_AT_ONCE_DELETE_COUNT) {
            String msg = MyUtils.getPluralString(
                    mPageView.getAppContext(),
                    R.plurals.TAGS_LIST_cannot_delete_more_tags_at_once,
                    Constants.MAX_TAGS_AT_ONCE_DELETE_COUNT
            );
            mPageView.showToast(msg);
            return;
        }

        List<BasicMVP_DataItem> selectedItems = mListView.getSelectedItems();
        StringBuilder messageBuilder = new StringBuilder();
        for (BasicMVP_DataItem dataItem : selectedItems) {
            Tag tag = (Tag) dataItem.getPayload();
            messageBuilder.append(tag.getName());
            messageBuilder.append("\n");
        }

        String title = MyUtils.getPluralString(
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
        List<BasicMVP_DataItem> selectedItemsList = mListView.getSelectedItems();

        if (!mUsersSingleton.currentUserIsAdmin()) {
            mPageView.showToast(R.string.action_denied);
            return;
        }

        deleteTagsFromList(selectedItemsList);
    }

    private void deleteTagsFromList(List<BasicMVP_DataItem> tagsList) {

        if (mInterruptFlag) {
            mPageView.showToast(R.string.TAGS_LIST_deletion_process_interrupted);
            mListView.clearSelection();
            setViewState(eBasicViewStates.NEUTRAL, null);
            return;
        }

        if (0 == tagsList.size()) {
            mPageView.showToast(R.string.TAGS_LIST_selected_tags_are_processed);
            setViewState(eBasicViewStates.NEUTRAL, null);
            return;
        }

        BasicMVP_DataItem dataItem = tagsList.get(0);
        tagsList.remove(0);
        Tag tag = (Tag) dataItem.getPayload();

        String msg = TextUtils.getText(mPageView.getAppContext(), R.string.TAGS_LIST_deleting_tag, tag.getName());
        setViewState(eBasicViewStates.PROGRESS_WITH_CANCEL_BUTTON, msg);

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


}









