package ru.aakumykov.me.sociocat.template_of_list;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.SearchView;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.aakumykov.me.sociocat.BaseView;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.template_of_list.view_model.ItemsList_ViewModel;
import ru.aakumykov.me.sociocat.template_of_list.view_model.ItemsList_ViewModelFactory;
import ru.aakumykov.me.sociocat.utils.MyUtils;

public class ItemsList_View extends BaseView implements iItemsList.iPageView {

    @BindView(R.id.swipeRefreshLayout) SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.recyclerView) RecyclerView recyclerView;

    private SearchView searchView;
    private iItemsList.iDataAdapter dataAdapter;
    private iItemsList.iPresenter presenter;


    // Activity
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.template_of_list_activity);
        ButterKnife.bind(this);

        setPageTitle(R.string.LIST_TEMPLATE_title);

        activateUpButton();

        configurePresenterAndAdapter();

        configureRecyclerView();

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();

        menuInflater.inflate(R.menu.search_widget, menu);
        configureSearchView(menu);

        switch (dataAdapter.getSortingMode()) {
            case ORDER_NAME_DIRECT:
                menuInflater.inflate(R.menu.sort_by_name_reverse, menu);
                menuInflater.inflate(R.menu.sort_by_count, menu);
                break;
            case ORDER_NAME_REVERSED:
                menuInflater.inflate(R.menu.sort_by_name, menu);
                menuInflater.inflate(R.menu.sort_by_count, menu);
                break;
            case ORDER_COUNT_DIRECT:
                menuInflater.inflate(R.menu.sort_by_name, menu);
                menuInflater.inflate(R.menu.sort_by_count_reverse, menu);
                break;
            case ORDER_COUNT_REVERSED:
                menuInflater.inflate(R.menu.sort_by_name, menu);
                menuInflater.inflate(R.menu.sort_by_count, menu);
            default:
                break;
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.actionSortByName:
            case R.id.actionSortByNameReverse:
                presenter.onSortByNameClicked();
                break;

            case R.id.actionSortByCount:
            case R.id.actionSortByCountReverse:
                presenter.onSortByCountClicked();
                break;

            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }


    // BaseView
    @Override
    public void onUserLogin() {

    }

    @Override
    public void onUserLogout() {

    }

    @Override
    public void showRefreshThrobber() {
        swipeRefreshLayout.setRefreshing(true);
    }

    @Override
    public void hideRefreshThrobber() {
        swipeRefreshLayout.setRefreshing(false);
    }


    // Внутренние методы
    private void configurePresenterAndAdapter() {

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
    }

    private void configureRecyclerView() {
//        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter((RecyclerView.Adapter) dataAdapter);
    }

    private void configureSwipeRefresh() {
        swipeRefreshLayout.setColorSchemeResources(R.color.blue_swipe, R.color.green_swipe, R.color.orange_swipe, R.color.red_swipe);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                presenter.onPageRefreshRequested();
            }
        });
    }

    private void configureSearchView(Menu menu) {

        searchView = (SearchView) menu.findItem(R.id.searchWidget).getActionView();

        String hint = MyUtils.getString(this, R.string.LIST_TEMPLATE_search_items);
        searchView.setQueryHint(hint);

        if (presenter.hasFilterText()) {
            searchView.setQuery(presenter.getFilterText(), false);
            searchView.setIconified(false);
        }

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //dataAdapter.getFilter().filter(newText);
                return false;
            }
        });
    }
}
