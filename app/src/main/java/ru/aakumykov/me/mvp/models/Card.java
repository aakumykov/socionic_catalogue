package ru.aakumykov.me.mvp.models;

import com.google.firebase.database.DataSnapshot;

import java.util.HashMap;

public class Card {

    private String title;
    private String quote;
    private String description;
    private String key;

    public Card() {
    }

    public Card(String key, String title, String quote, String description) {
        this.key = key;
        this.title = title;
        this.quote = quote;
        this.description = description;
    }

    public String getTitle() {
        return title;
    }
    public String getQuote() {
        return quote;
    }
    public String getDescription() {
        return description;
    }
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public void setQuote(String quote) {
        this.quote = quote;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Card {"+getKey()+", "+getTitle()+", "+getQuote()+", "+getDescription()+",}";
    }
}