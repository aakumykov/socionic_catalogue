package ru.aakumykov.me.sociocat.interfaces;

public interface iDialogCallbacks {

    interface Delete {
        boolean deleteDialogCheck();
        void deleteDialogYes();
        void onDeleteDialogNo();
    }
}
