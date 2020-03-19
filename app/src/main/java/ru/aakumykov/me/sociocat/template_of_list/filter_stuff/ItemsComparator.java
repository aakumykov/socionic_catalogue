package ru.aakumykov.me.sociocat.template_of_list.filter_stuff;

import java.util.Comparator;

import ru.aakumykov.me.sociocat.template_of_list.iItemsList;
import ru.aakumykov.me.sociocat.template_of_list.model.DataItem;
import ru.aakumykov.me.sociocat.template_of_list.model.ListItem;

public class ItemsComparator implements Comparator {

    private iItemsList.SortingMode sortOrder;
    private ListItem item1;
    private ListItem item2;


    public ItemsComparator(iItemsList.SortingMode sortingMode) {
        this.sortOrder = sortingMode;
    }

    @Override
    public int compare(Object o1, Object o2) {
        if (o1 instanceof DataItem && o2 instanceof DataItem) {
            this.item1 = ((DataItem) o1);
            this.item2 = ((DataItem) o2);
            return performSorting();
        }
        else {
            return 0;
        }
    }

    private int performSorting() {
        switch (sortOrder) {
            case ORDER_NAME_DIRECT:
            case ORDER_NAME_REVERSED:
                return makeNameSorting();
            case ORDER_COUNT_DIRECT:
            case ORDER_COUNT_REVERSED:
                return makeCountSorting();
            default:
                return 0;
        }
    }

    private int makeNameSorting() {

        switch (sortOrder) {
            case ORDER_NAME_DIRECT:
                return item1.compareTo(item2);
            case ORDER_NAME_REVERSED:
                return item2.compareTo(item1);
            default:
                return 0;
        }
    }

    private int makeCountSorting() {
        int cardsCount1 = ((DataItem) item1).getCount();
        int cardsCount2 = ((DataItem) item2).getCount();

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
