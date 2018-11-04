package ru.aakumykov.me.mvp.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.List;

import ru.aakumykov.me.mvp.Constants;

public class Card implements Parcelable {

    private String key;
    private String type;
    private String title;
    private String quote;
    private String imageURL;
    private String description;
    private HashMap<String, Boolean> tags;

    public Card() {

    }

    public Card(String type, String title, String quote, String imageURL, String description,
                HashMap<String,Boolean> tagsMap
    ) {
        this.type = type;
        this.title = title;
        this.quote = quote;
        this.imageURL = imageURL;
        this.description = description;
        this.tags = tagsMap;
    }

    @Exclude
    @Override
    public String toString() {
        return "Card { key: "+getKey()+
                ", title: "+getTitle()+
                ", quote: "+getQuote()+
                ", imageURL: "+imageURL+
                ", description: "+getDescription()+
                ", tags: "+ getTags()+
            ",}";
    }

    @Exclude
    public HashMap<String, Object> toMap() {
        HashMap<String,Object> map = new HashMap<>();
         map.put("type", type);
         map.put("title", title);
         map.put("quote", quote);
         map.put("imageURL", imageURL);
         map.put("description", description);
         map.put("tags", tags);
        return map;
    }


    /* Parcelable */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // важен порядок заполнения
        dest.writeString(this.key);
        dest.writeString(this.type);
        dest.writeString(this.title);
        dest.writeString(this.quote);
        dest.writeString(this.imageURL);
        dest.writeString(this.description);
        dest.writeMap(this.tags);
    }

    protected Card(Parcel in) {
        // важен порядок считывания
        key = in.readString();
        type = in.readString();
        title = in.readString();
        quote = in.readString();
        imageURL = in.readString();
        description = in.readString();
        tags = (HashMap<String,Boolean>) in.readHashMap(HashMap.class.getClassLoader());
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
    public HashMap<String, Boolean> getTags() {
        return tags;
    }

    public void setKey(String key) {
        this.key = key;
    }
    public void setType(String type) throws IllegalArgumentException {
        if (!type.equals(Constants.TEXT_CARD) && !type.equals(Constants.IMAGE_CARD)) {
            throw new IllegalArgumentException("Unknown card type '"+type+"'");
        }
        this.type = type;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public void setQuote(String quote) {
        this.quote = quote;
    }
    public void setImageURL(String imageURL) {
        // TODO: проверять бы на корректность
        this.imageURL = imageURL;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public void setTags(HashMap<String, Boolean> tags) {
        this.tags = tags;
    }
}