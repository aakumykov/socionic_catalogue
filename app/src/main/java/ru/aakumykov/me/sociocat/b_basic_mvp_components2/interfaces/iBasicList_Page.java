package ru.aakumykov.me.sociocat.b_basic_mvp_components2.interfaces;


import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public interface iBasicList_Page {

    void setPageTitle(int titleId);
    void setPageTitle(int titleId, Object... substitutedData);
    void setPageTitle(String title);

    void setDefaultPageTitle();
    void compileMenu();

    void showToast(int messageId);
    void showToast(String message);

    void activateUpButton();

    void setViewState(@NonNull iBasicViewState state, @Nullable Object data);
    void setViewState(iBasicViewState viewState);

    void refreshMenu();

    void restoreSearchView(String filterText);

    void scroll2position(int position);

    // Эти два здесь неуместны
    Intent getInputIntent();
    Context getAppContext();
    Context getPageContext();
}
