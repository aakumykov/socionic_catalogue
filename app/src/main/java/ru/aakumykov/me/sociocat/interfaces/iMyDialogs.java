package ru.aakumykov.me.sociocat.interfaces;

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
        String onPrepareText();
        String onYesClicked(String text);
        void onSuccess(String inputtedString);
    }

}
