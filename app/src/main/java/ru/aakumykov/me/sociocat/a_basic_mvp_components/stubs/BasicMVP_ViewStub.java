package ru.aakumykov.me.sociocat.a_basic_mvp_components.stubs;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;

import ru.aakumykov.me.sociocat.a_basic_mvp_components.interfaces.iBasicListPage;
import ru.aakumykov.me.sociocat.a_basic_mvp_components.interfaces.iViewState;


public class BasicMVP_ViewStub implements iBasicListPage {

    @Override
    public void setDefaultPageTitle() {

    }

    @Override
    public void compileMenu() {

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
    public void setViewState(@NonNull iViewState state, Object data) {

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
    public String getText(int stringResourceId, Object... formatArgs) {
        return null;
    }

    @Override
    public void inflateMenuItem(int menuResourceId) {

    }

    @Override
    public void setSelectionViewState(Object viewStateData) {

    }
}
