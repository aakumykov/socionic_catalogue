package ru.aakumykov.me.sociocat.a_basic_mvp_components.interfaces;


import ru.aakumykov.me.sociocat.a_basic_mvp_components.view_holders.Basic_DataViewHolder;

public interface iSelectionCommandsListener {
    void onSelectItemClicked(Basic_DataViewHolder basicDataViewHolder);
    void onSelectAllClicked();
    void onClearSelectionClicked();
    void onInvertSelectionClicked();
}
