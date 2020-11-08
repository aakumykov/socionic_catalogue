package ru.aakumykov.me.sociocat.a_basic_mvp_components.list_Items;

import androidx.annotation.NonNull;

import ru.aakumykov.me.sociocat.a_basic_mvp_components.interfaces.iBasicData;


public abstract class BasicMVP_DataItem extends BasicMVP_ListItem {

    // Своейства
    private boolean isSelected = false;
    private Object payload;


    // Геттеры, сеттеры
    public Object getPayload() {
        return payload;
    }
    public void setPayload(Object payload) {
        this.payload = payload;
    }

    public boolean isSelected() {
        return this.isSelected;
    }
    public void setSelected(boolean selected) {
        this.isSelected = selected;
    }

    public iBasicData getBasicData() {
        return (iBasicData) getPayload();
    }

    // Абстрактные классы
    public abstract String getTitle();

    @NonNull @Override
    public abstract String toString();
}
