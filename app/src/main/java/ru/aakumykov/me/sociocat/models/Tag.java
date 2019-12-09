package ru.aakumykov.me.sociocat.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.IgnoreExtraProperties;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@IgnoreExtraProperties
public class Tag implements Parcelable {

    public static final String KEY_KEY = "key";
    public static final String KEY_NAME = "name";
    public static final String KEY_CARDS = "cards";

    private String key;
    private String name;
    private List<String> cards = new ArrayList<>();


    public Tag() {}

    public Tag(String name) {
        this.name = name;
        this.key = name;
    }


    @NotNull
    @Override
    public String toString() {
        return "Tag { key: "+getKey()+
                ", name: "+getName()+
                ", cards: "+getCards()+
                " }";
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("key", key);
        map.put("name", name);
        map.put("cards", cards);
        return map;
    }


    // Конверт
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
        dest.writeList(cards);
    }

    private Tag(Parcel in) {
        // важен порядок считывания
        key = in.readString();
        name = in.readString();
        cards.addAll( in.readArrayList(Tag.class.getClassLoader()) );
    }
    // Конверт


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

    public List<String> getCards() {
        return cards;
    }
    public void setCards(List<String> cards) {
        if (null != cards) {
            this.cards.addAll(cards);
        }

    }

    public void addCard(String cardKey) {
        this.cards.add(cardKey);
    }



    // Дополнительные
    @Exclude
    public int getCardsCount() {
        return this.cards.size();
    }
}
