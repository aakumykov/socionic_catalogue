package ru.aakumykov.me.sociocat.b_basic_mvp_components2.interfaces;

import ru.aakumykov.me.sociocat.b_basic_mvp_components2.view_holders.BasicMVP_DataViewHolder;

public interface iSelectionCommandsListener {
    void onSelectItemClicked(BasicMVP_DataViewHolder basicDataViewHolder);
    void onSelectAllClicked();
    void onClearSelectionClicked();
    void onInvertSelectionClicked();
}
