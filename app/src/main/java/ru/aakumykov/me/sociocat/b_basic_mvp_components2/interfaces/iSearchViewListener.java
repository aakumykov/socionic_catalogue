package ru.aakumykov.me.sociocat.b_basic_mvp_components2.interfaces;

public interface iSearchViewListener {
    void onSearchViewCreated();
    void onSearchViewOpened();
    void onSearchViewClosed();
    void onSearchViewTextChanged(String pattern);
    void onSearchViewTextSubmitted(String pattern);
}
