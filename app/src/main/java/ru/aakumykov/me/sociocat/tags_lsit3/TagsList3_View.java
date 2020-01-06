package ru.aakumykov.me.sociocat.tags_lsit3;

import android.content.Intent;
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
import ru.aakumykov.me.sociocat.Constants;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.cards_grid.CardsGrid_View;
import ru.aakumykov.me.sociocat.models.Tag;
import ru.aakumykov.me.sociocat.tags_lsit3.view_model.TagsList3_ViewModel;
import ru.aakumykov.me.sociocat.tags_lsit3.view_model.TagsList3_ViewModelFactory;
import ru.aakumykov.me.sociocat.utils.MyUtils;

public class TagsList3_View extends BaseView implements iTagsList3.iPageView {

    @BindView(R.id.swipeRefreshLayout) SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.recyclerView) RecyclerView recyclerView;
    private SearchView searchView;
    private iTagsList3.iDataAdapter dataAdapter;
    private iTagsList3.iPresenter presenter;
    private final static String TAG = "TagsList3_View";


    // Activity
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tags_list3_activity);
        ButterKnife.bind(this);

        activateUpButton();
        setPageTitle(R.string.TAGS_LIST_page_title);


        TagsList3_ViewModel viewModel = new ViewModelProvider(this, new TagsList3_ViewModelFactory())
                .get(TagsList3_ViewModel.class);


        // Презентер (должен создаваться перед Адаптером)
        if (viewModel.hasPresenter()) {
            this.presenter = viewModel.getPresenter();
        } else {
            this.presenter = new TagsList3_Presenter();
            viewModel.storePresenter(this.presenter);
        }

        if (viewModel.hasDataAdapter()) {
            this.dataAdapter = viewModel.getDataAdapter();
        } else {
            this.dataAdapter = new TagsList3_DataAdapter(presenter);
            viewModel.storeDataAdapter(this.dataAdapter);
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();

        menuInflater.inflate(R.menu.search2, menu);
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

        return true;
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

    @Override
    public void showRefreshThrobber() {
        swipeRefreshLayout.setRefreshing(true);
    }

    @Override
    public void hideRefreshThrobber() {
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void showCardsWithTag(Tag tag) {
        Intent intent = new Intent(this, CardsGrid_View.class);
        intent.putExtra(Constants.TAG_FILTER, tag.getName());
        startActivity(intent);
    }


    // Нажатия


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

    private void configureSearchView(Menu menu) {

        searchView = (SearchView) menu.findItem(R.id.app_bar_search).getActionView();

        String hint = MyUtils.getString(this, R.string.TAGS_LIST_search_view_hint);
        searchView.setQueryHint(hint);

        CharSequence filterText = presenter.getFilterText();
        if (null != filterText) {
            searchView.setQuery(filterText, false);
            searchView.setIconified(false);
        }

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                dataAdapter.getFilter().filter(newText);
                return false;
                // TODO: попробовать true
            }
        });
    }
}
