package ru.aakumykov.me.sociocat.card_show2;

// counters/${ID}
public class Counter {
    private int numShards;

    public Counter(int numShards) {
        this.numShards = numShards;
    }

    public int getNumShards() {
        return numShards;
    }
}

