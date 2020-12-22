package ru.aakumykov.me.sociocat.a_basic_mvp_list_components.interfaces;


import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.view_modes.BasicViewMode;

public interface iBasicList_Page {

    void setPageTitle(int titleId);
    void setPageTitle(int titleId, Object... substitutedData);
    void setPageTitle(String title);

    void setDefaultPageTitle();

    void runDelayed(@NonNull Runnable runnable, long delay);

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
    Context getGlobalContext();
    Context getLocalContext();

    String getText(int stringResourceId, Object... formatArgs);

    void reconfigureRecyclerView();

    int getListScrollOffset();
    void setListScrollOffset(int offset);

    void showStyledToast(int messageId);
    void showStyledToast(String text);
}
