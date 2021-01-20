package io.gitlab.aakumykov.sociocat.a_basic_mvp_list_components.interfaces;

public interface iSearchViewListener {
    void onSearchViewCreated();
    void onSearchViewOpened();
    void onSearchViewClosed();
    void onSearchViewTextChanged(String pattern);
    void onSearchViewTextSubmitted(String pattern);
}
