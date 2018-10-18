package ru.aakumykov.me.mvp.interfaces;

public interface MyInterfaces {

    interface DialogCallbacks {

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
}
