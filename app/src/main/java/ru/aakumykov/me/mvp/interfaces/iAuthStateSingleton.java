package ru.aakumykov.me.mvp.interfaces;

public interface iAuthStateSingleton {

    void registerListener(iAuthStateSingletonCallbacks callbacks);
    void unregiserListener();

    interface iAuthStateSingletonCallbacks {
        void onAuthIn();
        void onAuthOut();
    }
}
