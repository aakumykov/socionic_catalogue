package ru.aakumykov.me.sociocat.models;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.google.firebase.database.Exclude;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ru.aakumykov.me.sociocat.Constants;
import ru.aakumykov.me.sociocat.card_show.list_items.ListItem;
import ru.aakumykov.me.sociocat.card_show.list_items.iTextItem;

// TODO: как сделать так, чтобы графическая карточка не могла сохраниться без картинки?
// И так далее...

public class Card extends ListItem implements
        Parcelable,
        iTextItem
{
    public final static String KEY_CTIME = "ctime";
    public static final String KEY_TAGS = "tags";
    public static final String KEY_USER_ID = "userId";

    private String key;
    private String userId;
    private String userName;
    private String type;
    private String title;
    private String quote;
    private String quoteSource;
    private String imageURL;
    @Exclude private transient String localImageURI;
    @Exclude private transient String mimeType;
    @Exclude private transient String imageType;
    private String fileName;
    private String videoCode;
    private String audioCode;
    private String description;
    private List<String> tags;
    private HashMap<String, Boolean> rateUpList;
    private HashMap<String, Boolean> rateDownList;
    private int commentsCount = 0;
    private HashMap<String, Boolean> commentsKeys/* = new HashMap<>()*/;
    private Integer rating = 0;
    private Long ctime = 0L;
    private Long mtime = 0L;

    public Card() {
        setItemType(ListItem.ItemType.CARD_ITEM);
    }

    @Override @Exclude
    public String toString() {
        return "Card {"+
                "  key: "+getKey()+
                ", title: "+getTitle()+
                ", userId: "+getUserId()+
                ", userName: "+getUserName()+
                ", type: "+getType()+
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
                ", ctime: "+getCTime()+
                ", mtime: "+getMTime()+
                ", localImageURI: "+getLocalImageURI()+
                ", mimeType: "+getMimeType()+
                ", imageType: "+getImageType()+
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
        dest.writeString(this.localImageURI);
        dest.writeString(this.mimeType);
        dest.writeString(this.imageType);
        dest.writeString(this.fileName);
        dest.writeString(this.videoCode);
        dest.writeString(this.audioCode);
        dest.writeString(this.description);
        dest.writeList(this.tags);
        dest.writeMap(this.rateUpList);
        dest.writeMap(this.rateDownList);
        dest.writeInt(this.commentsCount);
        dest.writeMap(this.commentsKeys);
        dest.writeInt(this.rating);
        dest.writeLong(this.ctime);
        dest.writeLong(this.mtime);
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
        localImageURI = in.readString();
        mimeType = in.readString();
        imageType = in.readString();
        fileName = in.readString();
        videoCode = in.readString();
        audioCode = in.readString();
        description = in.readString();
        in.readList(tags, ArrayList.class.getClassLoader());
        rateUpList = (HashMap<String,Boolean>) in.readHashMap(HashMap.class.getClassLoader());
        rateDownList = (HashMap<String,Boolean>) in.readHashMap(HashMap.class.getClassLoader());
        commentsCount = in.readInt();
        commentsKeys = (HashMap<String,Boolean>) in.readHashMap(HashMap.class.getClassLoader());
        rating = in.readInt();
        ctime = in.readLong();
        mtime = in.readLong();
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
    public List<String> getTags() {
        if (null == tags) this.tags = new ArrayList<>();
        return tags;
    }
    @Exclude
    public HashMap<String, Boolean> getTagsHash() {
        HashMap<String, Boolean> hashMap = new HashMap<>();
        for (String tagName : this.tags)
            hashMap.put(tagName, true);
        return hashMap;
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
        return this.ctime;
    }
    public Long getMTime() {
        return this.mtime;
    }


    // Сеттеры
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public void setUserName(String userName) { this.userName = userName; }
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
    public void setQuoteSource(String quoteSource) {
        this.quoteSource = quoteSource;
    }
    public void setImageURL(String imageURL) {
            this.imageURL = imageURL;
    }
    public void setFileName(String fileName) {
            this.fileName = fileName;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    public void setTags(List<String> tags) {
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
        this.ctime = cTime;
    }
    public void setMTime(Long mTime) {
        this.mtime = mTime;
    }


    public void setVideoCode(String videoCode) {
        this.videoCode = videoCode;
    }
    public String getVideoCode() {
        return videoCode;
    }
    public void removeVideoCode() {
        this.videoCode = null;
    }

    public void setAudioCode(String audioCode)  {
        this.audioCode = audioCode;
    }
    public String getAudioCode() {
        return audioCode;
    }
    public void removeAudioCode() {
        this.audioCode = null;
    }


    // Рейтинг
    @Exclude
    public boolean isRatedUpBy(String userId) {
        return (null != rateUpList && rateUpList.containsKey(userId));
    }

    @Exclude
    public boolean isRatedDownBy(String userId) {
        return (null != rateDownList && rateDownList.containsKey(userId));
    }

    @Exclude
    public void rateUp(String userId) {
        prepareReteUpList();

        if (isRatedDownBy(userId))
            this.rateDownList.remove(userId);
        else
            this.rateUpList.put(userId, true);

        changeRating(+1);
    }

    @Exclude
    public void rateDown(String userId) {
        prepareRateDownList();

        if (isRatedUpBy(userId))
            this.rateUpList.remove(userId);
        else
            this.rateDownList.put(userId, true);

        changeRating(-1);
    }

    private void prepareReteUpList() {
        if (null == this.rateUpList)
            this.rateUpList = new HashMap<>();
    }
    private void prepareRateDownList() {
        if (null == this.rateDownList)
            this.rateDownList = new HashMap<>();
    }
    private void changeRating(int value) {
        if (null == this.rating)
            this.rating = 0;

        this.rating += value;
    }


    // Служебные
    @Exclude public boolean hasLocalImageURI() {
        return null != this.localImageURI;
    }
    @Exclude public void setLocalImageURI(Uri uri) {
        if (null != uri)
            this.localImageURI = uri.toString();
    }
    @Exclude public Uri getLocalImageURI() {
        return (null == this.localImageURI) ? null : Uri.parse(this.localImageURI);
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

    @Exclude public String getImageType() {
        return imageType;
    }
    @Exclude public void setImageType(String imageType) {
        this.imageType = imageType;
    }

    @Exclude public List<String> getTagsList(boolean inLowerCase) {
        return this.tags;
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

    @Exclude public boolean isCreatedBy(User user) {
        return key.equals(user.getKey());
    }



    // Разные
    @Exclude public void addTag(String tag) {
        this.tags.add(tag);
    }
}