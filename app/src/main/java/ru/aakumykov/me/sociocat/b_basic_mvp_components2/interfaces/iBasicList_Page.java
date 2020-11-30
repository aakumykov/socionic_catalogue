package ru.aakumykov.me.sociocat.b_basic_mvp_components2.interfaces;


import android.content.Context;
import android.content.Intent;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import ru.aakumykov.me.sociocat.b_basic_mvp_components2.view_modes.BasicViewMode;

public interface iBasicList_Page {

    void setPageTitle(int titleId);
    void setPageTitle(int titleId, Object... substitutedData);
    void setPageTitle(String title);

    void setDefaultPageTitle();
    void assembleMenu();

    RecyclerView.ItemDecoration createItemDecoration(BasicViewMode viewMode);

    void showToast(int messageId);
    void showToast(String message);

    void showSnackbar(int msgId, int dismissStringResourceId);
    void showSnackbar(int msgId, int dismissStringResourceId, @Nullable Integer duration);
    void showSnackbar(String text, int dismissStringResourceId);
    void showSnackbar(String text, int dismissStringResourceId, @Nullable Integer duration);

    void activateUpButton();

    void setViewState(iBasicViewState viewState);

    void refreshMenu();

    void restoreSearchView(String filterText);

    void scroll2position(int position);

    // Эти два здесь неуместны
    Intent getInputIntent();
    Context getAppContext();
    Context getPageContext();

    String getText(int stringResourceId, Object... formatArgs);

    void reconfigureRecyclerView();

    int getListScrollOffset();
    void setListScrollOffset(int offset);
}
