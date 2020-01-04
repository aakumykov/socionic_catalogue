package ru.aakumykov.me.sociocat.interfaces;

public interface iDialogCallbacks {

    interface YesNoCallbacks {
        boolean onCheck();
        void onYesAnswer();
        void onNoAnswer();
    }
}
