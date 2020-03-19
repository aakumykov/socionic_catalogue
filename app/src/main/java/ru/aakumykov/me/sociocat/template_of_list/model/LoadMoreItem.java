package ru.aakumykov.me.sociocat.template_of_list.model;

public class LoadMoreItem extends ListItem {

    @Override
    public int compareTo(ListItem listItem) {
        if (listItem instanceof DataItem)
            return -1;
        else
            return 1;
    }
}
