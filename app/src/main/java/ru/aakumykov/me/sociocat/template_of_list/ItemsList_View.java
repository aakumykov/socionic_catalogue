package ru.aakumykov.me.sociocat.template_of_list;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.aakumykov.me.sociocat.BaseView;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.template_of_list.view_model.ItemsList_ViewModel;
import ru.aakumykov.me.sociocat.template_of_list.view_model.ItemsList_ViewModelFactory;

public class ItemsList_View extends BaseView implements iItemsList.iPageView {

    @BindView(R.id.swipeRefreshLayout) SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.recyclerView) RecyclerView recyclerView;

    private iItemsList.iDataAdapter dataAdapter;
    private iItemsList.iPresenter presenter;


    // Activity
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.items_list_activity);
        ButterKnife.bind(this);

        activateUpButton();
        setPageTitle(R.string.LIST_TEMPLATE_title);

        ItemsList_ViewModel viewModel = new ViewModelProvider(this, new ItemsList_ViewModelFactory())
                .get(ItemsList_ViewModel.class);

        // Презентер (должен создаваться перед Адаптером)
        if (viewModel.hasPresenter()) {
            this.presenter = viewModel.getPresenter();
        } else {
            this.presenter = new ItemsList_Presenter();
            viewModel.storePresenter(this.presenter);
        }


        // Адаптер данных
        if (viewModel.hasDataAdapter()) {
            this.dataAdapter = viewModel.getDataAdapter();
        } else {
            this.dataAdapter = new ItemsList_DataAdapter(presenter);
            viewModel.storeDataAdapter(this.dataAdapter);
        }

        // Настройка recyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        recyclerView.setAdapter((RecyclerView.Adapter) dataAdapter);

        // Настройка обновления протягиванием
        configureSwipeRefresh();
    }

    @Override
    protected void onStart() {
        super.onStart();

        presenter.linkViewAndAdapter(this, dataAdapter);

        if (dataAdapter.isVirgin()) {
            presenter.onFirstOpen(getIntent());
        } else {
            presenter.onConfigurationChanged();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        presenter.unlinkView();
    }

    // BaseView
    @Override
    public void onUserLogin() {

    }

    @Override
    public void onUserLogout() {

    }

    @Override
    public void hideRefreshThrobber() {
        swipeRefreshLayout.setRefreshing(false);
    }


    // Внутренние методы
    private void configureSwipeRefresh() {
        swipeRefreshLayout.setColorSchemeResources(R.color.blue_swipe, R.color.green_swipe, R.color.orange_swipe, R.color.red_swipe);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                presenter.onPageRefreshRequested();
            }
        });
    }
}
