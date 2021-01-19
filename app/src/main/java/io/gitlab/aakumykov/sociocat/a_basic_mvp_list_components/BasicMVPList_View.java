package io.gitlab.aakumykov.sociocat.a_basic_mvp_list_components;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
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
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import butterknife.BindView;
import io.gitlab.aakumykov.sociocat.AppConfig;
import io.gitlab.aakumykov.sociocat.BuildConfig;
import io.gitlab.aakumykov.sociocat.R;
import io.gitlab.aakumykov.sociocat.a_basic_mvp_list_components.enums.eBasicSortingMode;
import io.gitlab.aakumykov.sociocat.a_basic_mvp_list_components.exceptions.UnknownViewModeException;
import io.gitlab.aakumykov.sociocat.a_basic_mvp_list_components.interfaces.iBasicList_Page;
import io.gitlab.aakumykov.sociocat.a_basic_mvp_list_components.interfaces.iSortingMode;
import io.gitlab.aakumykov.sociocat.a_basic_mvp_list_components.interfaces.iViewState;
import io.gitlab.aakumykov.sociocat.a_basic_mvp_list_components.utils.BasicMVPList_Utils;
import io.gitlab.aakumykov.sociocat.a_basic_mvp_list_components.utils.RecyclerViewUtils;
import io.gitlab.aakumykov.sociocat.a_basic_mvp_list_components.utils.TextUtils;
import io.gitlab.aakumykov.sociocat.a_basic_mvp_list_components.utils.ViewUtils;
import io.gitlab.aakumykov.sociocat.a_basic_mvp_list_components.utils.builders.SortingMenuItem;
import io.gitlab.aakumykov.sociocat.a_basic_mvp_list_components.view_model.BasicMVPList_ViewModel;
import io.gitlab.aakumykov.sociocat.a_basic_mvp_list_components.view_model.BasicMVPList_ViewModelFactory;
import io.gitlab.aakumykov.sociocat.a_basic_mvp_list_components.view_modes.BasicViewMode;
import io.gitlab.aakumykov.sociocat.a_basic_mvp_list_components.view_modes.FeedViewMode;
import io.gitlab.aakumykov.sociocat.a_basic_mvp_list_components.view_modes.GridViewMode;
import io.gitlab.aakumykov.sociocat.a_basic_mvp_list_components.view_modes.ListViewMode;
import io.gitlab.aakumykov.sociocat.a_basic_mvp_list_components.view_states.AllItemsSelectedViewState;
import io.gitlab.aakumykov.sociocat.a_basic_mvp_list_components.view_states.CancelableProgressViewState;
import io.gitlab.aakumykov.sociocat.a_basic_mvp_list_components.view_states.ErrorViewState;
import io.gitlab.aakumykov.sociocat.a_basic_mvp_list_components.view_states.ListFilteredViewState;
import io.gitlab.aakumykov.sociocat.a_basic_mvp_list_components.view_states.NeutralViewState;
import io.gitlab.aakumykov.sociocat.a_basic_mvp_list_components.view_states.NoneItemsSelectedViewState;
import io.gitlab.aakumykov.sociocat.a_basic_mvp_list_components.view_states.ProgressViewState;
import io.gitlab.aakumykov.sociocat.a_basic_mvp_list_components.view_states.RefreshingViewState;
import io.gitlab.aakumykov.sociocat.a_basic_mvp_list_components.view_states.SomeItemsSelectedViewState;
import io.gitlab.aakumykov.sociocat.constants.Constants;
import io.gitlab.aakumykov.sociocat.singletons.AuthSingleton;
import io.gitlab.aakumykov.sociocat.utils.MyUtils;
import io.gitlab.aakumykov.sociocat.z_base_view.BaseView;

public abstract class BasicMVPList_View
        extends BaseView
        implements iBasicList_Page
{
    @Nullable @BindView(R.id.swipeRefreshLayout) SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.progressBar) ProgressBar progressBar;
    @BindView(R.id.messageView) TextView messageView;

    private static final String TAG = BasicMVPList_View.class.getSimpleName();

    protected BasicMVPList_ViewModel mViewModel;
    protected BasicMVPList_Presenter mPresenter;
    protected BasicMVPList_DataAdapter mDataAdapter;
    protected RecyclerView.LayoutManager mLayoutManager;
    protected RecyclerView.ItemDecoration mItemDecoration;

    protected Menu mMenu;
    protected MenuInflater mMenuInflater;
    protected SubMenu mSortingSubmenu;

    protected int mActivityRequestCode = Integer.MAX_VALUE;
    protected int mActivityResultCode = Integer.MAX_VALUE;
    protected Intent mActivityResultData;

    private SearchView mSearchView;

    // Абстрактные методы
    protected abstract void setActivityView();
    protected abstract BasicMVPList_Presenter preparePresenter();
    protected abstract BasicMVPList_DataAdapter prepareDataAdapter();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActivityView();

        mViewModel = prepareViewModel();
        mPresenter = preparePresenter();
        mDataAdapter = prepareDataAdapter();

        reconfigureRecyclerView();
        configureSwipeRefresh();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mActivityRequestCode = requestCode;
        mActivityResultCode = resultCode;
        mActivityResultData = data;
    }

    @Override
    protected void onStart() {
        super.onStart();

        mPresenter.bindViews(this, mDataAdapter);

        processActivityResult();
        forgetActivityResult();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        mMenu = menu;
        mMenuInflater = getMenuInflater();

        activateIconsInMenu(menu);

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

            case R.id.actionSortByName:
                mPresenter.onSortMenuItemClicked(eBasicSortingMode.BY_NAME);
                break;

            case R.id.actionSortByDate:
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

            case R.id.actionViewModeList:
                mPresenter.onViewModeListClicked();
                break;

            case R.id.actionViewModeGrid:
                mPresenter.onViewModeGridClicked();
                break;

            case R.id.actionViewModeFeed:
                mPresenter.onViewModeFeedClicked();
                break;

            default:
                super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if (!mPresenter.onBackPressed())
            super.onBackPressed();
    }

    @Override
    public abstract void setDefaultPageTitle();

    @Override
    public RecyclerView.ItemDecoration createItemDecoration(BasicViewMode viewMode) {
        if (viewMode instanceof ListViewMode)
            return RecyclerViewUtils.createSimpleDividerItemDecoration(this, R.drawable.simple_list_item_divider);
        return null;
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
    public void setViewState(iViewState viewState) {
        if (viewState instanceof NeutralViewState) {
            setNeutralViewState();
        }
        else if (viewState instanceof ListFilteredViewState) {
            setListFilteredViewState((ListFilteredViewState) viewState);
        }
        else if (viewState instanceof CancelableProgressViewState) {
            setCancelableProgressViewState((CancelableProgressViewState) viewState);
        }
        else if (viewState instanceof ProgressViewState) {
            setProgressViewState((ProgressViewState) viewState);
        }
        else if (viewState instanceof RefreshingViewState) {
            setRefreshingViewState();
        }
        // AllItemsSelectedViewState - частный случай SomeItemsSelectedViewState,
        // поэтому должен идти перед ним.
        else if (viewState instanceof AllItemsSelectedViewState) {
            setAllSelectedViewState((AllItemsSelectedViewState) viewState);
        }
        else if (viewState instanceof SomeItemsSelectedViewState) {
            setSomeItemSelectedViewState((SomeItemsSelectedViewState) viewState);
        }
        else if (viewState instanceof NoneItemsSelectedViewState) {
            setNoneItemsSelectedViewState();
        }
        else if (viewState instanceof ErrorViewState) {
            setErrorViewState((ErrorViewState) viewState);
        }
        else
            throw new RuntimeException("Unknown view state: "+viewState);
    }

    private void setListFilteredViewState(ListFilteredViewState listFilteredViewState) {
//        setNeutralViewState();
        restoreSearchView(listFilteredViewState.getFilterText());
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
//        invalidateOptionsMenu();

        if (null != mSearchView) {
            mSearchView.setQuery(filterText, false);
            mSearchView.setIconified(false);
            mSearchView.clearFocus();
        }
    }

    @Override
    public void scroll2position(int position) {
        if (mLayoutManager instanceof LinearLayoutManager)
            ((LinearLayoutManager) mLayoutManager).scrollToPositionWithOffset(position, 0);
        else
            mLayoutManager.scrollToPosition(position);
    }

    @Override
    public Context getGlobalContext() {
        return getApplicationContext();
    }

    @Override
    public Context getLocalContext() {
        return this;
    }

    @Override
    public String getText(int stringResourceId, Object... formatArgs) {
        return TextUtils.getText(this, stringResourceId, formatArgs);
    }

    @Override
    public void reconfigureRecyclerView() {

        mLayoutManager = prepareLayoutManager(mPresenter.getCurrentViewMode());

        mItemDecoration = prepareItemDecoration(mPresenter.getCurrentViewMode());

        BasicMVPList_Utils.configureRecyclerview(
                getRecyclerView(),
                mDataAdapter,
                mLayoutManager,
                mItemDecoration
        );
    }

    @Override
    public abstract int getListScrollOffset();

    @Override
    public abstract void setListScrollOffset(int offset);

    @Override
    public void showStyledToast(int messageId) {
        MyUtils.showCustomToast(this, messageId);
    }

    @Override
    public void showStyledToast(String text) {
        MyUtils.showCustomToast(this, text);
    }

    @Override
    public void runDelayed(@NonNull Runnable runnable, long delay) {
        findViewById(android.R.id.content)
                .postDelayed(runnable, delay);
    }


    public abstract void assembleMenu();

    public abstract RecyclerView.ItemDecoration prepareItemDecoration(BasicViewMode viewMode);


    public void hideProgressMessage() {
        hideProgressBar();
        hideMessage();
    }

    public void hideMessage() {
        ViewUtils.hide(messageView);
    }

    public void hideProgressBar() {
        ViewUtils.hide(progressBar);
    }



    protected abstract RecyclerView getRecyclerView();

    protected RecyclerView.LayoutManager createGridModeLayoutManager() {
        int colsCount = getColumnsCountForGridLayout(MyUtils.getOrientation(this));

        return new StaggeredGridLayoutManager(colsCount, StaggeredGridLayoutManager.VERTICAL);
    }

    protected int getColumnsCountForGridLayout(int orientation) {
        return (Configuration.ORIENTATION_PORTRAIT == orientation) ?
                AppConfig.CARDS_GRID_COLUMNS_COUNT_PORTRAIT :
                AppConfig.CARDS_GRID_COLUMNS_COUNT_LANDSCAPE;
    }

    protected RecyclerView.LayoutManager createLinearModeLayoutManager() {
        return new LinearLayoutManager(this);
    }

    protected void setNeutralViewState() {
        setDefaultPageTitle();

        clearMenu();
        assembleMenu();

        hideRefreshThrobber();
        hideProgressMessage();
    }

    protected void setCancelableProgressViewState(CancelableProgressViewState viewState) {
        setProgressViewState(viewState);
        showInterruptButton();
    }

    protected void setProgressViewState(ProgressViewState progressViewState) {
        if (progressViewState.hasStringMessage())
            showProgressMessage(progressViewState.getStringMessage());
        else
            showProgressMessage(progressViewState.getMessageId());
    }

    protected void setRefreshingViewState() {
        showRefreshThrobber();
    }

    protected void setErrorViewState(ErrorViewState errorViewState) {
        setNeutralViewState();
        showErrorMsg(errorViewState.getMessage(this), errorViewState.getDebugMessage());
    }

    protected void setAllSelectedViewState(AllItemsSelectedViewState viewState) {
        showAllSelectedMenu();
        showSelectedItemsCount(viewState.getSelectedItemsCount());
    }

    protected void setSomeItemSelectedViewState(SomeItemsSelectedViewState viewState) {
        showSelectionMenu();
        showSelectedItemsCount(viewState.getSelectedItemsCount());
    }

    private void setNoneItemsSelectedViewState() {
        setDefaultPageTitle();
        clearMenu();
        assembleMenu();
    }

    protected void processActivityResult() {
        switch (mActivityRequestCode) {
            case Constants.CODE_LOGIN:
                processLoginResult();
                break;

            default:
                Log.w(TAG, "Unknown request code: "+mActivityRequestCode);
                break;
        }
    }



    // Внутренние
    private BasicMVPList_ViewModel prepareViewModel() {
        return new ViewModelProvider(this, new BasicMVPList_ViewModelFactory())
                .get(BasicMVPList_ViewModel.class);
    }

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

    private RecyclerView.LayoutManager prepareLayoutManager(BasicViewMode viewMode) {

        if (viewMode instanceof ListViewMode || viewMode instanceof FeedViewMode) {
            return createLinearModeLayoutManager();
        }
        else if (viewMode instanceof GridViewMode) {
            return createGridModeLayoutManager();
        }
        else {
            throw new UnknownViewModeException(viewMode);
        }
    }

    protected void addSingleItemMenu(int menuResourceId, int itemId, int showAsAction) {

        mMenuInflater.inflate(menuResourceId, mMenu);

        // Если пункт меню настроен как скрытый, меняю цвет иконки на серый
        if (MenuItem.SHOW_AS_ACTION_NEVER == showAsAction)
        {
            MenuItem menuItem = mMenu.findItem(itemId);

            Drawable menuItemIcon = menuItem.getIcon();

            if (null != menuItemIcon) {
                Drawable wrappedDrawable = DrawableCompat.wrap(menuItemIcon);
                int color = getResources().getColor(R.color.menu_icon_grey);
                DrawableCompat.setTint(wrappedDrawable, color);
                menuItem.setIcon(wrappedDrawable);
            }
        }
    }

    protected void addChangeViewModeMenu() {
        inflateMenu(R.menu.change_view_mode);
    }

    protected void addSearchView() {

        if (null == mMenuInflater || null == mMenu)
            return;

        mMenuInflater.inflate(R.menu.search_view, mMenu);

        MenuItem searchMenuItem = mMenu.findItem(R.id.actionSearch);

        if (null != searchMenuItem) {
            mSearchView = (SearchView) searchMenuItem.getActionView();
            mSearchView.setImeOptions(EditorInfo.IME_ACTION_DONE);

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

    protected void addSortingMenuRootIfNotExists() {
        if (null != mMenu) {
            if (null == mMenu.findItem(R.id.actionSort)) {
                mMenuInflater.inflate(R.menu.sort, mMenu);
                mSortingSubmenu = mMenu.findItem(R.id.actionSort).getSubMenu();
            }
        }
    }

    protected void addAuthorizationMenu() {
        if (AuthSingleton.isLoggedIn())
            inflateMenu(R.menu.logout);
        else
            inflateMenu(R.menu.login);
    }

    protected void addSortByNameMenu() {

        addSortingMenuRootIfNotExists();

        new SortingMenuItem.Builder()
                .addMenuInflater(mMenuInflater)
                .addRootMenu(mSortingSubmenu)
                .addInflatedMenuResource(R.menu.sort_by_name)
                .addInflatedMenuItemId(R.id.actionSortByName)
                .addSortingMode(mPresenter.getCurrentSortingMode())
                .addSortingOrder(mPresenter.getCurrentSortingOrder())
                .addSortingModeParamsCallback(new SortingMenuItem.iSortingModeParamsCallback() {
                    @Override
                    public boolean isSortingModeComplains(iSortingMode sortingMode) {
                        return sortingMode instanceof eBasicSortingMode;
                    }

                    @Override
                    public boolean isSortingModeActive(iSortingMode sortingMode) {
                        return eBasicSortingMode.BY_NAME.equals(sortingMode);
                    }
                })
                .create();
    }

    protected void addSortByDateMenu() {

        addSortingMenuRootIfNotExists();

        new SortingMenuItem.Builder()
                .addMenuInflater(mMenuInflater)
                .addRootMenu(mSortingSubmenu)
                .addInflatedMenuResource(R.menu.sort_by_date)
                .addInflatedMenuItemId(R.id.actionSortByDate)
                .addSortingMode(mPresenter.getCurrentSortingMode())
                .addSortingOrder(mPresenter.getCurrentSortingOrder())
                .addSortingModeParamsCallback(new SortingMenuItem.iSortingModeParamsCallback() {
                    @Override
                    public boolean isSortingModeComplains(iSortingMode sortingMode) {
                        return sortingMode instanceof eBasicSortingMode;
                    }

                    @Override
                    public boolean isSortingModeActive(iSortingMode sortingMode) {
                        return eBasicSortingMode.BY_DATE.equals(sortingMode);
                    }
                })
                .create();
    }

    private void processLoginResult() {
        refreshMenu();
    }

    private void showProgressMessage(Object data) {
        showMessage(data);
        showProgressBar();
    }

    public void showProgressBar() {
        View progressBar = findViewById(R.id.progressBar);
        if (null != progressBar)
            ViewUtils.show(progressBar);
    }

    public void showRefreshThrobber() {
        if (null != swipeRefreshLayout)
            swipeRefreshLayout.setRefreshing(true);
    }

    public void hideRefreshThrobber() {
        if (null != swipeRefreshLayout)
            swipeRefreshLayout.setRefreshing(false);
    }

    public void showErrorMsg(int userMessageId, String debugErrorMsg) {
        String msg = TextUtils.getText(this, userMessageId);
        showErrorMsg(msg, debugErrorMsg);
    }

    public void showErrorMsg(String errorMsg, String debugErrorMsg) {
        setNeutralViewState();

        if (BuildConfig.DEBUG)
            errorMsg += ": " + debugErrorMsg;

        showMessage(errorMsg);
        setMessageColor(R.color.colorErrorText);
    }

    public void showErrorMsg(Object data) {
        setNeutralViewState();

        showMessage(data);
        setMessageColor(R.color.colorErrorText);
    }

    private void showMessage(Object data) {
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

    private void showSelectionViewState(Object viewStateData) {
        showSelectionMenu();
        showSelectedItemsCount(viewStateData);
    }

    private void showSelectionMenu() {
        clearMenu();
        inflateMenu(R.menu.item_select_all);
        inflateMenu(R.menu.item_invert_selection);
        inflateMenu(R.menu.item_clear_selection);
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

    private void showInterruptButton() {
        clearMenu();
        inflateMenu(R.menu.progress_interrupt);
        setPageTitle(R.string.interrupt);
    }

    private void forgetActivityResult() {
        mActivityRequestCode = Integer.MAX_VALUE;
        mActivityResultCode = Integer.MAX_VALUE;
        mActivityResultData = null;
    }




    @SuppressLint("RestrictedApi")
    private void activateIconsInMenu(Menu menu) {
        if(menu instanceof MenuBuilder){
            MenuBuilder menuBuilder = (MenuBuilder) menu;
            menuBuilder.setOptionalIconsVisible(true);
        }
    }
}
