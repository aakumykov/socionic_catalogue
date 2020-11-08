package ru.aakumykov.me.sociocat.a_basic_mvp_components.interfaces;


import ru.aakumykov.me.sociocat.a_basic_mvp_components.view_holders.BasicMVP_DataViewHolder;

public interface iSelectionCommandsListener {
    void onSelectItemClicked(BasicMVP_DataViewHolder basicDataViewHolder);
    void onSelectAllClicked();
    void onClearSelectionClicked();
    void onInvertSelectionClicked();
}
