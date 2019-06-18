package ru.aakumykov.me.sociocat.cards_grid_3.items;

public abstract class GridItem implements iGridItem {

    private Object mPayload;
    private boolean mIsPressed;

    @Override
    public Object getPayload() {
        return mPayload;
    }

    @Override
    public void setIsPressed(boolean value) {
        mIsPressed = value;
    }

    @Override
    public boolean isPressed() {
        return mIsPressed;
    }

    @Override
    public void setPayload(Object payload) {
        this.mPayload = payload;
    }
}
