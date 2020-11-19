package ru.aakumykov.me.sociocat.cards_list2;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.BasicMVP_DataAdapter;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.BasicMVP_Presenter;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.BasicMVP_View;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.enums.eBasicSortingMode;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.enums.eBasicViewMode;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.interfaces.iDataAdapterPreparationCallback;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.interfaces.iPresenterPreparationCallback;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.utils.BasicMVP_Utils;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.utils.RecyclerViewUtils;

public class CardsList2_View extends BasicMVP_View {

    @BindView(R.id.recyclerView) RecyclerView mRecyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cards_list2_activity);
        ButterKnife.bind(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        RecyclerView.ItemDecoration itemDecoration =
                RecyclerViewUtils.createSimpleDividerItemDecoration(this, R.drawable.simple_list_item_divider);

        BasicMVP_Utils.configureRecyclerview(
                mRecyclerView,
                mDataAdapter,
                mLayoutManager,
                itemDecoration,
                null
        );
    }

    @Override
    public void compileMenu() {
        super.compileMenu();
        inflateMenu(R.menu.change_view_mode);
    }

    @Override
    protected BasicMVP_Presenter preparePresenter() {
        return BasicMVP_Utils.prepPresenter(mViewModel, new iPresenterPreparationCallback() {
            @Override
            public BasicMVP_Presenter onPresenterPrepared() {
                return new CardsList2_Presenter(eBasicViewMode.LIST, eBasicSortingMode.BY_NAME);
            }
        });
    }

    @Override
    protected BasicMVP_DataAdapter prepareDataAdapter() {
        return BasicMVP_Utils.prepDataAdapter(mViewModel, new iDataAdapterPreparationCallback() {
            @Override
            public BasicMVP_DataAdapter onDataAdapterPrepared() {
                return new CardsList2_DataAdapter(mPresenter);
            }
        });
    }

    @Override
    protected void processActivityResult() {

    }

    @Override
    public void setDefaultPageTitle() {
        setPageTitle(R.string.CARDS_LIST_page_title);
    }

    @Override
    public void onUserLogin() {

    }

    @Override
    public void onUserLogout() {

    }
}
