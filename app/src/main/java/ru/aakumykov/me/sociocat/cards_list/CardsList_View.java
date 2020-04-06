package ru.aakumykov.me.sociocat.cards_list;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.view.ActionMode;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.kennyc.bottomsheet.BottomSheetListener;
import com.kennyc.bottomsheet.BottomSheetMenuDialogFragment;

import org.jetbrains.annotations.NotNull;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.aakumykov.me.sociocat.AppConfig;
import ru.aakumykov.me.sociocat.CardType;
import ru.aakumykov.me.sociocat.Constants;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.base_view.BaseView;
import ru.aakumykov.me.sociocat.card_edit.CardEdit_View;
import ru.aakumykov.me.sociocat.card_show.CardShow_View;
import ru.aakumykov.me.sociocat.cards_list.view_model.CardsList_ViewModel;
import ru.aakumykov.me.sociocat.cards_list.view_model.CardsList_ViewModelFactory;
import ru.aakumykov.me.sociocat.models.Card;
import ru.aakumykov.me.sociocat.singletons.AuthSingleton;
import ru.aakumykov.me.sociocat.user_show.UserShow_View;
import ru.aakumykov.me.sociocat.utils.MyUtils;

public class CardsList_View
        extends BaseView
        implements iCardsList.iPageView, iCardsList.ListEdgeReachedListener
{
    private static final String TAG = CardShow_View.class.getSimpleName();

    @BindView(R.id.swipeRefreshLayout) SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.recyclerView) RecyclerView recyclerView;

    private SearchView searchView;

    private boolean viewIsFresh = true;
    private boolean isFilterActive = false;

    private iCardsList.iPresenter presenter;
    private iCardsList.iDataAdapter dataAdapter;

    private LinearLayoutManager feedLayoutManager;
    private LinearLayoutManager listLayoutManager;
    private StaggeredGridLayoutManager gridLayoutManager;
    private RecyclerView.LayoutManager currentLayoutManager;

    private ActionMode actionMode;
    private ActionMode.Callback actionModeCallback = new ActionModeCallback();

    private BottomSheetListener bottomSheetListener;

    private iCardsList.ViewMode initialViewMode = iCardsList.ViewMode.FEED;
    private iCardsList.ToolbarState initialToolbarState = iCardsList.ToolbarState.INITIAL;


    // Activity
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cards_list_activity);
        ButterKnife.bind(this);

        setPageTitle(R.string.CARDS_GRID_page_title);

        configureSwipeRefresh();
        configureBottomSheetListener();

        configurePresenter();
        configureAdapter();
        bindPresenterAndAdapter();

        configureLayoutManagers();
        configureRecyclerView();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case Constants.CODE_SHOW_CARD:
                processCardShowResult(resultCode, data);
                break;

            case Constants.CODE_CREATE_CARD:
                processCardCreationResult(resultCode, data);
                break;

            case Constants.CODE_EDIT_CARD:
                processCardEditionResult(resultCode, data);
                break;

            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        presenter.linkView(this);
        dataAdapter.bindBottomReachedListener(this);

        if (viewIsFresh)
        {
            viewIsFresh = false;

            if (dataAdapter.isVirgin())
                presenter.onFirstOpen(getIntent());
            else
                presenter.onConfigurationChanged();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        dataAdapter.unbindBottomReachedListener();
        presenter.unlinkView();
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();

        // Поиск
        menuInflater.inflate(R.menu.search_widget, menu);
        configureSearchView(menu);

        // Изменение вида список/плитки
        menuInflater.inflate(R.menu.view_mode, menu);
        MenuItem menuItem = menu.findItem(R.id.actionViewMode);
        if (null != menuItem) {
            switch (dataAdapter.getViewMode()) {
                case FEED:
                    menuItem.setIcon(R.drawable.ic_view_mode_list);
                    break;

                case LIST:
                    menuItem.setIcon(R.drawable.ic_view_mode_grid);
                    break;

                case GRID:
                    menuItem.setIcon(R.drawable.ic_view_mode_feed);
                    break;
            }
        }

        // Сортировка
        *//*switch (dataAdapter.getSortingMode()) {
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
        }*//*
        menuInflater.inflate(R.menu.sort, menu);

        // Профиль пользователя
        if (AuthSingleton.isLoggedIn())
            menuInflater.inflate(R.menu.profile_in, menu);
        else
            menuInflater.inflate(R.menu.profile_out, menu);

        return super.onCreateOptionsMenu(menu);
    }*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        iCardsList.ToolbarState toolbarState = presenter.getToolbarState();

        switch (toolbarState) {
            case INITIAL:
                setInitialToolbarState(menu);
                break;

            case SORTING:
                setSortingToolbarState(menu);
                break;

            case FILTERING:
                setFilteringToolbarState(menu);
                break;

            default:
                Log.e(TAG, "Unknown toolbar state: "+toolbarState);
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {

        switch (menuItem.getItemId()) {

            case R.id.actionSort:
                onSortMenuClicked();
                break;

            case R.id.actionSortByName:
                onSortByNameClicked();
                break;

            case R.id.actionSortByDate:
                onSortByDateClicked();
                break;

            case R.id.actionViewMode:
                presenter.onChangeLayoutClicked();
                break;

            default:
                return super.onOptionsItemSelected(menuItem);
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
    public void finishActionMode() {
        if (null != actionMode) {
            actionMode.finish();
            actionMode = null;
        }
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
    public void goEditCard(Card card) {
        Intent intent = new Intent(this, CardEdit_View.class);
        intent.setAction(Constants.ACTION_EDIT);
        intent.putExtra(Constants.CARD, card);
        startActivityForResult(intent, Constants.CODE_EDIT_CARD);
    }

    @Override
    public void showAddNewCardMenu() {
        new BottomSheetMenuDialogFragment.Builder(this, R.style.MyBottomSheetMenuStyleForLight)
                .setSheet(R.menu.add_new_card_bottom_shet_menu)
                .setTitle(R.string.add_new_card_bottom_menu_title)
                .setListener(bottomSheetListener)
                .show(getSupportFragmentManager());
    }

    @Override
    public void goCreateCard(CardType cardType) {
        Intent intent = new Intent(this, CardEdit_View.class);
        intent.setAction(Constants.ACTION_CREATE);
        intent.putExtra(Constants.CARD_TYPE, cardType.name());
        startActivityForResult(intent, Constants.CODE_CREATE_CARD);
    }

    @Override
    public void goUserProfile(String userId) {
        Intent intent = new Intent(this, UserShow_View.class);
        intent.putExtra(Constants.USER_ID, userId);
        startActivity(intent);
    }

    @Override
    public void go2cardComments(Card card) {
        Intent intent = new Intent(this, CardShow_View.class);
        intent.setAction(Constants.ACTION_SHOW_CARD_COMMENTS);
        intent.putExtra(Constants.CARD, card);
        startActivity(intent);
    }

    @Override
    public void changeViewMode(@NonNull iCardsList.ViewMode viewMode) {

        switch (viewMode) {
            case FEED:
                currentLayoutManager = feedLayoutManager;
                break;

            case LIST:
                currentLayoutManager = listLayoutManager;
                break;

            case GRID:
                currentLayoutManager = gridLayoutManager;
                break;

            default:
                throw new RuntimeException("Unknown layout mode");
        }

        recyclerView.setLayoutManager(currentLayoutManager);
        recyclerView.setAdapter(null);
        recyclerView.setAdapter((RecyclerView.Adapter) dataAdapter);
    }

    @Override
    public void setViewState(@Nullable iCardsList.PageViewState pageViewState, @Nullable Integer messageId, @Nullable Object messageDetails) {

        presenter.storeViewState(pageViewState, messageId, messageDetails);

        if (null == pageViewState)
            return;

        switch (pageViewState) {
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

    @Override
    public void setToolbarState(iCardsList.ToolbarState toolbarState) {
        presenter.storeToolbarState(toolbarState);
        invalidateOptionsMenu();
    }


    // iCardsList.ListEdgeReachedListener
    @Override
    public void onTopReached(int position) {

    }

    @Override
    public void onBottomReached(int position) {

    }


    // Нажатия
    @OnClick(R.id.floatingActionButton)
    void onFABClicked() {
        presenter.onNewCardMenuClicked();
    }


    // Внутренние методы
    private void configurePresenter() {

        CardsList_ViewModel viewModel = new ViewModelProvider(this, new CardsList_ViewModelFactory())
                .get(CardsList_ViewModel.class);

        if (viewModel.hasPresenter()) {
            presenter = viewModel.getPresenter();
        } else {
            presenter = new CardsList_Presenter();

            presenter.storeToolbarState(initialToolbarState);
            presenter.storeViewMode(initialViewMode);

            viewModel.storePresenter(this.presenter);
        }
    }

    private void configureAdapter() {

        if (null == this.presenter)
            throw new RuntimeException("Presenter must be created first");

        CardsList_ViewModel viewModel = new ViewModelProvider(this, new CardsList_ViewModelFactory())
                .get(CardsList_ViewModel.class);

        if (viewModel.hasDataAdapter()) {
            this.dataAdapter = viewModel.getDataAdapter();
        } else {
            this.dataAdapter = new CardsList_DataAdapter();
            viewModel.storeDataAdapter(this.dataAdapter);
        }
    }

    private void bindPresenterAndAdapter() {
        dataAdapter.setPresenter(presenter);
        presenter.setDataAdapter(dataAdapter);
    }

    private void configureLayoutManagers() {

        if (null == presenter)
            throw new RuntimeException("Presenter must be created first");

        if (null == dataAdapter)
            throw new RuntimeException("Data adapter must be created first");


        int colsNum = MyUtils.isPortraitOrientation(this) ?
                AppConfig.CARDS_GRID_COLUMNS_COUNT_PORTRAIT : AppConfig.CARDS_GRID_COLUMNS_COUNT_LANDSCAPE;

        this.feedLayoutManager = new LinearLayoutManager(this);
        this.listLayoutManager = this.feedLayoutManager;
        this.gridLayoutManager = new StaggeredGridLayoutManager(colsNum, StaggeredGridLayoutManager.VERTICAL);

        switch (presenter.getViewMode()) {
            case LIST:
                currentLayoutManager = listLayoutManager;
                break;
            case GRID:
                currentLayoutManager = gridLayoutManager;
                break;
            default:
                currentLayoutManager = feedLayoutManager;
        }
    }

    private void configureRecyclerView() {

        if (null == currentLayoutManager)
            throw new RuntimeException("Layout manager must be configured before RecyclerView");

        if (null == dataAdapter)
            throw new RuntimeException("Data adapter must be configured before RecyclerView");

        recyclerView.setLayoutManager(currentLayoutManager);
        recyclerView.setAdapter((RecyclerView.Adapter) dataAdapter);
    }

    private void configureBottomSheetListener() {
        bottomSheetListener = new BottomSheetListener() {
            @Override
            public void onSheetShown(@NotNull BottomSheetMenuDialogFragment bottomSheetMenuDialogFragment, @org.jetbrains.annotations.Nullable Object o) {

            }

            @Override
            public void onSheetItemSelected(@NotNull BottomSheetMenuDialogFragment bottomSheetMenuDialogFragment,
                                            @NotNull MenuItem menuItem, @org.jetbrains.annotations.Nullable Object o) {
                switch (menuItem.getItemId()) {
                    case R.id.actionAddTextCard:
                        presenter.onNewCardTypeSelected(CardType.TEXT_CARD);
                        break;

                    case R.id.actionAddImageCard:
                        presenter.onNewCardTypeSelected(CardType.IMAGE_CARD);
                        break;

                    case R.id.actionAddAudioCard:
                        presenter.onNewCardTypeSelected(CardType.AUDIO_CARD);
                        break;

                    case R.id.actionAddVideoCard:
                        presenter.onNewCardTypeSelected(CardType.VIDEO_CARD);
                        break;

                    default:
                        throw new RuntimeException("Unknown item id");
                }
            }

            @Override
            public void onSheetDismissed(@NotNull BottomSheetMenuDialogFragment bottomSheetMenuDialogFragment, @org.jetbrains.annotations.Nullable Object o, int i) {

            }
        };
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

    private void processCardShowResult(int resultCode, @Nullable Intent data) {

        //showToast(R.string.not_implemented_yet);

        /*if (RESULT_OK == resultCode) {
            if (null != data) {

                String action = data.getAction();
                if (null == action) action = "";

                Card card = data.getParcelableExtra(Constants.CARD);

                switch (action) {
                    case Constants.ACTION_DELETE:
                        dataAdapter.removeItem();
                        positionInWork = -1;
                        break;

                case Constants.ACTION_EDIT:
                    dataAdapter.updateItem(positionInWork, card);
                    positionInWork = -1;
                    break;

                    default:
                        dataAdapter.updateItem(positionInWork, card);
                        positionInWork = -1;
                        break;
                }

            }
        }*/
    }

    private void processCardCreationResult(int resultCode, @Nullable Intent data) {
        if (RESULT_OK != resultCode)
            return;

        presenter.onNewCardCreated(data);
    }

    private void processCardEditionResult(int resultCode, @Nullable Intent data) {
        if (RESULT_OK != resultCode)
            return;

        presenter.onCardEdited(data);
    }


    private void setInitialToolbarState(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();

        addSearchMenuItem(menuInflater, menu);
        addViewModeMenu(menuInflater, menu);
        addProfileMenuItem(menuInflater, menu);
        addSortMenuItem(menuInflater, menu, false);
    }

    private void setSortingToolbarState(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();

        //addSortMenuItem(menuInflater, menu, true);
        menuInflater.inflate(R.menu.sorting_menu, menu);
    }

    private void setFilteringToolbarState(Menu menu) {
        showToast(R.string.not_implemented_yet);
    }

    private void addSearchMenuItem(MenuInflater menuInflater, Menu menu) {
        menuInflater.inflate(R.menu.search, menu);
    }

    private void addSortMenuItem(MenuInflater menuInflater, Menu menu, boolean isActive) {
        menuInflater.inflate(R.menu.sort, menu);

        if (isActive) {
            MenuItem menuItem = menu.findItem(R.id.actionSort);
            if (null != menuItem)
                menuItem.setIcon(R.drawable.ic_sort_active);
        }
    }

    private void addProfileMenuItem(MenuInflater menuInflater, Menu menu) {
        int menuItem = (AuthSingleton.isLoggedIn()) ? R.menu.profile_in : R.menu.profile_out;
        menuInflater.inflate(menuItem, menu);
    }

    private void addViewModeMenu(MenuInflater menuInflater, Menu menu) {
        menuInflater.inflate(R.menu.view_mode, menu);

        MenuItem menuItem = menu.findItem(R.id.actionViewMode);

        if (null != menuItem) {
            switch (presenter.getViewMode()) {
                case FEED:
                    menuItem.setIcon(R.drawable.ic_view_mode_list);
                    break;

                case LIST:
                    menuItem.setIcon(R.drawable.ic_view_mode_grid);
                    break;

                case GRID:
                    menuItem.setIcon(R.drawable.ic_view_mode_feed);
                    break;
            }
        }
    }


    private void onSortMenuClicked() {
        setToolbarState(
                (iCardsList.ToolbarState.SORTING.equals(presenter.getToolbarState()))
                        ?
                iCardsList.ToolbarState.INITIAL
                        :
                iCardsList.ToolbarState.SORTING
        );
    }

    private void onSortByNameClicked() {
        dataAdapter.sortByName(new iCardsList.SortingListener() {
            @Override
            public void onSortingComplete() {
                //refreshMenu();
            }
        });
    }

    private void onSortByDateClicked() {
        dataAdapter.sortByDate(new iCardsList.SortingListener() {
            @Override
            public void onSortingComplete() {
                //refreshMenu();
            }
        });
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

            if (presenter.canSelectItem()) {
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
