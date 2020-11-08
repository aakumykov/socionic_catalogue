package ru.aakumykov.me.sociocat.tags_list;

import ru.aakumykov.me.sociocat.a_basic_mvp_components.BasicMVP_Presenter;
import ru.aakumykov.me.sociocat.a_basic_mvp_components.enums.eBasicViewStates;
import ru.aakumykov.me.sociocat.a_basic_mvp_components.view_holders.BasicMVP_DataViewHolder;
import ru.aakumykov.me.sociocat.a_basic_mvp_components.view_holders.BasicMVP_ViewHolder;

public class TagsList_Presenter extends BasicMVP_Presenter {

    @Override
    public void unbindViews() {
        mPageView = new TagsList_ViewStub();
    }

    @Override
    protected void onRefreshRequested() {

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
        mPageView.setViewState(eBasicViewStates.NEUTRAL, null);
    }

    @Override
    protected void onConfigChanged() {
        super.onConfigChanged();
    }
}
