package ru.aakumykov.me.mvp.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.google.firebase.database.Exclude;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Card implements Parcelable {

    @Exclude
    private final static String TAG = "Card";

    private String key;
    private String type;
    private String title;
    private String quote;
    private String imageURL;
    private String description;
    private HashMap<String, Boolean> tags;

    public Card() {
    }

    public Card(String type, String title, String quote, String imageURL, String description, List<String> tagsList) {
        this.type = type;
        this.title = title;
        this.quote = quote;
        this.imageURL = imageURL;
        this.description = description;
        this.setTags(tagsList);
    }

    @Override
    public String toString() {
        return "Card { " +
                "key: "+getKey()+
                ", title: "+getTitle()+
                ", quote: "+getQuote()+
                ", imageURL: "+imageURL+
                ", description: "+getDescription()+
                ", tags: "+getTags()+
            ",}";
    }


    /* Parcelable */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // порядок заполнения важен
        dest.writeString(this.key);
        dest.writeString(this.type);
        dest.writeString(this.title);
        dest.writeString(this.quote);
        dest.writeString(this.imageURL);
        dest.writeString(this.description);
        dest.writeStringArray();
    }

    protected Card(Parcel in) {
        key = in.readString();
        type = in.readString();
        title = in.readString();
        quote = in.readString();
        imageURL = in.readString();
        description = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Card> CREATOR = new Creator<Card>() {
        @Override
        public Card createFromParcel(Parcel in) {
            return new Card(in);
        }

        @Override
        public Card[] newArray(int size) {
            return new Card[size];
        }
    };
    /* Parcelable */


    // Простые геттеры/сеттеры
    public String getKey() {
        return key;
    }
    public String getType() {
        return type;
    }
    public String getTitle() {
        return title;
    }
    public String getQuote() {
        return quote;
    }
    public String getImageURL() {
        return imageURL;
    }
    public String getDescription() {
        return description;
    }

    public void setKey(String key) {
        this.key = key;
    }
    public void setType(String type) {
        this.type = type;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public void setQuote(String quote) {
        this.quote = quote;
    }
    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }
    public void setDescription(String description) {
        this.description = description;
    }


    // Сложные геттеры/сеттеры
    public List<String> getTags() {
        List<String> list = new ArrayList<>(this.tags.keySet());
        String[] array = new String[list.size()];
        /* Вот эта сложная хуета с преобразованиями меток
        * требуется потому, что я передаю Карточку через Intent ... */
    }

    public void setTags(List<String> tagsList) {
        Log.d(TAG, "setTags(), "+tagsList);

        for (String tagName : tagsList) {
            this.tags.put(tagName, true);
        }
    }
}