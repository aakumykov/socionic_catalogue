package ru.aakumykov.me.sociocat.template_of_list.model;

import androidx.annotation.NonNull;

public class Item {

    private String name;
    private int count;

    public Item(String name, int count) {
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

        stringBuilder.append(Item.class.getSimpleName());
        stringBuilder.append(" {");
        stringBuilder.append(" name: ");
        stringBuilder.append(name);
        stringBuilder.append(", ");
        stringBuilder.append("count: ");
        stringBuilder.append(count);
        stringBuilder.append(" }");

        return stringBuilder.toString();
    }
}
