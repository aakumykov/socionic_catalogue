package ru.aakumykov.me.sociocat.template_of_list.stubs;

import android.annotation.SuppressLint;

import androidx.annotation.Nullable;

import ru.aakumykov.me.sociocat.base_view.BaseView_Stub;
import ru.aakumykov.me.sociocat.template_of_list.iTemplateOfList;

@SuppressLint("Registered")
public class TemplateOfList_ViewStub
        extends BaseView_Stub
        implements iTemplateOfList.iPageView
{

    @Override
    public void changeLayout(iTemplateOfList.LayoutMode layoutMode) {

    }

    @Override
    public void setViewState(iTemplateOfList.ViewState viewState, Integer messageId, @Nullable Object messageDetails) {

    }

    @Override
    public boolean actionModeIsActive() {
        return false;
    }

    @Override
    public void scrollToPosition(int position) {

    }
}
