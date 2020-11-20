package ru.aakumykov.me.sociocat.b_basic_mvp_components2;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import butterknife.BindView;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.enums.eBasicSortingMode;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.enums.eSortingOrder;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.helpers.SortingMenuItemConstructor;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.interfaces.iBasicList_Page;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.interfaces.iBasicViewState;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.interfaces.iSortingMode;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.utils.PageUtils;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.utils.RecyclerViewUtils;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.utils.TextUtils;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.utils.ViewUtils;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.view_model.BasicMVP_ViewModel;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.view_model.BasicMVP_ViewModelFactory;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.view_modes.BasicViewMode;

public abstract class BasicMVP_Fragment
        extends Fragment
        implements iBasicList_Page
{
    @Nullable @BindView(R.id.swipeRefreshLayout) SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.progressBar) ProgressBar progressBar;
    @BindView(R.id.messageView) TextView messageView;

    private static final String TAG = BasicMVP_Fragment.class.getSimpleName();

    protected BasicMVP_ViewModel mViewModel;
    protected BasicMVP_Presenter mPresenter;
    protected BasicMVP_DataAdapter mDataAdapter;
    protected RecyclerView.LayoutManager mLayoutManager;

    private View mFragmentView;

    protected Menu mMenu;
    protected MenuInflater mMenuInflater;
    protected SubMenu mSortingSubmenu;
    private SearchView mSearchView;


    protected abstract BasicMVP_Presenter preparePresenter();
    protected abstract BasicMVP_DataAdapter prepareDataAdapter();
    protected abstract RecyclerView.LayoutManager prepareLayoutManager();


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mViewModel = new ViewModelProvider(this, new BasicMVP_ViewModelFactory())
                .get(BasicMVP_ViewModel.class);
        mPresenter = preparePresenter();
        mDataAdapter = prepareDataAdapter();
        mLayoutManager = prepareLayoutManager();
    }

    @Override
    public void onStart() {
        super.onStart();
        configureSwipeRefresh();
        mPresenter.bindViews(this, mDataAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
        mPresenter.onStop();
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        if(menu instanceof MenuBuilder){
            MenuBuilder menuBuilder = (MenuBuilder) menu;
            menuBuilder.setOptionalIconsVisible(true);
        }

        mMenu = menu;
        mMenuInflater = inflater;

        mPresenter.onMenuCreated();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:
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

            default:
                super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    public abstract void setDefaultPageTitle();

    public void compileMenu() {
        if (null != mMenu) {
            clearMenu();

            addSearchView();
            addSortingMenuRoot();

            addSortByNameMenu();
            addSortByDateMenu();
        }
    }

    @Override
    public RecyclerView.ItemDecoration createItemDecoration(BasicViewMode viewMode) {
        return RecyclerViewUtils.createSimpleDividerItemDecoration(getContext(), R.drawable.simple_list_item_divider);
    }

    @Override
    public void setPageTitle(int titleId) {
        Activity activity = getActivity();
        if (null != activity)
            ((BasicMVP_Activity) activity).setPageTitle(titleId);
    }

    @Override
    public void setPageTitle(int titleId, Object... formatArguments) {
        Activity activity = getActivity();
        if (null != activity)
            ((BasicMVP_Activity) activity).setPageTitle(titleId, formatArguments);
    }

    @Override
    public void setPageTitle(String title) {
        Activity activity = getActivity();
        if (null != activity)
            ((BasicMVP_Activity) activity).setPageTitle(title);
    }

    @Override
    public void activateUpButton() {
        Activity activity = getActivity();
        if (null != activity)
            ((BasicMVP_Activity) activity).activateUpButton();
    }

    @Override
    public void setViewState(iBasicViewState viewState) {

    }

    @Override
    public void showToast(int messageId) {
        showToast(getString(messageId));
    }

    @Override
    public void showToast(String title) {
        PageUtils.showToast(getContext(), title);
    }


    @Override
    public Intent getInputIntent() {
        Activity activity = getActivity();
        return (null == activity) ? null : getActivity().getIntent();
    }

    @Override
    public void refreshMenu() {
        Activity activity = getActivity();
        if (null != activity)
            ((BasicMVP_Activity) getActivity()).refreshMenu();
    }

    @Override
    public void restoreSearchView(String filterText) {
        refreshMenu();

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
    public Context getAppContext() {
        // FIXME: исправить это. Только как?
        return getActivity().getApplicationContext();
    }

    @Override
    public Context getPageContext() {
        return getContext();
    }

    @Override
    public String getText(int stringResourceId, Object... formatArgs) {
        return TextUtils.getText(getContext(), stringResourceId, formatArgs);
    }

    @Override
    public void reconfigureRecyclerView() {

    }


    // Внутренние
    private void onBackPressed() {
        Activity activity = getActivity();
        if (null != activity)
            activity.onBackPressed();
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

    private void clearMenu() {
        if (null != mMenu)
            mMenu.clear();
    }

    private void addSearchView() {

        Activity activity = getActivity();
        if (null == activity)
            return;

        activity.getMenuInflater().inflate(R.menu.search_view, mMenu);

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
                .addReverseOrderActiveIcon(R.id.actionSortByNameReverse)
                .addDirectOrderActiveIcon(R.drawable.ic_menu_sort_by_name_direct_active)
                .addReverseOrderActiveIcon(R.drawable.ic_menu_sort_by_name_reverse_active)
                .addDirectOrderInactiveIcon(R.drawable.ic_menu_sort_by_name_direct)
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
                .addReverseOrderActiveIcon(R.id.actionSortByDateReverse)
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


    // ViewState
    protected void setNeutralViewState() {
        setDefaultPageTitle();

        hideRefreshThrobber();
        hideProgressMessage();

        compileMenu();
    }

    protected void setProgressViewState(Object data) {
        showMessage(data);
        showProgressBar();
    }

    protected void hideProgressMessage() {
        hideProgressBar();
        hideMessage();
    }

    protected void setRefreshViewState() {
        if (null != swipeRefreshLayout)
            swipeRefreshLayout.setRefreshing(true);
    }

    protected void hideRefreshThrobber() {
        if (null != swipeRefreshLayout)
            swipeRefreshLayout.setRefreshing(false);
    }

    protected void setErrorViewState(Object data) {
        setNeutralViewState();

        showMessage(data);
        setMessageColor(R.color.colorErrorText);
    }

    protected void showMessage(Object data) {
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

    protected void hideMessage() {
        ViewUtils.hide(messageView);
    }

    protected void setMessageColor(int colorId) {
        int color = getResources().getColor(colorId);
        messageView.setTextColor(color);
    }

    protected void showProgressBar() {
        ViewUtils.show(progressBar);
    }

    protected void hideProgressBar() {
        ViewUtils.hide(progressBar);
    }


    protected void setSelectionViewState(Object viewStateData) {
        showSelectionMenu();
        showSelectedItemsCount(viewStateData);
    }

    protected void setAllSelectedViewState(Object viewStateData) {
        showAllSelectedMenu();
        showSelectedItemsCount(viewStateData);
    }

    protected void showSelectionMenu() {
        clearMenu();
        mMenuInflater.inflate(R.menu.item_select_all, mMenu);
        mMenuInflater.inflate(R.menu.item_invert_selection, mMenu);
        mMenuInflater.inflate(R.menu.item_clear_selection, mMenu);
    }

    protected void showAllSelectedMenu() {
        clearMenu();
        mMenuInflater.inflate(R.menu.item_clear_selection, mMenu);
    }

    protected void showSelectedItemsCount(Object viewStateData) {
        setPageTitle(R.string.page_title_selected_items_count, viewStateData);
    }


}
