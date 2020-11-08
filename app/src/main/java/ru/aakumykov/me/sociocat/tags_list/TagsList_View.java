package ru.aakumykov.me.sociocat.tags_list;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import ru.aakumykov.me.sociocat.a_basic_mvp_components.Basic_DataAdapter;
import ru.aakumykov.me.sociocat.a_basic_mvp_components.Basic_Presenter;
import ru.aakumykov.me.sociocat.a_basic_mvp_components.Basic_View;

public class TagsList_View extends Basic_View {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected Basic_Presenter preparePresenter() {
        return null;
    }

    @Override
    protected Basic_DataAdapter prepareDataAdapter() {
        return null;
    }

    @Override
    protected RecyclerView.LayoutManager prepareLayoutManager() {
        return null;
    }

    @Override
    public void setDefaultPageTitle() {

    }
}
