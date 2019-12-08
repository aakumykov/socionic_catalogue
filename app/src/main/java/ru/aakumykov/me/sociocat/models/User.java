package ru.aakumykov.me.sociocat.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.IgnoreExtraProperties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@IgnoreExtraProperties
public class User implements Parcelable {

    public static final String KEY_CARDS_KEYS = "cardsKeys";
    public static final String KEY_COMMENTS_KEYS = "commentsKeys";
    public static final String KEY_RATED_UP_CARD_KEYS = "ratedUpCardKeys";
    public static final String KEY_RATED_DOWN_CARD_KEYS = "ratedDownCardKeys";
    public static final String KEY_RATED_UP_COMMENT_KEYS = "ratedUPCommentKeys";
    public static final String KEY_RATED_DOWN_COMMENT_KEYS = "ratedDownCommentKeys";

    private String key;
    private String name;
    private String email;
    private String pushToken;
    private String about;
    private String avatarFileName;
    private String avatarURL;
    private boolean emailVerified = false;

    private List<String> cardsKeys = new ArrayList<>();
    private List<String> commentsKeys = new ArrayList<>();
    private List<String> unsubscribedCards = new ArrayList<>();
    private List<String> ratedUpCardKeys = new ArrayList<>();
    private List<String> ratedDownCardKeys = new ArrayList<>();
    private List<String> ratedUpCommentKeys = new ArrayList<>();
    private List<String> ratedDownCommentKeys = new ArrayList<>();


    public User() {}

    public User(String userId) throws IllegalArgumentException {
        if (TextUtils.isEmpty(userId)) throw new IllegalArgumentException("userId cannot be empty");
        this.key = userId;
    }

    // Преобразователи
    @Override @Exclude
    public String toString() {
        return "User { "+
                "key: "+key+
                ", name: "+name+
                ", email: "+email+
                ", pushToken: "+pushToken+
                ", about: "+about+
                ", avatarFileName: "+avatarFileName+
                ", avatarURL: "+avatarURL+
                ", emailVerified: "+emailVerified+
                ", cardsKeys: "+ cardsKeys +
                ", commentsKeys: "+ commentsKeys +
                ", unsubscribedCards: "+ unsubscribedCards +
                ", ratedUpCardKeys: "+ ratedUpCardKeys +
                ", ratedDownCardKeys: "+ ratedDownCardKeys +
                ", ratedUpCommentKeys: "+ ratedUpCommentKeys +
                ", ratedDownCommentKeys: "+ ratedDownCommentKeys +
                " }";
    }

    @Exclude
    public HashMap<String, Object> toMap() {
        HashMap<String,Object> map = new HashMap<>();
        map.put("key", key);
        map.put("name", name);
        map.put("email", email);
        map.put("pushToken", pushToken);
        map.put("about", about);
        map.put("avatarFileName", avatarFileName);
        map.put("avatarURL", avatarURL);
        map.put("emailVerified", emailVerified);
        map.put(KEY_CARDS_KEYS, cardsKeys);
        map.put(KEY_COMMENTS_KEYS, commentsKeys);
        map.put("unsubscribedCards", unsubscribedCards);
        map.put(KEY_RATED_UP_CARD_KEYS, ratedUpCardKeys);
        map.put(KEY_RATED_DOWN_CARD_KEYS, ratedDownCardKeys);
        map.put(KEY_RATED_UP_COMMENT_KEYS, ratedUpCommentKeys);
        map.put(KEY_RATED_DOWN_COMMENT_KEYS, ratedDownCommentKeys);
        return map;
    }


    // Конверт: начало
    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
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
        dest.writeString(name);
        dest.writeString(email);
        dest.writeString(pushToken);
        dest.writeString(about);
        dest.writeString(avatarFileName);
        dest.writeString(avatarURL);
        dest.writeString(String.valueOf(emailVerified));
        dest.writeList(this.cardsKeys);
        dest.writeList(this.commentsKeys);
        dest.writeList(this.unsubscribedCards);
        dest.writeList(this.ratedUpCardKeys);
        dest.writeList(this.ratedDownCardKeys);
        dest.writeList(this.ratedUpCommentKeys);
        dest.writeList(this.ratedDownCommentKeys);
    }

    private User(Parcel in) {
        // важен порядок чтения
        key = in.readString();
        name = in.readString();
        email = in.readString();
        pushToken = in.readString();
        about = in.readString();
        avatarFileName = in.readString();
        avatarURL = in.readString();
        emailVerified = in.readString().equals("1");
        in.readList(this.cardsKeys, ArrayList.class.getClassLoader());
        in.readList(this.commentsKeys, ArrayList.class.getClassLoader());
        in.readList(this.unsubscribedCards, ArrayList.class.getClassLoader());
        in.readList(this.ratedUpCardKeys, ArrayList.class.getClassLoader());
        in.readList(this.ratedDownCardKeys, ArrayList.class.getClassLoader());
        in.readList(this.ratedUpCommentKeys, ArrayList.class.getClassLoader());
        in.readList(this.ratedDownCommentKeys, ArrayList.class.getClassLoader());
    }
    // Конверт: конец


    public String getKey() {
        return key;
    }
    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getPushToken() {
        return this.pushToken;
    }
    public void setPushToken(String token) {
        this.pushToken = token;
    }

    public String getAbout() {
        return about;
    }
    public void setAbout(String about) {
        this.about = about;
    }

    public String getAvatarFileName() {
        return avatarFileName;
    }
    public void setAvatarFileName(String avatarFileName) {
        this.avatarFileName = avatarFileName;
    }

    public String getAvatarURL() {
        return avatarURL;
    }
    public void setAvatarURL(String avatarURL) {
        this.avatarURL = avatarURL;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }
    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    @Exclude public List<String> getCardsKeys() {
        return this.cardsKeys;
    }
    public void setCardsKeysList(List<String> cardsKeysList) {
        this.cardsKeys.clear();
        this.cardsKeys.addAll(cardsKeysList);
    }
    @Exclude public HashMap<String, Boolean> getCardsKeysHash() {
        HashMap<String,Boolean> hashMap = new HashMap<>();
        for (String key : this.cardsKeys)
            hashMap.put(key, true);
        return hashMap;
    }

    @Exclude public List<String> getCommentsKeys() {
        return this.commentsKeys;
    }
    public void setCommentsKeysList(List<String> commentsKeysList) {
        this.commentsKeys.clear();
        this.commentsKeys.addAll(commentsKeysList);
    }
    @Exclude public HashMap<String, Boolean> getCommentsKeysHash() {
        HashMap<String,Boolean> hashMap = new HashMap<>();
        for (String key : this.commentsKeys)
            hashMap.put(key, true);
        return hashMap;
    }

    @Exclude public List<String> getUnsubscribedCards() { return this.unsubscribedCards; }
    public void setUnsubscribedCardsList(List<String> unsubscribedCardsList) {
        this.unsubscribedCards.clear();
        this.unsubscribedCards.addAll(unsubscribedCardsList);
    }
    @Exclude public HashMap<String,Boolean> getUnsubscribedCardsHash() {
        HashMap<String,Boolean> hashMap = new HashMap<>();
        for (String key : this.unsubscribedCards)
            hashMap.put(key, true);
        return hashMap;
    }

    @Exclude public boolean hasAvatar() {
        return !TextUtils.isEmpty(getAvatarURL());
    }

    @Exclude public boolean isSubscribedToCardComments(String cardId) {
        return this.unsubscribedCards.contains(cardId);
    }


    // Манипуляция ключами карточек с изменённым рейтингом
    public void addRatedUpCard(String cardKey) {
        if (!ratedUpCardKeys.contains(cardKey)) {
            ratedUpCardKeys.add(cardKey);
        }
    }
    public void removeRatedUpCard(String cardKey) {
        ratedUpCardKeys.remove(cardKey);
    }
    public void addRatedDownCard(String cardKey) {
        if (!ratedDownCardKeys.contains(cardKey)) {
            ratedDownCardKeys.add(cardKey);
        }
    }
    public void removeRatedDownCard(String cardKey) {
        ratedDownCardKeys.remove(cardKey);
    }

    @Exclude public boolean alreadyRateUpCard(String cardKey) {
        return ratedUpCardKeys.contains(cardKey);
    }
    @Exclude public boolean alreadyRateDownCard(String cardKey) {
        return ratedDownCardKeys.contains(cardKey);
    }

    @Exclude public List<String> getRatedUpCardKeys() {
        return ratedUpCardKeys;
    }
    public void setRatedUpCardKeys(List<String> ratedUpCardKeys) {
        this.ratedUpCardKeys = ratedUpCardKeys;
    }

    @Exclude public List<String> getRatedDownCardKeys() {
        return ratedDownCardKeys;
    }
    public void setRatedDownCardKeys(List<String> ratedDownCardKeys) {
        this.ratedDownCardKeys = ratedDownCardKeys;
    }

    // Манипуляция ключами комментариев с изменённым рейтингом
    public void addRatedUpCommentKey(String commentKey) {
        if (!ratedUpCommentKeys.contains(commentKey))
            ratedUpCommentKeys.add(commentKey);
    }
    public void removeRatedUpCommentKey(String commentKey) {
        ratedUpCommentKeys.remove(commentKey);
    }
    public void addRatedDownCommentKey(String commentKey) {
        if (!ratedDownCommentKeys.contains(commentKey))
            ratedDownCommentKeys.add(commentKey);
    }
    public void removeRatedDownCommentKey(String commentKey) {
        ratedDownCommentKeys.remove(commentKey);
    }

    @Exclude public boolean alreadyRateUpComment(String commentKey) {
        return ratedUpCommentKeys.contains(commentKey);
    }
    @Exclude public boolean alreadyRateDownComment(String commentKey) {
        return ratedDownCommentKeys.contains(commentKey);
    }

    @Exclude public List<String> getRatedUpCommentKeys() {
        return ratedUpCommentKeys;
    }
    public void setRatedUpCommentKeys(List<String> ratedUpCommentKeys) {
        this.ratedUpCommentKeys = ratedUpCommentKeys;
    }

    @Exclude public List<String> getRatedDownCommentKeys() {
        return ratedDownCommentKeys;
    }
    public void setRatedDownCommentKeys(List<String> ratedDownCommentKeys) {
        this.ratedDownCommentKeys = ratedDownCommentKeys;
    }
}
