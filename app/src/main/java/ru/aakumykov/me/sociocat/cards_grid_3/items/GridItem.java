package ru.aakumykov.me.sociocat.cards_grid_3.items;

public class GridItem implements iGridItem {

    private Object payload;

    @Override
    public Object getPayload() {
        return payload;
    }

    @Override
    public void setPayload(Object payload) {
        this.payload = payload;
    }
}
