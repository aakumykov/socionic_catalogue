package ru.aakumykov.me.mvp.interfaces;

public interface iDialogCallbacks {

    interface onCheck {
        boolean doCheck();
    }

    interface onYes {
        void yesAction();
    }

    interface onNo {
        void noAction();
    }
}
