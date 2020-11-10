package ru.aakumykov.me.sociocat.cards_list.list_items;

import androidx.annotation.NonNull;

import ru.aakumykov.me.sociocat.cards_list.iCardsList;
import ru.aakumykov.me.sociocat.iListPayload;
import ru.aakumykov.me.sociocat.models.Card;

public class DataItem<T extends iListPayload> extends ListItem {

    private String name;
    private int count;
    private Object payload;
    private boolean isNowDeleting = false;

    public DataItem() {

    }

    public DataItem(@NonNull Card card) {
        setPayload(card);
    }

    public DataItem(String name, int count) {
        this.name = name;
        this.count = count;
    }

    public String getName() {
        return ((T) payload).getTitle();
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCount() {
        return ((T) payload).getCount();
    }

    public void setCount(int count) {
        this.count = count;
    }

    public Object getPayload() {
        return payload;
    }

    public void setPayload(Object payload) {
        this.payload = payload;
    }

    // Object
    @NonNull @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(DataItem.class.getSimpleName());
        stringBuilder.append(" {");
        stringBuilder.append(" name: ");
        stringBuilder.append(getName());
        stringBuilder.append(", ");
        stringBuilder.append("count: ");
        stringBuilder.append(getCount());
        stringBuilder.append(" }");

        return stringBuilder.toString();
    }


    // ListItem
    @Override
    public int compareTo(ListItem listItem) {
        if (listItem instanceof LoadMoreItem)
            return -1;

        if (listItem instanceof DataItem) {
            String name1 = this.getName();
            String name2 = ((DataItem) listItem).getName();
            return name1.compareTo(name2);
        }

        return 0;
    }

    @Override
    public iCardsList.ItemType getItemType() {
        return iCardsList.ItemType.DATA_ITEM;
    }

    @Override
    public void setIsNowDeleting(boolean value) {
        this.isNowDeleting = value;
    }

    @Override
    public boolean isNowDeleting() {
        return this.isNowDeleting;
    }

}
