package ru.aakumykov.me.mvp.interfaces;

public interface iAuthStateListener {

    interface StateChangeCallbacks {
        void processLogin();
        void processLogout();
    }
}
