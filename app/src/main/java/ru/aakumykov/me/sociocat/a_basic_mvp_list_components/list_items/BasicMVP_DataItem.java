package ru.aakumykov.me.sociocat.a_basic_mvp_list_components.list_items;


import androidx.annotation.NonNull;

import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.interfaces.iSortableData;

public abstract class BasicMVP_DataItem extends BasicMVP_ListItem {

    // Своейства
    private boolean isSelected = false;
    private boolean isHighLighted = false;
    private Object payload;


    // Геттеры, сеттеры
    public Object getPayload() {
        return payload;
    }
    public void setPayload(Object payload) {
        this.payload = payload;
    }

    public void setSelected(boolean selected) {
        this.isSelected = selected;
    }
    public boolean isSelected() {
        return this.isSelected;
    }

    public void setHighLighted(boolean highLighted) {
        isHighLighted = highLighted;
    }
    public boolean isHighLighted() {
        return this.isHighLighted;
    }

    public iSortableData getBasicData() {
        return (iSortableData) getPayload();
    }

    // Абстрактные классы
    public abstract String getTitle();

    @NonNull
    @Override
    public abstract String toString();
}
