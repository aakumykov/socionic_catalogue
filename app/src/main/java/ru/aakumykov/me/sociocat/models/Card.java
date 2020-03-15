package ru.aakumykov.me.sociocat.models;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.IgnoreExtraProperties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.aakumykov.me.sociocat.Constants;


// TODO: как сделать так, чтобы графическая карточка не могла сохраниться без картинки?
// И так далее...

@IgnoreExtraProperties
public class Card implements
        Parcelable,
        iCommentable
{
    public static final String KEY_KEY = "key";
    public final static String KEY_CTIME = "ctime";
    public static final String KEY_TAGS = "tags";
    public static final String KEY_TITLE = "title";
    public static final String KEY_USER_ID = "userId";
    public static final String KEY_USER_NAME = "userName";
    public static final String KEY_USER_AVATAR_URL = "userAvatarURL";
    public static final String KEY_COMMENTS_KEYS = "commentsKeys";
    public static final String KEY_RATING = "rating";

    public final static String GHOST_TAG_PREFIX = "TAG_";
    public static final String TEXT_CARD = "TEXT_CARD";
    public static final String IMAGE_CARD = "IMAGE_CARD";
    public static final String VIDEO_CARD = "VIDEO_CARD";
    public static final String AUDIO_CARD = "AUDIO_CARD";

    // TODO: а может, все свойства здесь инициализировать?
    private String key;
    private String userId;
    private String userName;
    private String userAvatarURL;
    private String type;
    private String title;
    private String quote;
    private String quoteSource;
    private String imageURL;
    private String fileName;
    private String videoCode;
    private String audioCode;
    private Float timecode = 0.0f;
    private String description;
    private Integer rating = 0;
    private Long ctime = 0L;
    private Long mtime = 0L;

    private List<String> commentsKeys = new ArrayList<>();
    private List<String> tags = new ArrayList<>();

    @Exclude private transient String localImageURI;
    @Exclude private transient String mimeType;
    @Exclude private transient String imageType;


    public Card() {

    }

    public Card(String cardKey) {
        this.key = cardKey;
    }

    @Override @Exclude
    public String toString() {
        return "Card {"+
                "  key: "+getKey()+
                ", title: "+getTitle()+
                ", userId: "+getUserId()+
                ", userName: "+getUserName()+
                ", userAvatarURL: "+getUserAvatarURL()+
                ", type: "+getType()+
                ", quote: "+getQuote()+
                ", quoteSource: "+getQuoteSource()+
                ", imageURL: "+getImageURL()+
                ", fileName: "+getFileName()+
                ", videoCode: "+getVideoCode() +
                ", audioCode: "+getAudioCode() +
                ", timecode: "+getTimecode() +
                ", description: "+getDescription()+
                ", tags: "+ getTags()+
                ", commentsKeys: "+getCommentsKeys()+
                ", rating: "+getRating()+
                ", ctime: "+getCTime()+
                ", mtime: "+getMTime()+
                ", localImageURI: "+getLocalImageURI()+
                ", mimeType: "+getMimeType()+
                ", imageType: "+getImageType()+
                " }";
    }

    @Exclude
    public Map<String, Object> toMap() {
        Map<String, Object> cardMap = new HashMap<>();

        cardMap.put("key", getKey());
        cardMap.put("userId", getUserId());
        cardMap.put("userName", getUserName());
        cardMap.put("userAvatarURL", getUserAvatarURL());
        cardMap.put("type", getType());
        cardMap.put("title", getTitle());
        cardMap.put("quote", getQuote());
        cardMap.put("quoteSource", getQuoteSource());
        cardMap.put("imageURL", getImageURL());
        cardMap.put("localImageURI", getLocalImageURI());
        cardMap.put("mimeType", getMimeType());
        cardMap.put("imageType", getImageType());
        cardMap.put("fileName", getFileName());
        cardMap.put("videoCode", getVideoCode());
        cardMap.put("audioCode", getAudioCode());
        cardMap.put("timecode", getTimecode());
        cardMap.put("description", getDescription());
        cardMap.put("tags", getTags());
        cardMap.put("commentsKeys", getCommentsKeys());
        //cardMap.put("rating", this.getRating());
        cardMap.put("ctime", this.getCTime());
        cardMap.put("mtime", this.getMTime());

        for (String tagName : this.getTags())
            cardMap.put(Card.GHOST_TAG_PREFIX+tagName, true);

        return cardMap;
    }


    // Parcelable
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // важен порядок заполнения
        dest.writeString(this.key);
        dest.writeString(this.userId);
        dest.writeString(this.userName);
        dest.writeString(this.userAvatarURL);
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
        dest.writeFloat(this.timecode);
        dest.writeString(this.description);

        dest.writeList(this.tags);
        dest.writeList(this.commentsKeys);

        dest.writeInt(this.rating);
        dest.writeLong(this.ctime);
        dest.writeLong(this.mtime);
    }

    protected Card(Parcel in) {
        // важен порядок считывания
        key = in.readString();
        userId = in.readString();
        userName = in.readString();
        userAvatarURL = in.readString();
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
        timecode = in.readFloat();
        description = in.readString();

        in.readList(tags, ArrayList.class.getClassLoader());
        in.readList(commentsKeys, ArrayList.class.getClassLoader());

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


    // iCommentable
    @Override
    public String getKey() {
        return key;
    }

    @Override
    public String getText() {
        switch (getType()) {
            case Constants.TEXT_CARD:
                return getQuote();
            case Constants.AUDIO_CARD:
                return getTitle();
            case Constants.VIDEO_CARD:
                return getTitle();
            case Constants.IMAGE_CARD:
                return getTitle();
            default:
                return "";
        }
    }


    // Геттеры
    public String getUserId() {
        return userId;
    }
    public String getUserName() { return userName; }
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
    public String getFileName() {
        return fileName;
    }
    public String getDescription() {
        return description;
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
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // Список комментариев к карточке
    public List<String> getCommentsKeys() {
        return new ArrayList<>(this.commentsKeys);
    }
    // Сеттер не используется неявно при считывании карточки из БД
    public void setCommentsKeys(List<String> inputCommentsKeys) {
        if (null != inputCommentsKeys) {
            this.commentsKeys.addAll(inputCommentsKeys);
        }
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

    // Метки
    public List<String> getTags() {
        return tags;
    }
    public void setTags(List<String> inputTagsList) {
        this.tags.clear();
        if (null != inputTagsList) {
            this.tags.addAll(inputTagsList);
        }
    }
    @Exclude
    public HashMap<String, Boolean> getTagsHash() {
        HashMap<String, Boolean> hashMap = new HashMap<>();
        for (String tagName : this.tags)
            hashMap.put(tagName, true);
        return hashMap;
    }

    // Ссылка на аватар
    public String getUserAvatarURL() {
        return userAvatarURL;
    }

    public void setUserAvatarURL(String userAvatarURL) {
        this.userAvatarURL = userAvatarURL;
    }

    // Отметка времени
    public Float getTimecode() {
        return timecode;
    }
    public void setTimecode(Float timecode) {
        this.timecode = timecode;
    }

    // ImageURL
    public String getImageURL() {
        return imageURL;
    }
    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }
    public void clearImageURL() {
        this.imageURL = null;
    }

    // Рейтинг
    public int getRating() {
        if (null == this.rating) return 0;
        else return rating;
    }
    public void setRating(Integer rating) {
        this.rating = rating;
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
//        return getImageType().equals(Constants.TEXT_CARD);
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
        return userId.equals(user.getKey());
    }



    // Разные
    @Exclude public void addTag(String tag) {
        this.tags.add(tag);
    }

    @Exclude public boolean hasTag(String tagName) {
        return tags.contains(tagName);
    }
}
