package ru.aakumykov.me.mvp.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.Exclude;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Comment implements Parcelable {

    public final static int key_commentId = 10;
    public final static String key_createdAt = "createdAt";
    public final static String key_cardId = "cardId";

    private String key;
    private String text;

    private String cardId;
    private String parentId;
    private String parentText;
    private String userId;
    private String userName;
    private String userAvatar;
    private String createdAt;
    private String editedAt;
    private Integer rating = 0;
    private List<String> rateUpList = new ArrayList<>();
    private List<String> rateDownList = new ArrayList<>();
    
    public Comment(){}
    
    public Comment(String text, String cardId, String parentId,
                   String parentText, String userId, String userName, String userAvatar) {
        this.text = text;
        this.cardId = cardId;
        this.parentId = parentId;
        this.parentText = parentText;
        this.userId = userId;
        this.userName = userName;
        this.userAvatar = userAvatar;
        this.rating = 0;
        this.rateUpList = new ArrayList<>();
        this.rateDownList = new ArrayList<>();
    }
    

    @Override @Exclude
    public String toString() {
        return "Comment { "
                +"key: "+key
                +", text: "+text
                +", cardId: "+cardId
                +", parentId: "+parentId
                +", parentText: "+parentText
                +", userId: "+userId
                +", userName: "+userName
                +", userAvatar: "+userAvatar
                +", createdAt: "+createdAt
                +", editedAt: "+editedAt
                +", rating: "+rating
                +", rateUpList: "+rateUpList
                +", rateDownList: "+rateDownList
            +" }";
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> map = new HashMap<>();
            map.put("key", key);
            map.put("text", text);
            map.put("cardId", cardId);
            map.put("parentId", parentId);
            map.put("parentText", parentText);
            map.put("userId", userId);
            map.put("userName", userName);
            map.put("userAvatar", userAvatar);
            map.put("createdAt", createdAt);
            map.put("editedAt", editedAt);
            map.put("rating", rating);
            map.put("rateUpList", rateUpList);
            map.put("rateDownList", rateDownList);
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
        dest.writeString(parentText);
        dest.writeString(userId);
        dest.writeString(userName);
        dest.writeString(userAvatar);
        dest.writeString(createdAt);
        dest.writeString(editedAt);
        dest.writeInt(rating);
        dest.writeList(this.rateUpList);
        dest.writeList(this.rateDownList);
    }

    private Comment(Parcel in) {
        // важен порядок считывания
        key = in.readString();
        text = in.readString();
        cardId = in.readString();
        parentId = in.readString();
        userId = in.readString();
        userName = in.readString();
        userAvatar = in.readString();
        createdAt = in.readString();
        editedAt = in.readString();
        rating = in.readInt();
        in.readStringList(rateUpList);
        in.readStringList(rateDownList);
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

    public String getParentText() {
        return parentText;
    }
    public void setParentText(String parentText) {
        this.parentText = parentText;
    }

    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserAvatar() {
        return userAvatar;
    }
    public void setUserAvatar(String userAvatar) {
        this.userAvatar = userAvatar;
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
        if (null == this.rating) return 0;
        else return rating;
    }
    public void setRating(int rating) {
        this.rating = rating;
    }

    public List<String> getRateUpList() {
        return rateUpList;
    }
    public void setRateUpList(List<String> rateUpList) {
        this.rateUpList = rateUpList;
    }

    public List<String> getRateDownList() {
        return rateDownList;
    }
    public void setRateDownList(List<String> rateDownList) {
        this.rateDownList = rateDownList;
    }


    @Exclude public void rateUp(String userId) {
        if (!rateUpList.contains(userId)) {
            rateUpList.add(userId);
            setRating(rating+1);
        }
        rateDownList.remove(userId);
    }
    @Exclude public void rateDown(String userId) {
        if (!rateDownList.contains(userId)) {
            rateDownList.add(userId);
            setRating(rating-1);
        }
        rateUpList.remove(userId);
    }

    @Exclude public boolean isRatedUpBy(String userId) {
        return getRateUpList().contains(userId);
    }
    @Exclude public boolean isRatedDownBy(String userId) {
        return getRateDownList().contains(userId);
    }

}