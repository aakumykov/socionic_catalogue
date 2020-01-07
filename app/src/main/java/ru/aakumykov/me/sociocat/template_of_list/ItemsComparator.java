package ru.aakumykov.me.sociocat.template_of_list;

import java.util.Comparator;

import ru.aakumykov.me.sociocat.template_of_list.model.Item;

public class ItemsComparator implements Comparator {

    private iItemsList.SortingMode sortOrder;
    private Item item1;
    private Item item2;


    public ItemsComparator(iItemsList.SortingMode sortingMode) {
        this.sortOrder = sortingMode;
    }

    @Override
    public int compare(Object o1, Object o2) {
        if (o1 instanceof Item && o2 instanceof Item) {
            this.item1 = ((Item) o1);
            this.item2 = ((Item) o2);
            return performSorting(item1, item2);
        }
        else {
            return 0;
        }
    }

    private int performSorting(Item tag1, Item tag2) {
        switch (sortOrder) {
            case ORDER_NAME_DIRECT:
            case ORDER_NAME_REVERSED:
                return makeNameSorting();
            case ORDER_COUNT_DIRECT:
            case ORDER_COUNT_REVERSED:
                return nameCardsCountSorting();
            default:
                return 0;
        }
    }

    private int makeNameSorting() {
        String name1 = item1.getName();
        String name2 = item2.getName();

        switch (sortOrder) {
            case ORDER_NAME_DIRECT:
                return name1.compareTo(name2);
            case ORDER_NAME_REVERSED:
                return name2.compareTo(name1);
            default:
                return 0;
        }
    }

    private int nameCardsCountSorting() {
        int cardsCount1 = item1.getCount();
        int cardsCount2 = item2.getCount();

        switch (sortOrder) {
            case ORDER_COUNT_DIRECT:
                return Integer.compare(cardsCount1, cardsCount2);
            case ORDER_COUNT_REVERSED:
                return Integer.compare(cardsCount2, cardsCount1);
            default:
                return 0;
        }
    }
}
