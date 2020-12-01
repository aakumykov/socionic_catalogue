package ru.aakumykov.me.sociocat.a_basic_mvp_list_components.helpers;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;

import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.enums.eSortingOrder;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.interfaces.iSortingMode;

public class SortingMenuItemCreator {

    private final static String DIRECT_ARROW = "↓ ";
    private final static String REVERSE_ARROW = "↑ ";

    private MenuInflater mMenuInflater;
    private Menu mRootMenu;
    private int mMenuResource;
    private int mMenuItemId;
    private iSortingMode mSortingMode;
    private eSortingOrder mSortingOrder;
    private SortingMenuItemCreator.iSortingModeParamsCallback mParamsCallback;


    public static class Builder {

        private final MenuInflater mMenuInflater;
        private Menu mRootMenu = null;
        private int mMenuResource = -1;
        private int mMenuItemId = -1;
        private iSortingMode mSortingMode = null;
        private eSortingOrder mSortingOrder = null;
        private SortingMenuItemCreator.iSortingModeParamsCallback mParamsCallback = null;


        public Builder(MenuInflater menuInflater) {
            mMenuInflater = menuInflater;
        }

        public Builder setRootMenu(Menu menu) {
            mRootMenu = menu;
            return this;
        }

        public Builder addInflatedMenuResource(int menuResource) {
            mMenuResource = menuResource;
            return this;
        }

        public Builder addMenuItemId(int menuItemId) {
            mMenuItemId = menuItemId;
            return this;
        }

        public Builder addSortingModeParamsCallback(SortingMenuItemCreator.iSortingModeParamsCallback callback) {
            mParamsCallback = callback;
            return this;
        }

        public Builder addSortingMode(iSortingMode sortingMode) {
            mSortingMode = sortingMode;
            return this;
        }

        public Builder addSortingOrder(eSortingOrder sortingOrder) {
            mSortingOrder = sortingOrder;
            return this;
        }


        public void createMenuItem() {
            new SortingMenuItemCreator(this).create();
        }
    }


    private void create() {

        if (null == mMenuInflater || null == mRootMenu)
            return;

        // Пункт меню добавляется к заданному в качестве родительского.
        mMenuInflater.inflate(mMenuResource, mRootMenu);

        // Пункт меню "активируется" (отображается направление сортировки), если совпадает.
        if (mParamsCallback.isSortingModeComplains(mSortingMode))
        {
            if (mParamsCallback.isSortingModeActive(mSortingMode))
            {
                MenuItem menuItem = mRootMenu.findItem(mMenuItemId);
                if (null != menuItem)
                {
                    setSortingArrow(menuItem, mSortingOrder.isDirect());
                }
            }
        }
    }

    private SortingMenuItemCreator(Builder builder) throws RuntimeException {

        mRootMenu = builder.mRootMenu;
        if (null == mRootMenu)
            throw new SortingMenuItemBuilder_Exception("Вы должны добавить корневой элемент меню для создаваемого пункта меню");

        mMenuResource = builder.mMenuResource;
        if (-1 == mMenuResource)
            throw new SortingMenuItemBuilder_Exception("Вы должны добавить ресурс разметки меню");

        mMenuItemId = builder.mMenuItemId;
        if (-1 == mMenuItemId)
            throw new SortingMenuItemBuilder_Exception("Вы должны указать id создаваемого пункта меню");

        mParamsCallback = builder.mParamsCallback;
        if (null == mParamsCallback)
            throw new SortingMenuItemBuilder_Exception("Вы должны добавить объект SortingMenuItemCreator.iSortingModeParamsCallback");

        mSortingMode = builder.mSortingMode;
        if (null == mSortingMode)
            throw new SortingMenuItemBuilder_Exception("Вы должны добавить SortingMode");

        mSortingOrder = builder.mSortingOrder;
        if (null == mSortingOrder)
            throw new SortingMenuItemBuilder_Exception("Вы должны добавить SortingOrder");
    }


    private void setSortingArrow(@NonNull MenuItem menuItem, boolean isDirectOrder) {

        String title = menuItem.getTitle().toString();

        if (isDirectOrder) {
            title = title.replace(REVERSE_ARROW, "");
            title = DIRECT_ARROW + title;
        }
        else {
            title = title.replace(DIRECT_ARROW, "");
            title = REVERSE_ARROW + title;
        }

        menuItem.setTitle(title);
    }


    public interface iSortingModeParamsCallback {
        boolean isSortingModeComplains(iSortingMode sortingMode);
        boolean isSortingModeActive(iSortingMode sortingMode);
    }

    public static class SortingMenuItemBuilder_Exception extends RuntimeException {
        public SortingMenuItemBuilder_Exception(String message) {
            super(message);
        }
    }
}
