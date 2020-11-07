package ru.aakumykov.me.sociocat.a_basic_mvp_components.interfaces;

public interface iSearchViewListener {
    void onSearchViewCreated();
    void onSearchViewOpened();
    void onSearchViewClosed();
    void onSearchViewTextChanged(String pattern);
    void onSearchViewTextSubmitted(String pattern);
}
