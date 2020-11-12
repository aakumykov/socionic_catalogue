package ru.aakumykov.me.sociocat.a_basic_mvp_components;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import butterknife.BindView;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.a_basic_mvp_components.enums.eBasicSortingMode;
import ru.aakumykov.me.sociocat.a_basic_mvp_components.helpers.SortingMenuItemConstructor;
import ru.aakumykov.me.sociocat.a_basic_mvp_components.enums.eBasicViewStates;
import ru.aakumykov.me.sociocat.a_basic_mvp_components.enums.eSortingOrder;
import ru.aakumykov.me.sociocat.a_basic_mvp_components.interfaces.iBasicListPage;
import ru.aakumykov.me.sociocat.a_basic_mvp_components.interfaces.iSortingMode;
import ru.aakumykov.me.sociocat.a_basic_mvp_components.interfaces.iViewState;
import ru.aakumykov.me.sociocat.a_basic_mvp_components.utils.TextUtils;
import ru.aakumykov.me.sociocat.a_basic_mvp_components.utils.ViewUtils;
import ru.aakumykov.me.sociocat.a_basic_mvp_components.view_model.BasicMVP_ViewModel;
import ru.aakumykov.me.sociocat.a_basic_mvp_components.view_model.BasicMVP_ViewModelFactory;
import ru.aakumykov.me.sociocat.base_view.BaseView;


public abstract class BasicMVP_View
        extends BaseView
        implements iBasicListPage
{
    @Nullable @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.progressBar) ProgressBar progressBar;
    @BindView(R.id.messageView) TextView messageView;

    private static final String TAG = BasicMVP_View.class.getSimpleName();

    protected BasicMVP_ViewModel mViewModel;
    protected BasicMVP_Presenter mPresenter;
    protected BasicMVP_DataAdapter mDataAdapter;
    protected RecyclerView.LayoutManager mLayoutManager;

    protected Menu mMenu;
    protected MenuInflater mMenuInflater;
    protected SubMenu mSortingSubmenu;

    private SearchView mSearchView;


    // Абстрактные методы
    protected abstract BasicMVP_Presenter preparePresenter();
    protected abstract BasicMVP_DataAdapter prepareDataAdapter();
    protected abstract RecyclerView.LayoutManager prepareLayoutManager();


    @Override
    protected void onStart() {
        super.onStart();

        mViewModel = new ViewModelProvider(this, new BasicMVP_ViewModelFactory())
                .get(BasicMVP_ViewModel.class);

        mPresenter = preparePresenter();

        mDataAdapter = prepareDataAdapter();

        mLayoutManager = prepareLayoutManager();

        configureSwipeRefresh();

        mPresenter.bindViews(this, mDataAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (null != mPresenter)
            mPresenter.onStop();
    }

    @SuppressLint("RestrictedApi") @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        if(menu instanceof MenuBuilder){
            MenuBuilder menuBuilder = (MenuBuilder) menu;
            menuBuilder.setOptionalIconsVisible(true);
        }

        mMenu = menu;
        mMenuInflater = getMenuInflater();

        mPresenter.onMenuCreated();

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:
                // FIXME: сделать корректно с т.з. навигации
                onBackPressed();
                break;

            case R.id.actionSortByNameDirect:
                mPresenter.onSortMenuItemClicked(eBasicSortingMode.BY_NAME);
                break;

            case R.id.actionSortByNameReverse:
                mPresenter.onSortMenuItemClicked(eBasicSortingMode.BY_NAME);
                break;

            case R.id.actionSortByDateDirect:
                mPresenter.onSortMenuItemClicked(eBasicSortingMode.BY_DATE);
                break;

            case R.id.actionSortByDateReverse:
                mPresenter.onSortMenuItemClicked(eBasicSortingMode.BY_DATE);
                break;

            case R.id.actionSelectAll:
                mPresenter.onSelectAllClicked();
                break;

            case R.id.actionClearSelection:
                mPresenter.onClearSelectionClicked();
                break;

            case R.id.actionInvertSelection:
                mPresenter.onInvertSelectionClicked();
                break;

            case R.id.actionInterrupt:
                mPresenter.onInterruptRunningProcessClicked();
                break;

            default:
                super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    public abstract void setDefaultPageTitle();

    @Override
    public void compileMenu() {
        if (null != mMenu && null != mMenuInflater) {
            clearMenu();

            addSearchView();

            addSortingMenuRoot();
            addSortByNameMenu();
            //addSortByDateMenu();
        }
    }

    @Override
    public void setPageTitle(int titleId) {
        String title = getString(titleId);
        setPageTitle(title);
    }

    @Override
    public void setPageTitle(int titleId, Object... formatArguments) {
        String title = getResources().getString(titleId, formatArguments);
        setPageTitle(title);
    }

    @Override
    public void setPageTitle(String title) {
        ActionBar actionBar = getSupportActionBar();
        if (null != actionBar)
            actionBar.setTitle(title);
    }

    @Override
    public void activateUpButton() {
        ActionBar actionBar = getSupportActionBar();
        if (null != actionBar)
            actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void setViewState(@NonNull iViewState state, @Nullable Object data) {

        eBasicViewStates viewState = (eBasicViewStates) state;

        switch (viewState) {
            case NEUTRAL:
                setNeutralViewState();
                break;

            case PROGRESS:
                showProgressMessage(data);
                break;

            case PROGRESS_WITH_CANCEL_BUTTON:
                showProgressWithCancelButton(data);
                break;

            case ERROR:
                showErrorMessage(data);
                break;

            case REFRESHING:
                showRefreshThrobber();
                break;

            case SELECTION:
                setSelectionViewState(data);
                break;

            case SELECTION_ALL:
                showAllSelectedViewState(data);
                break;

            default:
                throw new RuntimeException("Unknown viewState: "+viewState);
        }
    }

    @Override
    public void setSelectionViewState(Object viewStateData) {
        showSelectionMenu();
        showSelectedItemsCount(viewStateData);
    }

    @Override
    public void showToast(int messageId) {
        showToast(getString(messageId));
    }

    @Override
    public void showToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    @Override
    public Intent getInputIntent() {
        return getIntent();
    }

    @Override
    public void refreshMenu() {
        invalidateOptionsMenu();
    }

    @Override
    public void restoreSearchView(String filterText) {
        invalidateOptionsMenu();

        if (null != mSearchView) {
            mSearchView.setQuery(filterText, false);
            mSearchView.setIconified(false);
            mSearchView.clearFocus();
        }
    }

    @Override
    public void scroll2position(int position) {
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        if (null != recyclerView)
            recyclerView.scrollToPosition(position);
    }

    @Override
    public Context getAppContext() {
        return getApplicationContext();
    }

    @Override
    public String getText(int stringResourceId, Object... formatArgs) {
        return TextUtils.getText(this, stringResourceId, formatArgs);
    }

    @Override
    public void inflateMenuItem(int menuResourceId) {
        if (null != mMenuInflater && null != mMenu)
            mMenuInflater.inflate(menuResourceId, mMenu);
    }


    // Внутренние
    private void configureSwipeRefresh() {
        if (null == swipeRefreshLayout)
            return;

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPresenter.onRefreshRequested();
            }
        });
    }

    private void clearMenu() {
        if (null != mMenu)
            mMenu.clear();
    }

    private void addSearchView() {

        mMenuInflater.inflate(R.menu.search_view, mMenu);

        MenuItem searchMenuItem = mMenu.findItem(R.id.actionSearch);

        if (null != searchMenuItem) {
            mSearchView = (SearchView) searchMenuItem.getActionView();
            mSearchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
            mSearchView.setMaxWidth(Integer.MAX_VALUE);

            mSearchView.setOnSearchClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mPresenter.onSearchViewOpened();
                }
            });

            mSearchView.setOnCloseListener(new SearchView.OnCloseListener() {
                @Override
                public boolean onClose() {
                    mPresenter.onSearchViewClosed();
                    return false;
                }
            });

            mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String s) {
                    mPresenter.onSearchViewTextSubmitted(s);
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String s) {
                    mPresenter.onSearchViewTextChanged(s);
                    return false;
                }
            });

            mPresenter.onSearchViewCreated();
        }
    }

    private void addSortingMenuRoot() {
        mMenuInflater.inflate(R.menu.sorting, mMenu);
        mSortingSubmenu = mMenu.findItem(R.id.actionSort).getSubMenu();
    }

    private void addSortByNameMenu() {

        new SortingMenuItemConstructor()
                .addMenuInflater(mMenuInflater)
                .addTargetMenu(mSortingSubmenu)
                .addMenuResource(R.menu.menu_sort_by_name)
                .addDirectOrderMenuItemId(R.id.actionSortByNameDirect)
                .addReverseOrderMenuItemId(R.id.actionSortByNameReverse)
                .addDirectOrderActiveIcon(R.drawable.ic_menu_sort_by_name_simple)
                .addReverseOrderActiveIcon(R.drawable.ic_menu_sort_by_name_simple)
                .addDirectOrderInactiveIcon(R.drawable.ic_menu_sort_by_name_simple)
                .addSortingModeParamsCallback(new SortingMenuItemConstructor.iSortingModeParamsCallback() {
                    @Override
                    public boolean isSortingModeComplains(iSortingMode sortingMode) {
                        return sortingMode instanceof eBasicSortingMode;
                    }

                    @Override
                    public boolean isSortingModeActive(iSortingMode sortingMode) {
                        switch ((eBasicSortingMode) sortingMode) {
                            case BY_NAME:
                                return true;
                            default:
                                return false;
                        }
                    }

                    @Override
                    public boolean isDirectOrder(eSortingOrder sortingOrder) {
                        return sortingOrder.isDirect();
                    }
                })
                .makeMenuItem(mPresenter.getCurrentSortingMode(), mPresenter.getCurrentSortingOrder());
    }

    private void addSortByDateMenu() {

        new SortingMenuItemConstructor()
                .addMenuInflater(mMenuInflater)
                .addTargetMenu(mSortingSubmenu)
                .addMenuResource(R.menu.menu_sort_by_date)
                .addDirectOrderMenuItemId(R.id.actionSortByDateDirect)
                .addReverseOrderMenuItemId(R.id.actionSortByDateReverse)
                .addDirectOrderActiveIcon(R.drawable.ic_menu_sort_by_date_direct_active)
                .addReverseOrderActiveIcon(R.drawable.ic_menu_sort_by_date_reverse_active)
                .addDirectOrderInactiveIcon(R.drawable.ic_menu_sort_by_date_direct)
                .addSortingModeParamsCallback(new SortingMenuItemConstructor.iSortingModeParamsCallback() {
                    @Override
                    public boolean isSortingModeComplains(iSortingMode sortingMode) {
                        return sortingMode instanceof eBasicSortingMode;
                    }

                    @Override
                    public boolean isSortingModeActive(iSortingMode sortingMode) {
                        switch ((eBasicSortingMode) sortingMode) {
                            case BY_DATE:
                                return true;
                            default:
                                return false;
                        }
                    }

                    @Override
                    public boolean isDirectOrder(eSortingOrder sortingOrder) {
                        return sortingOrder.isDirect();
                    }
                })
                .makeMenuItem(mPresenter.getCurrentSortingMode(), mPresenter.getCurrentSortingOrder());
    }

    private void showInterruptButton() {
        clearMenu();
        inflateMenuItem(R.menu.progress_interrupt);
        setPageTitle(R.string.interrupt);
    }


    // ViewState
    private void setNeutralViewState() {
        setDefaultPageTitle();
        compileMenu();

        hideRefreshThrobber();
        hideProgressMessage();
    }

    private void showProgressMessage(@Nullable Object data) {
        showMessage(data);
        showProgressBar();
    }

    private void showProgressWithCancelButton(@Nullable Object data) {
        showProgressMessage(data);
        showInterruptButton();
    }

    public void showRefreshThrobber() {
        if (null != swipeRefreshLayout)
            swipeRefreshLayout.setRefreshing(true);
    }

    public void hideRefreshThrobber() {
        if (null != swipeRefreshLayout)
            swipeRefreshLayout.setRefreshing(false);
    }

    public void showErrorMessage(Object data) {
        setNeutralViewState();

        showMessage(data);
        setMessageColor(R.color.colorErrorText);
    }

    private void showMessage(@Nullable Object data) {
        if (data instanceof Integer)
            messageView.setText((int) data);
        else if (data instanceof String)
            messageView.setText((String) data);
        else {
            Log.e(TAG, "Unsupported data type: " + data);
            return;
        }
        ViewUtils.show(messageView);
    }

    private void setMessageColor(int colorId) {
        int color = getResources().getColor(colorId);
        messageView.setTextColor(color);
    }

    private void showSelectionMenu() {
        clearMenu();
        inflateMenuItem(R.menu.item_select_all);
        inflateMenuItem(R.menu.item_invert_selection);
        inflateMenuItem(R.menu.item_clear_selection);
    }

    private void showAllSelectedViewState(Object viewStateData) {
        showAllSelectedMenu();
        showSelectedItemsCount(viewStateData);
    }

    private void showAllSelectedMenu() {
        clearMenu();
        mMenuInflater.inflate(R.menu.item_clear_selection, mMenu);
    }

    private void showSelectedItemsCount(Object viewStateData) {
        setPageTitle(R.string.page_title_selected_items_count, viewStateData);
    }

}
