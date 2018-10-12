package ru.aakumykov.me.mvp.models;

import com.google.firebase.database.DataSnapshot;

import java.util.HashMap;

public class Card {

    private String key;
    private String type;
    private String title;
    private String quote;
    private String ImageURL;
    private String description;

    public Card() {
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
        return ImageURL;
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
        ImageURL = imageURL;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Card {"+getKey()+", "+getTitle()+", "+getQuote()+", "+getDescription()+",}";
    }
}