package ru.aakumykov.me.sociocat.utils.my_dialogs;

public interface iDialogCallbacks {

    interface YesNoCallbacks {
        boolean onCheck();
        void onYesAnswer();
        void onNoAnswer();
    }
}
