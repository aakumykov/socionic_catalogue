package ru.aakumykov.me.mvp.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Card implements Parcelable, Cloneable {

    private String key;
    private String type;
    private String title;
    private String quote;
    private String imageURL;
    private String description;

    public Card() {
    }

    public Card(String key, String type, String title, String quote, String imageURL, String description) {
        this.key = key;
        this.type = type;
        this.title = title;
        this.quote = quote;
        this.imageURL = imageURL;
        this.description = description;
    }

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

    @Override
    public String toString() {
        return "Card { key: "+getKey()+", title: "+getTitle()+", quote: "+getQuote()+", imageURL: "+imageURL+", description: "+getDescription()+",}";
    }

    @Override
    public Card clone() throws CloneNotSupportedException {
        super.clone();

        Card theClone = new Card();

        theClone.setKey(getKey());
        theClone.setType(getType());

        theClone.setTitle(getTitle());
        theClone.setQuote(getQuote());
        theClone.setImageURL(getImageURL());
        theClone.setDescription(getDescription());

        return theClone;
    }

    /* Parcelable */
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

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // порядок заполнения важен
        dest.writeString(this.key);
        dest.writeString(this.type);
        dest.writeString(this.title);
        dest.writeString(this.quote);
        dest.writeString(this.imageURL);
        dest.writeString(this.description);
    }
    /* Parcelable */
}