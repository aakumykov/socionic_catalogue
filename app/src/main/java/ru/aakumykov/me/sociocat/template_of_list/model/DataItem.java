package ru.aakumykov.me.sociocat.template_of_list.model;

import androidx.annotation.NonNull;

public class DataItem extends ListItem {

    private String name;
    private int count;

    public DataItem(String name, int count) {
        this.name = name;
        this.count = count;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @NonNull @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(DataItem.class.getSimpleName());
        stringBuilder.append(" {");
        stringBuilder.append(" name: ");
        stringBuilder.append(name);
        stringBuilder.append(", ");
        stringBuilder.append("count: ");
        stringBuilder.append(count);
        stringBuilder.append(" }");

        return stringBuilder.toString();
    }

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
}
