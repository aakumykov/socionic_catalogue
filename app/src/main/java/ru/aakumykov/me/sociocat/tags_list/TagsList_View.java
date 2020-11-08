package ru.aakumykov.me.sociocat.tags_list;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import ru.aakumykov.me.sociocat.a_basic_mvp_components.BasicMVP_DataAdapter;
import ru.aakumykov.me.sociocat.a_basic_mvp_components.BasicMVP_Presenter;
import ru.aakumykov.me.sociocat.a_basic_mvp_components.BasicMVP_View;

public class TagsList_View extends BasicMVP_View {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected BasicMVP_Presenter preparePresenter() {
        return null;
    }

    @Override
    protected BasicMVP_DataAdapter prepareDataAdapter() {
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
