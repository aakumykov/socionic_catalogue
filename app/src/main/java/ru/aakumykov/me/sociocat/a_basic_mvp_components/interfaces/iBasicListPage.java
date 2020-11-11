package ru.aakumykov.me.sociocat.a_basic_mvp_components.interfaces;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public interface iBasicListPage {

    void setPageTitle(int titleId);
    void setPageTitle(int titleId, Object... substitutedData);
    void setPageTitle(String title);

    void setDefaultPageTitle();
    void compileMenu();

    void showToast(int messageId);
    void showToast(String message);

    void activateUpButton();

    void setViewState(@NonNull iViewState state, @Nullable Object data);

    //TODO: вынести сюда другие методы установки вида...
    void showSelectionViewState(Object viewStateData);

    void refreshMenu();

    void inflateMenuItem(int menuResourceId);

    void restoreSearchView(String filterText);

    void scroll2position(int position);

    // Эти два здесь неуместны
    Intent getInputIntent();
    Context getAppContext();
}
