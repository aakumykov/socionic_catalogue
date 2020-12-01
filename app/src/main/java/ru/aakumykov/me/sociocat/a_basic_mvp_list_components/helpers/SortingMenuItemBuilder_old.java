package ru.aakumykov.me.sociocat.a_basic_mvp_list_components.helpers;


import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;

import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.enums.eSortingOrder;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.interfaces.iSortingMode;

public class SortingMenuItemBuilder_old {

    private final static String DIRECT_ARROW = "↓ ";
    private final static String REVERSE_ARROW = "↑ ";

    private MenuInflater mMenuInflater;
    private Menu mMenu;
    private int mMenuResourceId;
    private int mMenuItemId;
    private iSortingModeParamsCallback mParamsCallback;


    public SortingMenuItemBuilder_old addMenuInflater(MenuInflater inflater) {
        this.mMenuInflater = inflater;
        return this;
    }

    public SortingMenuItemBuilder_old addMenuRoot(Menu menu) {
        this.mMenu = menu;
        return this;
    }

    public SortingMenuItemBuilder_old addMenuResource(int menuResource) {
        this.mMenuResourceId = menuResource;
        return this;
    }

    public SortingMenuItemBuilder_old addMenuItemId(int menuItemId) {
        mMenuItemId = menuItemId;
        return this;
    }

    public SortingMenuItemBuilder_old addSortingModeParamsCallback(iSortingModeParamsCallback paramsCallback) {
        this.mParamsCallback = paramsCallback;
        return this;
    }

    public void buildMenuItem(iSortingMode targetSortingMode, eSortingOrder targetSortingOrder) {

        if (null == mMenuInflater || null == mMenu)
            return;

        // Пункт меню добавляется к заданному в качестве родительского.
        mMenuInflater.inflate(mMenuResourceId, mMenu);

        // Пункт меню "активируется" (отображается направление сортировки), если совпадает.
        if (mParamsCallback.isSortingModeComplains(targetSortingMode))
        {
            if (mParamsCallback.isSortingModeActive(targetSortingMode))
            {
                MenuItem menuItem = mMenu.findItem(mMenuItemId);
                if (null != menuItem)
                {
                    setSortingArrow(menuItem, targetSortingOrder.isDirect());
                }
            }
        }
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
}
