package ru.aakumykov.me.sociocat.tags_lsit3;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
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

    @BindView(R.id.filterDrawer) ViewGroup filterDrawer;
    @BindView(R.id.searchView) SearchView searchView;
    @BindView(R.id.sortByNameWidget) ImageView sortByNameWidget;
    @BindView(R.id.sortByCountWidget) ImageView sortByCountWidget;

    private iTagsList3.iDataAdapter dataAdapter;
    private iTagsList3.iPresenter presenter;


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
        getMenuInflater().inflate(R.menu.filter, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.actionFilter:
                onFilterMenuClicked();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
    @OnClick(R.id.searchView)
    void onSearchViewClicked() {

    }

    @OnClick(R.id.sortByNameWidget)
    void onSortByNameClicked() {
        dataAdapter.sortListByName();
    }

    @OnClick(R.id.sortByCountWidget)
    void onSortByCountClicked() {
        dataAdapter.sortListByCardsCount();
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

    private void onFilterMenuClicked() {
//        MyUtils.toggleVisibility(filterDrawer);
        if (filterDrawer.getVisibility() == View.GONE)
            MyUtils.show(filterDrawer);
        else
            MyUtils.hide(filterDrawer);
    }
}
