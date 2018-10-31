package ru.aakumykov.me.mvp.interfaces;

public interface iDialogCallbacks {

    interface Delete {
        boolean deleteDialogCheck();
        void deleteDialogYes();
        void onDeleteDialogNo();
    }
}
