package ru.aakumykov.me.mvp.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.Exclude;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Card implements Parcelable {

    private String key;
    private String type;
    private String title;
    private String quote;
    private String imageURL;
    private String description;
    private List<String> tags = new ArrayList<>();
    private HashMap<String, Boolean> tags2;

    public Card() {
    }

    public Card(String type, String title, String quote, String imageURL, String description,
                List<String> tagsList,
                HashMap<String,Boolean> tagsList2
    ) {
        this.type = type;
        this.title = title;
        this.quote = quote;
        this.imageURL = imageURL;
        this.description = description;
        this.tags = tagsList;
        this.tags2 = tagsList2;
    }

    @Exclude
    @Override
    public String toString() {
        return "Card { key: "+getKey()+
                ", title: "+getTitle()+
                ", quote: "+getQuote()+
                ", imageURL: "+imageURL+
                ", description: "+getDescription()+
                ", tags: "+getTags()+
                ", tags2: "+getTags2()+
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
         map.put("tags2", tags2);
        return map;
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
        dest.writeStringList(this.tags);
        dest.writeMap(this.tags2);
    }

    protected Card(Parcel in) {
        key = in.readString();
        type = in.readString();
        title = in.readString();
        quote = in.readString();
        imageURL = in.readString();
        description = in.readString();
        in.readStringList(this.tags);
        tags2 = (HashMap<String,Boolean>) in.readHashMap(HashMap.class.getClassLoader());
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
    public List<String> getTags() { return this.tags; }

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
    public void setTags(List<String> tagsList) { this.tags = tagsList; }

    public HashMap<String, Boolean> getTags2() {
        return tags2;
    }

    public void setTags2(HashMap<String, Boolean> tags2) {
        this.tags2 = tags2;
    }
}