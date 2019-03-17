package ru.aakumykov.me.sociocat.models;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.google.firebase.database.Exclude;

import java.util.Arrays;
import java.util.HashMap;

import ru.aakumykov.me.sociocat.Config;
import ru.aakumykov.me.sociocat.Constants;

// TODO: как сделать так, чтобы графическая карточка не могла сохраниться без картинки?
// И так далее...

public class Card implements Parcelable {

    private String key;
    private String userId;
    private String userName;
    private String type;
    private String title;
    private String quote;
    private String quoteSource;
    private String imageURL;
    @Exclude private transient Uri localImageURI;
    @Exclude private transient String mimeType;
    private String fileName;
    private String videoCode;
    private String audioCode;
    private String description;
    private HashMap<String, Boolean> tags;
    private HashMap<String, Boolean> rateUpList;
    private HashMap<String, Boolean> rateDownList;
    private int commentsCount = 0;
    private HashMap<String, Boolean> commentsKeys/* = new HashMap<>()*/;
    private Integer rating = 0;
    private Long cTime = 0L;
    private Long mTime = 0L;


    public Card() {

    }

    @Exclude @Override
    public String toString() {
        return "Card {"+
                "  key: "+getKey()+
                ", userId: "+getUserId()+
                ", userName: "+getUserName()+
                ", type: "+getType()+
                ", title: "+getTitle()+
                ", quote: "+getQuote()+
                ", quoteSource: "+getQuoteSource()+
                ", imageURL: "+imageURL+
                ", fileName: "+fileName+
                ", videoCode: "+videoCode +
                ", audioCode: "+audioCode +
                ", description: "+getDescription()+
                ", tags: "+ getTags()+
                ", rateUpList: "+ getRateUpList()+
                ", rateDownList: "+ getRateDownList()+
                ", commentsCount: "+getCommentsCount()+
                ", commentsKeys: "+getCommentsKeys()+
                ", rating: "+getRating()+
                ", cTime: "+getCTime()+
                ", mTime: "+getMTime()+
                ", localImageURI: "+getLocalImageURI()+
                ", mimeType: "+getMimeType()+
            " }";
    }


    /* Parcelable */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // важен порядок заполнения
        dest.writeString(this.key);
        dest.writeString(this.userId);
        dest.writeString(this.userName);
        dest.writeString(this.type);
        dest.writeString(this.title);
        dest.writeString(this.quote);
        dest.writeString(this.quoteSource);
        dest.writeString(this.imageURL);
        dest.writeString(this.fileName);
        dest.writeString(this.videoCode);
        dest.writeString(this.audioCode);
        dest.writeString(this.description);
        dest.writeMap(this.tags);
        dest.writeMap(this.rateUpList);
        dest.writeMap(this.rateDownList);
        dest.writeInt(this.commentsCount);
        dest.writeMap(this.commentsKeys);
        dest.writeInt(this.rating);
        dest.writeLong(this.cTime);
        dest.writeLong(this.mTime);
    }

    protected Card(Parcel in) {
        // важен порядок считывания
        key = in.readString();
        userId = in.readString();
        userName = in.readString();
        type = in.readString();
        title = in.readString();
        quote = in.readString();
        quoteSource = in.readString();
        imageURL = in.readString();
        fileName = in.readString();
        videoCode = in.readString();
        audioCode = in.readString();
        description = in.readString();
        tags = (HashMap<String,Boolean>) in.readHashMap(HashMap.class.getClassLoader());
        rateUpList = (HashMap<String,Boolean>) in.readHashMap(HashMap.class.getClassLoader());
        rateDownList = (HashMap<String,Boolean>) in.readHashMap(HashMap.class.getClassLoader());
        commentsCount = in.readInt();
        commentsKeys = (HashMap<String,Boolean>) in.readHashMap(HashMap.class.getClassLoader());
        rating = in.readInt();
        cTime = in.readLong();
        mTime = in.readLong();
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
    public String getUserName() { return userName; }
    public String getKey() {
        return key;
    }
    public String getType() {
        return type + "";
    }
    public String getTitle() {
        return title;
    }
    public String getQuote() {
        return quote;
    }
    public String getQuoteSource() {
        return quoteSource;
    }
    public String getImageURL() {
        return imageURL;
    }
    public String getFileName() {
        return fileName;
    }
    public String getDescription() {
        return description;
    }
    public HashMap<String, Boolean> getTags() {
        if (null == tags) this.tags = new HashMap<>();
        return tags;
    }
    public HashMap<String, Boolean> getRateUpList() {
        if (null == rateUpList) this.rateUpList = new HashMap<>();
        return rateUpList;
    }
    public HashMap<String, Boolean> getRateDownList() {
        if (null == rateDownList) this.rateDownList = new HashMap<>();
        return rateDownList;
    }
    public int getCommentsCount() { return commentsCount; }
    public HashMap<String, Boolean> getCommentsKeys() { return commentsKeys; }
    public int getRating() {
        if (null == this.rating) return 0;
        else return rating;
    }
    public Long getCTime() {
        return this.cTime;
    }
    public Long getMTime() {
        return this.mTime;
    }


    // Сеттеры
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public void setUserName(String userName) { this.userName = userName; }
    public void setKey(String key) {
        this.key = key;
    }
    public void setType(String type) throws IllegalArgumentException {
        String[] availableCardTypes = {
                Constants.TEXT_CARD,
                Constants.IMAGE_CARD,
                Constants.VIDEO_CARD,
                Constants.AUDIO_CARD
        };

        if (Arrays.asList(availableCardTypes).contains(type)) {
            this.type = type;
        } else {
            throw new IllegalArgumentException("Unknown card_edit type '"+type+"'");
        }
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public void setQuote(String quote) {
        this.quote = quote;
    }
    public void setQuoteSource(String quoteSource) {
        this.quoteSource = quoteSource;
    }
    public void setImageURL(String imageURL) throws IllegalArgumentException {
            Uri uri = Uri.parse(imageURL);
            if (null == uri) throw new IllegalArgumentException("Error parsing imageURL");
            this.imageURL = imageURL;
    }
    public void setFileName(String fileName) throws IllegalArgumentException {
            this.fileName = fileName;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    public void setTags(HashMap<String, Boolean> tags) {
        this.tags = tags;
    }
    public void setRateUpList(HashMap<String, Boolean> rateUpList) {
        this.rateUpList = rateUpList;
    }
    public void setRateDownList(HashMap<String, Boolean> rateDownList) {
        this.rateDownList = rateDownList;
    }
    public void setCommentsCount(int count) { this.commentsCount = count; }
    public void setCommentsKeys(HashMap<String, Boolean> commentsKeys) {
        this.commentsKeys = commentsKeys;
    }
    public void setCTime(Long cTime) {
        this.cTime = cTime;
    }
    public void setMTime(Long mTime) {
        this.mTime = mTime;
    }


    public void setVideoCode(String videoCode) throws IllegalArgumentException {
        Uri uri = Uri.parse(videoCode);
        if (null == uri) throw new IllegalArgumentException("Error parsing videoCode");
        this.videoCode = videoCode;
    }
    public String getVideoCode() {
        return videoCode;
    }
    public void removeVideoCode() {
        this.videoCode = null;
    }

    public void setAudioCode(String audioCode) throws IllegalArgumentException {
        if (audioCode.matches(Config.YOUTUBE_CODE_REGEX))
            this.audioCode = audioCode;
        else
            throw new IllegalArgumentException("Wrong audio code: "+audioCode);
    }
    public String getAudioCode() {
        return audioCode;
    }
    public void removeAudioCode() {
        this.audioCode = null;
    }


    // Рейтинг
    @Exclude public boolean isRatedUpBy(String userId) {
        return (null != rateUpList && rateUpList.containsKey(userId));
    }
    @Exclude public boolean isRatedDownBy(String userId) {
        return (null != rateDownList && rateDownList.containsKey(userId));
    }

    @Exclude public void rateUp(String userId) {
        if (!isRatedUpBy(userId)) {
            if (null == this.rateUpList)
                this.rateUpList = new HashMap<>();
            this.rateUpList.put(userId, true);
            setRating(rating+1);
        }
        if (null != this.rateDownList) this.rateDownList.remove(userId);
    }
    @Exclude public void rateDown(String userId) {
        if (!isRatedDownBy(userId)) {
            if (null == this.rateDownList)
                this.rateDownList = new HashMap<>();
            this.rateDownList.put(userId, true);
            setRating(rating-1);
        }
        if (null != this.rateUpList) this.rateUpList.remove(userId);
    }

    @Exclude private void setRating(int ratingValue) {
        this.rating = ratingValue;
    }


    // Служебные
    @Exclude public boolean hasLocalImageURI() {
        return null != this.localImageURI;
    }
    @Exclude public void setLocalImageURI(Uri uri) {
        this.localImageURI = uri;
    }
    @Exclude public void setLocalImageURI(String uri) {
        this.localImageURI = Uri.parse(uri);
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

    @Exclude public boolean isTextCard() {
        // TODO: метод вызывает исключение, если данные повреждены
//        return getType().equals(Constants.TEXT_CARD);
        return Constants.TEXT_CARD.equals(getType()); // Устойчивый к отсутствию данных вариант.
    }
    @Exclude public boolean isImageCard() {
        return Constants.IMAGE_CARD.equals(getType());
    }
    @Exclude public boolean isVideoCard() {
        return Constants.VIDEO_CARD.equals(getType());
    }
    @Exclude public boolean isAudioCard() {
        return Constants.AUDIO_CARD.equals(getType());
    }

    @Exclude public boolean hasImageURL() {
        return !TextUtils.isEmpty(getImageURL());
    }


    // Разные
    @Exclude public void addTag(String tag) {
        tags.put(tag, true);
    }
}