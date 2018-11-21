package ru.aakumykov.me.mvp.interfaces;

public interface iMyDialogs {

    interface YesCallback {
        boolean onCheckInDialog();
        void onYesInDialog();
    }

    interface NoCallback {
        void onNoInDialog();
    }

    interface CancelCallback {
        void onCancelInDialog();
    }


    interface StandardCallbacks extends YesCallback, NoCallback, CancelCallback {

    }


    interface Edit extends StringInputCallback {

    }

    interface Delete extends StandardCallbacks {

    }

    interface StringInputCallback {
        void onDialogWithStringYes(String text);
    }

}
