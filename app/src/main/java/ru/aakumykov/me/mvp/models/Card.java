package ru.aakumykov.me.mvp.models;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.Exclude;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import ru.aakumykov.me.mvp.Constants;

// TODO: как сделать так, чтобы графическая карточка не могла сохраниться без картинки?
// И так далее...

public class Card implements Parcelable {

    private String key;
    private String userId;
    private String type;
    private String title;
    private String quote;
    private String imageURL;
    private String videoCode;
    private String description;
    private HashMap<String, Boolean> tags;
    private int commentsCount = 0;
    private HashMap<String, Boolean> commentsKeys;
    private Integer rating = 0;
    private List<String> rateUpList = new ArrayList<>();
    private List<String> rateDownList = new ArrayList<>();

    public Card() {

    }

    public Card(
            String userId,
            String type,
            String title,
            String quote,
            String imageURL,
            String videoCode,
            String description,
            HashMap<String,Boolean> tagsMap
    )
    {
        setType(type);
        this.title = title;
        this.quote = quote;
        setImageURL(imageURL);
        setVideoCode(videoCode);
        this.description = description;
        this.tags = tagsMap;
        this.commentsCount = 0;
        this.rating = 0;
        this.rateUpList = new ArrayList<>();
        this.rateDownList = new ArrayList<>();
    }

    @Exclude
    @Override
    public String toString() {
        return "Card { "+
                "  key: "+getKey()+
                ", userId: "+getUserId()+
                ", type: "+getType()+
                ", title: "+getTitle()+
                ", quote: "+getQuote()+
                ", imageURL: "+imageURL+
                ", videoCode: "+videoCode +
                ", description: "+getDescription()+
                ", tags: "+ getTags()+
                ", commentsCount: "+getCommentsCount()+
                ", commentsKeys: "+getCommentsKeys()+
                ", rating: "+getRating()+
                ", rateUpList: "+rateUpList+
                ", rateDownList: "+rateDownList+
            " }";
    }

    @Exclude
    public HashMap<String, Object> toMap() {
        HashMap<String,Object> map = new HashMap<>();
         map.put("userId", userId);
         map.put("type", type);
         map.put("title", title);
         map.put("quote", quote);
         map.put("imageURL", imageURL);
         map.put("videoCode", videoCode);
         map.put("description", description);
         map.put("tags", tags);
         map.put("commentsCount", commentsCount);
         map.put("commentsKeys", commentsKeys);
         map.put("rating", rating);
         map.put("rateUpList", rateUpList);
         map.put("rateDownList", rateDownList);
        return map;
    }


    /* Parcelable */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // важен порядок заполнения
        dest.writeString(this.key);
        dest.writeString(this.userId);
        dest.writeString(this.type);
        dest.writeString(this.title);
        dest.writeString(this.quote);
        dest.writeString(this.imageURL);
        dest.writeString(this.videoCode);
        dest.writeString(this.description);
        dest.writeMap(this.tags);
        dest.writeInt(this.commentsCount);
        dest.writeMap(this.commentsKeys);
        dest.writeInt(this.rating);
        dest.writeList(this.rateUpList);
        dest.writeList(this.rateDownList);
    }

    protected Card(Parcel in) {
        // важен порядок считывания
        key = in.readString();
        userId = in.readString();
        type = in.readString();
        title = in.readString();
        quote = in.readString();
        imageURL = in.readString();
        videoCode = in.readString();
        description = in.readString();
        tags = (HashMap<String,Boolean>) in.readHashMap(HashMap.class.getClassLoader());
        commentsCount = in.readInt();
        commentsKeys = (HashMap<String,Boolean>) in.readHashMap(HashMap.class.getClassLoader());
        rating = in.readInt();
        in.readStringList(rateUpList);
        in.readStringList(rateDownList);
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


    // Геттеры
    public String getUserId() {
        return userId;
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
    public String getVideoCode() {
        return videoCode;
    }
    public String getDescription() {
        return description;
    }
    public HashMap<String, Boolean> getTags() {
        if (null == tags) this.tags = new HashMap<>();
        return tags;
    }
    public int getCommentsCount() { return commentsCount; }
    public HashMap<String, Boolean> getCommentsKeys() { return commentsKeys; }
    public int getRating() {
        if (null == this.rating) return 0;
        else return rating;
    }
    public List<String> getRateUpList() {
        return rateUpList;
    }
    public List<String> getRateDownList() {
        return rateDownList;
    }


    // Сеттеры
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public void setKey(String key) {
        this.key = key;
    }
    public void setType(String type) throws IllegalArgumentException {
        String[] availableCardTypes = {
                Constants.TEXT_CARD,
                Constants.IMAGE_CARD,
                Constants.VIDEO_CARD
        };

        if (Arrays.asList(availableCardTypes).contains(type)) {
            this.type = type;
        } else {
            throw new IllegalArgumentException("Unknown card type '"+type+"'");
        }
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public void setQuote(String quote) {
        this.quote = quote;
    }
    public void setImageURL(String imageURL) throws IllegalArgumentException {
            Uri uri = Uri.parse(imageURL);
            if (null == uri) throw new IllegalArgumentException("Error parsing imageURL");
            this.imageURL = imageURL;
    }
    public void setVideoCode(String videoCode) throws IllegalArgumentException {
            Uri uri = Uri.parse(videoCode);
            if (null == uri) throw new IllegalArgumentException("Error parsing videoCode");
            this.videoCode = videoCode;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public void setTags(HashMap<String, Boolean> tags) {
        this.tags = tags;
    }
    public void setCommentsCount(int count) { this.commentsCount = count; }
    public void setCommentsKeys(HashMap<String, Boolean> commentsKeys) {
        this.commentsKeys = commentsKeys;
    }
    // Этот метод не публичный
    private void setRating(int ratingValue) { this.rating = ratingValue; }
    public void setRateUpList(List<String> rateUpList) {
        this.rateUpList = rateUpList;
    }
    public void setRateDownList(List<String> rateDownList) {
        this.rateDownList = rateDownList;
    }

    // Служебные
    @Exclude private Uri localImageURI;
    @Exclude private String mimeType;

    @Exclude public void setLocalImageURI(Uri uri) {
        this.localImageURI = uri;
    }
    @Exclude public Uri getLocalImageURI() {
        return this.localImageURI;
    }
    @Exclude public void clearLocalImageURI() {
        this.localImageURI = null;
    }

    @Exclude public String getMimeType() {
        return mimeType;
    }
    @Exclude public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }
    @Exclude public void clearMimeType() {
        this.mimeType = null;
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