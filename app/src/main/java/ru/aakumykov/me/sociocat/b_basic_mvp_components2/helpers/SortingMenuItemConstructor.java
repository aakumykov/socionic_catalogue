package ru.aakumykov.me.sociocat.b_basic_mvp_components2.helpers;


import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import ru.aakumykov.me.sociocat.b_basic_mvp_components2.enums.eSortingOrder;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.interfaces.iSortingMode;

public class SortingMenuItemConstructor {

    public interface iSortingModeParamsCallback {
        boolean isSortingModeComplains(iSortingMode sortingMode);
        boolean isSortingModeActive(iSortingMode sortingMode);
        boolean isDirectOrder(eSortingOrder sortingOrder);
    }

    private MenuInflater mMenuInflater;
    private Menu mMenu;
    private int menuResourceId;
    private int directOrderMenuItemId;
    private int reverseOrderMenuItemId;
    private int directOrderActiveIconId;
    private int reverseOrderActiveIconId;
    private int directOrderInactiveIconId;
    private iSortingModeParamsCallback paramsCallback;


    public SortingMenuItemConstructor addMenuInflater(MenuInflater inflater) {
        this.mMenuInflater = inflater;
        return this;
    }

    public SortingMenuItemConstructor addTargetMenu(Menu menu) {
        this.mMenu = menu;
        return this;
    }

    public SortingMenuItemConstructor addMenuResource(int menuResource) {
        this.menuResourceId = menuResource;
        return this;
    }

    public SortingMenuItemConstructor addDirectOrderMenuItemId(int itemId) {
        this.directOrderMenuItemId = itemId;
        return this;
    }

    public SortingMenuItemConstructor addReverseOrderMenuItemId(int itemId) {
        this.reverseOrderMenuItemId = itemId;
        return this;
    }
    
    public SortingMenuItemConstructor addDirectOrderActiveIcon(int iconId) {
        this.directOrderActiveIconId = iconId;
        return this;
    }

    public SortingMenuItemConstructor addReverseOrderActiveIcon(int iconId) {
        this.reverseOrderActiveIconId = iconId;
        return this;
    }

    public SortingMenuItemConstructor addDirectOrderInactiveIcon(int iconId) {
        this.directOrderInactiveIconId = iconId;
        return this;
    }
    
    public SortingMenuItemConstructor addSortingModeParamsCallback(iSortingModeParamsCallback paramsCallback) {
        this.paramsCallback = paramsCallback;
        return this;
    }

    public void makeMenuItem(iSortingMode targetSortingMode, eSortingOrder targetSortingOrder) {

        if (null == mMenuInflater || null == mMenu)
            return;

        mMenuInflater.inflate(menuResourceId, mMenu);

        if (paramsCallback.isSortingModeComplains(targetSortingMode))
        {
            if (paramsCallback.isSortingModeActive(targetSortingMode))
            {
                if (paramsCallback.isDirectOrder(targetSortingOrder))
                {
                    removeMenuItem(reverseOrderMenuItemId);
                    setMenuItemIcon(directOrderMenuItemId, directOrderActiveIconId);
                    return;
                }
                else {
                    removeMenuItem(directOrderMenuItemId);
                    setMenuItemIcon(reverseOrderMenuItemId, reverseOrderActiveIconId);
                    return;
                }
            }
        }

        removeMenuItem(reverseOrderMenuItemId);
        setMenuItemIcon(directOrderMenuItemId, directOrderInactiveIconId);
    }

    private void setMenuItemIcon(int itemId, int iconResource) {
        MenuItem menuItem = mMenu.findItem(itemId);
        if (null != menuItem)
            menuItem.setIcon(iconResource);
    }

    private void removeMenuItem(int itemId) {
        mMenu.removeItem(itemId);
    }
}
