package io.gitlab.aakumykov.sociocat.utils.my_dialogs;

public interface iDialogCallbacks {

    interface YesNoCallbacks {
        boolean onCheck();
        void onYesAnswer();
        void onNoAnswer();
    }
}
