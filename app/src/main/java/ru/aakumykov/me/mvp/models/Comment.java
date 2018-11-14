package ru.aakumykov.me.mvp.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class Comment implements Parcelable {

    public final static int key_commentId = 10;
    public final static String key_createdAt = "createdAt";
    public final static String key_cardId = "cardId";

    private String key;
    private String text;

    private String cardId;
    private String parentId;
    private String userId;
    private String createdAt;
    private String editedAt;
    private int rating = 0;
    
    public Comment(){}
    
    public Comment(String text, String cardId, String parentId, String userId) {
        this.text = text;
        this.cardId = cardId;
        this.parentId = parentId;
        this.userId = userId;
    }
    

    @Override @Exclude
    public String toString() {
        return "Comment { "
                +"key: "+key
                +", text: "+text
                +", cardId: "+cardId
                +", parentId: "+parentId
                +", userId: "+userId
                +", createdAt: "+createdAt
                +", editedAt: "+editedAt
                +", rating: "+rating
            +" }";
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> map = new HashMap<>();
            map.put("key", key);
            map.put("text", text);
            map.put("cardId", cardId);
            map.put("parentId", parentId);
            map.put("userId", userId);
            map.put("createdAt", createdAt);
            map.put("editedAt", editedAt);
            map.put("rating", rating);
        return map;
    }


    /* Parcelable */
    public static final Creator<Comment> CREATOR = new Creator<Comment>() {
        @Override
        public Comment createFromParcel(Parcel in) {
            return new Comment(in);
        }

        @Override
        public Comment[] newArray(int size) {
            return new Comment[size];
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
        dest.writeString(text);
        dest.writeString(cardId);
        dest.writeString(parentId);
        dest.writeString(userId);
        dest.writeString(createdAt);
        dest.writeString(editedAt);
        dest.writeInt(rating);
    }

    private Comment(Parcel in) {
        // важен порядок считывания
        key = in.readString();
        text = in.readString();
        cardId = in.readString();
        parentId = in.readString();
        userId = in.readString();
        createdAt = in.readString();
        editedAt = in.readString();
        rating = in.readInt();
    }
    /* Parcelable */


    // Геттеры и сеттеры
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getEditedAt() {
        return editedAt;
    }

    public void setEditedAt(String editedAt) {
        this.editedAt = editedAt;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }
}
