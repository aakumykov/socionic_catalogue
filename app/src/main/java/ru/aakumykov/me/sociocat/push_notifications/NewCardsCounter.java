package ru.aakumykov.me.sociocat.push_notifications;

public class NewCardsCounter {

    private static volatile int newCardsCount = 0;


    public static void incrementCounter() {
        setCounter(newCardsCount + 1);
    }

    public static int getCount() {
        return newCardsCount;
    }

    public static void reset() {
        setCounter(0);
    }


    private synchronized static void setCounter(int newValue) {
        newCardsCount = newValue;
    }

    private NewCardsCounter() {}
}
