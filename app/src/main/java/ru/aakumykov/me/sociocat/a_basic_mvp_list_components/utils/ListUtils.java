package ru.aakumykov.me.sociocat.a_basic_mvp_list_components.utils;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.list_items.BasicMVP_DataItem;
import ru.aakumykov.me.sociocat.a_basic_mvp_list_components.list_items.BasicMVP_ListItem;

public class ListUtils {

    public static <T> List<T> listDiff(List<T> list1, List<T> list2) {
        Set<T> set1 = new HashSet<>(list1);
        set1.removeAll(new HashSet<>(list2));
        return new ArrayList<>(set1);
    }

    public static <T> List<BasicMVP_ListItem> incapsulateObjects2basicItemsList(List<T> inputList, iIncapsulationCallback callback) {
        List<BasicMVP_ListItem> outputList = new ArrayList<>();
        for (Object object : inputList)
            outputList.add(callback.createDataItem(object));
        return outputList;
    }


    public interface iIncapsulationCallback {
        BasicMVP_DataItem createDataItem(Object payload);
    }
}
