package ru.aakumykov.me.sociocat.b_basic_mvp_components2.helpers;


import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;

import ru.aakumykov.me.sociocat.b_basic_mvp_components2.enums.eSortingOrder;
import ru.aakumykov.me.sociocat.b_basic_mvp_components2.interfaces.iSortingMode;

public class SortingMenuItemConstructor2 {

    public interface iSortingModeParamsCallback {
        boolean isSortingModeComplains(iSortingMode sortingMode);
        boolean isSortingModeActive(iSortingMode sortingMode);
        boolean isDirectOrder(eSortingOrder sortingOrder);
    }

    private MenuInflater mMenuInflater;
    private Menu mMenu;
    private int menuResourceId;
    private int menuItemId;
    private int menuIconId;
    private iSortingModeParamsCallback paramsCallback;


    public SortingMenuItemConstructor2 addMenuInflater(MenuInflater inflater) {
        this.mMenuInflater = inflater;
        return this;
    }

    public SortingMenuItemConstructor2 addTargetMenu(Menu menu) {
        this.mMenu = menu;
        return this;
    }

    public SortingMenuItemConstructor2 addMenuResource(int menuResource) {
        this.menuResourceId = menuResource;
        return this;
    }

    public SortingMenuItemConstructor2 addSortingModeParamsCallback(iSortingModeParamsCallback paramsCallback) {
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
                MenuItem menuItem = mMenu.findItem(menuItemId);
                if (null != menuItem)
                {
                    setSortingArrow(menuItem, paramsCallback.isDirectOrder(targetSortingOrder));
                }
            }
        }
    }

    private void setSortingArrow(@NonNull MenuItem menuItem, boolean isDirectOrder) {
        String reverseArrow = "↑";
        String directArrow = "↓";

        String title = menuItem.getTitle().toString();

        if (isDirectOrder) {
            title = title.replace(reverseArrow, "");
            title = directArrow + title;
        }
        else {
            title = title.replace(directArrow, "");
            title = reverseArrow + title;
        }

        menuItem.setTitle(title);
    }
}
