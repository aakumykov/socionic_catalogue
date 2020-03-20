package ru.aakumykov.me.sociocat.template_of_list.stubs;

import android.annotation.SuppressLint;

import androidx.annotation.Nullable;

import ru.aakumykov.me.sociocat.base_view.BaseView_Stub;
import ru.aakumykov.me.sociocat.template_of_list.iItemsList;

@SuppressLint("Registered")
public class ItemsList_ViewStub
        extends BaseView_Stub
        implements iItemsList.iPageView
{
    @Override
    public void setViewState(iItemsList.ViewState viewState, Integer messageId, @Nullable Object messageDetails) {

    }

    @Override
    public void setLayoutType(iItemsList.LayoutType layoutType) {

    }

    @Override
    public boolean actionModeIsActive() {
        return false;
    }

    @Override
    public void scrollToPosition(int position) {

    }
}
