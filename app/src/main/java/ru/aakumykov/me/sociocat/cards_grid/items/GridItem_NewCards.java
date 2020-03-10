package ru.aakumykov.me.sociocat.cards_grid.items;

public class GridItem_NewCards extends GridItem {
    private int count = 0;

    public GridItem_NewCards(int count) {
        this.count = count;
    }

    public int getCount() {
        return count;
    }
}
