package ru.aakumykov.me.sociocat.tags_list;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.a_basic_mvp_components.BasicMVP_DataAdapter;
import ru.aakumykov.me.sociocat.a_basic_mvp_components.BasicMVP_Presenter;
import ru.aakumykov.me.sociocat.a_basic_mvp_components.BasicMVP_View;
import ru.aakumykov.me.sociocat.a_basic_mvp_components.interfaces.iDataAdapterPreparationCallback;
import ru.aakumykov.me.sociocat.a_basic_mvp_components.interfaces.iPresenterPreparationCallback;
import ru.aakumykov.me.sociocat.a_basic_mvp_components.utils.BasicMVP_Utils;

public class TagsList_View extends BasicMVP_View {

    @BindView(R.id.recyclerView) RecyclerView mRecyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tags_list);
        ButterKnife.bind(this);

        activateUpButton();
    }

    @Override
    protected void onStart() {
        super.onStart();

        DividerItemDecoration dividerItemDecoration =
                new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);

        dividerItemDecoration.setDrawable(
                ResourcesCompat.getDrawable(
                        getResources(),
                        R.drawable.simple_list_item_divider,
                        null
                )
        );

        BasicMVP_Utils.configureRecyclerview(
                mRecyclerView,
                mDataAdapter,
                mLayoutManager,
                null,
                null
        );
    }

    @Override
    protected BasicMVP_Presenter preparePresenter() {
        return BasicMVP_Utils.prepPresenter(mViewModel, new iPresenterPreparationCallback() {
            @Override
            public BasicMVP_Presenter onPresenterPrepared() {
                return new TagsList_Presenter();
            }
        });
    }

    @Override
    protected BasicMVP_DataAdapter prepareDataAdapter() {
        return BasicMVP_Utils.prepDataAdapter(mViewModel, new iDataAdapterPreparationCallback() {
            @Override
            public BasicMVP_DataAdapter onDataAdapterPrepared() {
                return new TagsList_DataAdapter(mPresenter);
            }
        });
    }

    @Override
    protected RecyclerView.LayoutManager prepareLayoutManager() {
        return new LinearLayoutManager(this);
    }

    @Override
    public void setDefaultPageTitle() {
        setPageTitle(R.string.TAGS_LIST_page_title);
    }

    @Override
    public void onUserLogin() {

    }

    @Override
    public void onUserLogout() {

    }
}
