package ru.aakumykov.me.sociocat.tags_lsit3;

import java.util.Comparator;

import ru.aakumykov.me.sociocat.models.Tag;

public class TagsComparator implements Comparator {

    private iTagsList3.SortOrder sortOrder;
    private Tag tag1;
    private Tag tag2;


    public TagsComparator(iTagsList3.SortOrder sortOrder) {
        this.sortOrder = sortOrder;
    }

    @Override
    public int compare(Object o1, Object o2) {
        if (o1 instanceof Tag && o2 instanceof Tag) {
            this.tag1 = ((Tag) o1);
            this.tag2 = ((Tag) o2);
            return performSorting(tag1, tag2);
        }
        else {
            return 0;
        }
    }

    private int performSorting(Tag tag1, Tag tag2) {
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
        String name1 = tag1.getName();
        String name2 = tag2.getName();

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
        int cardsCount1 = tag1.getCards().size();
        int cardsCount2 = tag2.getCards().size();

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
