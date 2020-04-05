package ru.aakumykov.me.sociocat.cards_list.filter_stuff;

import java.util.Comparator;

import ru.aakumykov.me.sociocat.cards_list.iCardsList;
import ru.aakumykov.me.sociocat.cards_list.list_items.DataItem;
import ru.aakumykov.me.sociocat.cards_list.list_items.ListItem;
import ru.aakumykov.me.sociocat.models.Card;

public class ItemsComparator implements Comparator {

    private iCardsList.SortingMode sortOrder;
    private ListItem item1;
    private ListItem item2;


    public ItemsComparator(iCardsList.SortingMode sortingMode) {
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
                return makeDateSorting();
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

    private int makeDateSorting() {
        long card1date = ((Card) ((DataItem) item1).getPayload()).getDate();
        long card2date = ((Card) ((DataItem) item2).getPayload()).getDate();

        switch (sortOrder) {
            case ORDER_COUNT_DIRECT:
                return Long.compare(card1date, card2date);
            case ORDER_COUNT_REVERSED:
                return Long.compare(card2date, card1date);
            default:
                return 0;
        }
    }
}
