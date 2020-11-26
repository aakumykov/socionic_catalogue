package ru.aakumykov.me.sociocat.b_basic_mvp_components2.stubs;


import android.content.Context;
import android.content.Intent;

import androidx.recyclerview.widget.RecyclerView;

import ru.aakumykov.me.sociocat.b_basic_mvp_components2.interfaces.iBasicList_Page;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.interfaces.iBasicViewState;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.view_modes.BasicViewMode;

public class BasicMVP_ViewStub implements iBasicList_Page {

    @Override
    public void setDefaultPageTitle() {

    }

    @Override
    public void assembleMenu() {

    }

    @Override
    public RecyclerView.ItemDecoration createItemDecoration(BasicViewMode viewMode) {
        return null;
    }

    @Override
    public void setPageTitle(int titleId) {

    }

    @Override
    public void setPageTitle(int titleId, Object... substitutedData) {

    }

    @Override
    public void setPageTitle(String title) {

    }

    @Override
    public void activateUpButton() {

    }

    @Override
    public void setViewState(iBasicViewState viewState) {

    }

    @Override
    public void showToast(int messageId) {

    }

    @Override
    public void showToast(String text) {

    }

    @Override
    public Intent getInputIntent() {
        return null;
    }

    @Override
    public void refreshMenu() {

    }

    @Override
    public void restoreSearchView(String filterText) {

    }

    @Override
    public void scroll2position(int position) {

    }

    @Override
    public Context getAppContext() {
        return null;
    }

    @Override
    public Context getPageContext() {
        return null;
    }

    @Override
    public String getText(int stringResourceId, Object... formatArgs) {
        return null;
    }

    @Override
    public void reconfigureRecyclerView() {

    }

    @Override
    public int getListScrollPosition() {
        return 0;
    }

    @Override
    public void restoreListScrollPosition(int position) {

    }

}
