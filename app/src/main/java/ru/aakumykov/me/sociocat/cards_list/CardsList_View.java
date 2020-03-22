package ru.aakumykov.me.sociocat.cards_list;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;

import androidx.annotation.Nullable;
import androidx.appcompat.view.ActionMode;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.aakumykov.me.sociocat.AppConfig;
import ru.aakumykov.me.sociocat.Constants;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.base_view.BaseView;
import ru.aakumykov.me.sociocat.card_show.CardShow_View;
import ru.aakumykov.me.sociocat.cards_list.view_model.CardsList_ViewModel;
import ru.aakumykov.me.sociocat.cards_list.view_model.CardsList_ViewModelFactory;
import ru.aakumykov.me.sociocat.models.Card;
import ru.aakumykov.me.sociocat.utils.MyUtils;

public class CardsList_View
        extends BaseView
        implements iCardsList.iPageView, iCardsList.ListEdgeReachedListener
{
    @BindView(R.id.swipeRefreshLayout) SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.recyclerView) RecyclerView recyclerView;

    private SearchView searchView;
    private iCardsList.iDataAdapter dataAdapter;
    private iCardsList.iPresenter presenter;
    private boolean isFilterActive = false;

    private StaggeredGridLayoutManager staggeredGridLayoutManager;
    private LinearLayoutManager linearLayoutManager;

    private RecyclerView.LayoutManager currentLayoutManager;

    private ActionMode actionMode;
    private ActionMode.Callback actionModeCallback = new ActionModeCallback();


    // Activity
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cards_list_activity);
        ButterKnife.bind(this);

        setPageTitle(R.string.CARDS_LIST_page_title);

        configureSwipeRefresh();
        configurePresenterAndAdapter();
        configureLayoutManagers();
        configureRecyclerView();
    }

    @Override
    protected void onStart() {
        super.onStart();

        presenter.linkViewAndAdapter(this, dataAdapter);
        dataAdapter.bindBottomReachedListener(this);

        if (dataAdapter.isVirgin()) {
            presenter.onFirstOpen(getIntent());
        } else {
            presenter.onConfigurationChanged();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        dataAdapter.unbindBottomReachedListener();
        presenter.unlinkViewAndAdapter();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();

        // Поиск
        menuInflater.inflate(R.menu.search_widget, menu);
        configureSearchView(menu);

        // Изменение вида список/плитки
        if (currentLayoutManager instanceof StaggeredGridLayoutManager)
            menuInflater.inflate(R.menu.list_view, menu);
        else menuInflater.inflate(R.menu.grid_view, menu);

        // Сортировка
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
                dataAdapter.sortByName(new iCardsList.SortingListener() {
                    @Override
                    public void onSortingComplete() {
                        refreshMenu();
                    }
                });
                break;

            case R.id.actionSortByCount:
            case R.id.actionSortByCountReverse:
                dataAdapter.sortByCount(new iCardsList.SortingListener() {
                    @Override
                    public void onSortingComplete() {
                        refreshMenu();
                    }
                });
                break;

            case R.id.actionListView:
                toggleViewMode();
                break;

            case R.id.actionGridView:
                toggleViewMode();
                break;

            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if (null != searchView && !searchView.isIconified()) {
            searchView.clearFocus();
            searchView.setIconified(true);
        }
        else
            super.onBackPressed();
    }


    // BaseView
    @Override
    public void onUserLogin() {

    }

    @Override
    public void onUserLogout() {

    }

    @Override
    public boolean actionModeIsActive() {
        return null != actionMode;
    }

    @Override
    public void scrollToPosition(int position) {
        recyclerView.scrollToPosition(position);
    }

    @Override
    public void goShowCard(Card card) {
        Intent intent = new Intent(this, CardShow_View.class);
        intent.putExtra(Constants.CARD, card);
        startActivityForResult(intent, Constants.CODE_SHOW_CARD);
    }

    @Override
    public void applyViewMode() {
        iCardsList.ViewMode viewMode = presenter.getStoredViewMode();

        if (null == viewMode || iCardsList.ViewMode.GRID == viewMode)
            currentLayoutManager = staggeredGridLayoutManager;
        else
            currentLayoutManager = linearLayoutManager;

        recyclerView.setLayoutManager(currentLayoutManager);
    }

    @Override
    public void setViewState(iCardsList.ViewState viewState, Integer messageId, @Nullable Object messageDetails) {

        presenter.storeViewState(viewState, messageId, messageDetails);

        //applyViewMode(presenter.getStoredViewMode());

        switch (viewState) {
            case SUCCESS:
                finishActionMode();
                hideProgressMessage();
                hideRefreshThrobber();
                break;

            case PROGRESS:
                finishActionMode();
                hideRefreshThrobber();
                showProgressMessage(messageId);
                break;

            case REFRESHING:
                finishActionMode();
                hideProgressMessage();
                break;

            case ERROR:
                finishActionMode();
                hideRefreshThrobber();
                showErrorMsg(messageId, (String) messageDetails);
                break;

            case SELECTION:
                startActionMode();
                if (null != messageDetails)
                    showSelectedItemsCount((Integer) messageDetails);
                break;
        }
    }


    // iCardsList.ListEdgeReachedListener
    @Override
    public void onTopReached(int position) {

    }

    @Override
    public void onBottomReached(int position) {

    }

    // Внутренние методы
    private void configurePresenterAndAdapter() {

        CardsList_ViewModel viewModel = new ViewModelProvider(this, new CardsList_ViewModelFactory())
                .get(CardsList_ViewModel.class);

        // Презентер (должен создаваться перед Адаптером)
        if (viewModel.hasPresenter()) {
            this.presenter = viewModel.getPresenter();
        } else {
            this.presenter = new CardsList_Presenter();
            viewModel.storePresenter(this.presenter);
        }

        // Адаптер данных
        if (viewModel.hasDataAdapter()) {
            this.dataAdapter = viewModel.getDataAdapter();
        } else {
            this.dataAdapter = new CardsList_DataAdapter(presenter);
            viewModel.storeDataAdapter(this.dataAdapter);
        }
    }

    private void configureLayoutManagers() {
        int colsNum = MyUtils.isPortraitOrientation(this) ?
                AppConfig.CARDS_GRID_COLUMNS_COUNT_PORTRAIT : AppConfig.CARDS_GRID_COLUMNS_COUNT_LANDSCAPE;

        this.staggeredGridLayoutManager = new StaggeredGridLayoutManager(colsNum, StaggeredGridLayoutManager.VERTICAL);
        this.linearLayoutManager = new LinearLayoutManager(this);
    }

    private void configureRecyclerView() {
        recyclerView.setLayoutManager(currentLayoutManager);
        recyclerView.setAdapter((RecyclerView.Adapter) dataAdapter);
    }

    private void configureSwipeRefresh() {
        swipeRefreshLayout.setColorSchemeResources(R.color.blue_swipe, R.color.green_swipe, R.color.orange_swipe, R.color.red_swipe);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                presenter.onRefreshRequested();
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

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                isFilterActive = false;
                return false;
            }
        });

        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    isFilterActive = true;
                }
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (isFilterActive) {
                    dataAdapter.getFilter().filter(newText);
                }
                return false;
            }
        });
    }

    private void toggleViewMode() {
        iCardsList.ViewMode currentViewMode = presenter.getStoredViewMode();

        if (null == currentViewMode || iCardsList.ViewMode.GRID.equals(currentViewMode))
            currentViewMode = iCardsList.ViewMode.LIST;
        else
            currentViewMode = iCardsList.ViewMode.GRID;

        presenter.storeViewMode(currentViewMode);

        applyViewMode();

        refreshMenu();
    }

    private void showRefreshThrobber() {
        swipeRefreshLayout.setRefreshing(true);
    }

    private void hideRefreshThrobber() {
        swipeRefreshLayout.setRefreshing(false);
    }

    private void showSelectedItemsCount(int count) {
        actionMode.setTitle(getString(R.string.LIST_TEMPLATE_items_selected, count));
        actionMode.invalidate();
    }

    private void startActionMode() {
        if (actionMode == null)
            actionMode = startSupportActionMode(actionModeCallback);
    }

    private void finishActionMode() {
        if (null != actionMode) {
            actionMode.finish();
            actionMode = null;
        }
    }


    // Внутренние классы
    private class ActionModeCallback implements ActionMode.Callback {
        @SuppressWarnings("unused")
        private final String TAG = ActionModeCallback.class.getSimpleName();

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.selected_menu, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {

            MenuItem menuItemSelectAll = menu.findItem(R.id.actionSelectAll);
            MenuItem menuItemClearSelection = menu.findItem(R.id.actionClearSelection);

            if (presenter.canSelectAll()) {
                if (dataAdapter.allItemsAreSelected()) {
                    menuItemSelectAll.setVisible(false);
                    menuItemClearSelection.setVisible(true);
                } else {
                    menuItemSelectAll.setVisible(true);
                    menuItemClearSelection.setVisible(false);
                }
            }
            else {
                if (null != menuItemSelectAll)
                    menuItemSelectAll.setVisible(false);

                if (null != menuItemClearSelection)
                    menuItemClearSelection.setVisible(false);
            }

            MenuItem menuItemEdit = menu.findItem(R.id.actionEdit);
            if (null != menuItemEdit)
                menuItemEdit.setVisible(presenter.canEditSelectedItem());

            MenuItem menuItemDelete = menu.findItem(R.id.actionDelete);
            if (null != menuItemDelete)
                menuItemDelete.setVisible(presenter.canDeleteSelectedItem());

            return true;
        }

        @Override
        public boolean onActionItemClicked(ActionMode actionMode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.actionSelectAll:
                    presenter.onSelectAllClicked();
                    return true;

                case R.id.actionClearSelection:
                    presenter.onClearSelectionClicked();
                    return true;

                case R.id.actionDelete:
                    presenter.onDeleteSelectedItemsClicked();
                    return true;

                case R.id.actionEdit:
                    presenter.onEditSelectedItemClicked();
                    return true;

                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode actionMode) {
            CardsList_View.this.actionMode = null;
            presenter.onActionModeDestroyed();
        }
    }
}
