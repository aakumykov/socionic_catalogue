package ru.aakumykov.me.sociocat.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.Exclude;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Tag implements Parcelable {

    private String key;
    private String name;private HashMap<String,Boolean> cards = new HashMap<>();
    private Integer counter = 0;

    public Tag() {}

    public Tag(String name, final HashMap<String,Boolean> cards) {
        this.key = name;
        this.name = name;
        this.cards = cards;
        this.counter = cards.size();
    }

    @Override
    public String toString() {
        return "Tag { key: "+key+", name: "+name+", cards: "+cards+", counter: "+counter+" }";
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("key", key);
        map.put("name", name);
        map.put("cards", cards);
        map.put("counter", counter);
        return map;
    }


    /* Parcelable */
    public static final Creator<Tag> CREATOR = new Creator<Tag>() {
        @Override
        public Tag createFromParcel(Parcel in) {
            return new Tag(in);
        }

        @Override
        public Tag[] newArray(int size) {
            return new Tag[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // важен порядок заполнения
        dest.writeString(key);
        dest.writeString(name);
        dest.writeMap(cards);
        dest.writeInt(counter);
    }

    private Tag(Parcel in) {
        // важен порядок считывания
        key = in.readString();
        name = in.readString();
        cards = (HashMap<String,Boolean>) in.readHashMap(HashMap.class.getClassLoader());
        counter = in.readInt();
    }
    /* Parcelable */


    // Геттеры и сеттеры
    public String getKey() {
        return key;
    }
    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public HashMap<String, Boolean> getCards() {
        return cards;
    }
    public void setCards(List<String> cards) {
        for (String cardKey : cards)
            this.cards.put(cardKey, true);

    }

    public Integer getCounter() {
        return counter;
    }
    public void setCounter(Integer counter) {
        this.counter = counter;
    }


    // Дополнительные
    @Exclude public int getCardsCount() {
        return getCards().size();
    }
}
