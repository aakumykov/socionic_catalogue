package io.gitlab.aakumykov.sociocat.a_basic_mvp_list_components.stubs;


import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import io.gitlab.aakumykov.sociocat.a_basic_mvp_list_components.interfaces.iBasicList_Page;
import io.gitlab.aakumykov.sociocat.a_basic_mvp_list_components.interfaces.iViewState;
import io.gitlab.aakumykov.sociocat.a_basic_mvp_list_components.view_modes.BasicViewMode;

public class BasicMVPList_ViewStub implements iBasicList_Page {

    @Override
    public void setDefaultPageTitle() {

    }

    @Override
    public void runDelayed(@NonNull Runnable runnable, long delay) {

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
    public void setViewState(iViewState viewState) {

    }

    @Override
    public void showToast(int messageId) {

    }

    @Override
    public void showToast(String text) {

    }

    @Override
    public void showSnackbar(int msgId, int dismissStringResourceId) {

    }

    @Override
    public void showSnackbar(int msgId, int dismissStringResourceId, @Nullable Integer duration) {

    }

    @Override
    public void showSnackbar(String text, int dismissStringResourceId) {

    }

    @Override
    public void showSnackbar(String text, int dismissStringResourceId, @Nullable Integer duration) {

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
    public Context getGlobalContext() {
        return null;
    }

    @Override
    public Context getLocalContext() {
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
    public int getListScrollOffset() {
        return 0;
    }

    @Override
    public void setListScrollOffset(int offset) {

    }

    @Override
    public void showStyledToast(int messageId) {

    }

    @Override
    public void showStyledToast(String text) {

    }

}
