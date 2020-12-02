package ru.aakumykov.me.sociocat.utils;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class SortUtils {

    public static <T> List<T> sortList(List<T> inputList, Comparator<T>comparator) {
        return inputList.stream()
                .sorted(comparator)
                .collect(Collectors.toList());
    }

}
