package ru.aakumykov.me.sociocat.interfaces;

public interface iAuthStateListener {

    interface StateChangeCallbacks {
        void onLoggedIn();
        void onLoggedOut();
    }
}
