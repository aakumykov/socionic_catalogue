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
    
    private String key;
    private String name;
    private String email;
    private String pushToken;
    private String about;
    private String avatarFileName;
    private String avatarURL;
    private boolean emailVerified = false;

    private List<String> cardsKeysList = new ArrayList<>();
    private List<String> commentsKeysList = new ArrayList<>();
    private List<String> unsubscribedCardsList = new ArrayList<>();



    private HashMap<String,Boolean> cardsKeys = new HashMap<>();
    private HashMap<String,Boolean> commentsKeys = new HashMap<>();
    private HashMap<String,Boolean> unsubscribedCards = new HashMap<>();

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
                ", cardsKeys: "+ cardsKeysList +
                ", commentsKeys: "+ commentsKeysList +
                ", unsubscribedCards: "+ unsubscribedCardsList +
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
        map.put("cardsKeys", cardsKeysList);
        map.put("commentsKeys", commentsKeysList);
        map.put("unsubscribedCards", unsubscribedCardsList);
        return map;
    }


    /* Parcelable */
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
        dest.writeList(this.cardsKeysList);
        dest.writeList(this.commentsKeysList);
        dest.writeList(this.unsubscribedCardsList);
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
//        cardsKeys = (HashMap<String,Boolean>) in.readHashMap(HashMap.class.getClassLoader());
//        commentsKeys = (HashMap<String,Boolean>) in.readHashMap(HashMap.class.getClassLoader());
//        unsubscribedCards = (HashMap<String,Boolean>) in.readHashMap(HashMap.class.getClassLoader());
        in.readList(this.cardsKeysList, ArrayList.class.getClassLoader());
        in.readList(this.commentsKeysList, ArrayList.class.getClassLoader());
        in.readList(this.unsubscribedCardsList, ArrayList.class.getClassLoader());
    }
    /* Parcelable */


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

    public List<String> getCardsKeys() {
        return new ArrayList<>(cardsKeys.keySet());
    }
    public void setCardsKeysList(List<String> cardsKeysList) {
        this.cardsKeysList.clear();
        this.cardsKeysList.addAll(cardsKeysList);
    }
    @Exclude public HashMap<String, Boolean> getCardsKeysHash() {
        HashMap<String,Boolean> hashMap = new HashMap<>();
        for (String key : this.cardsKeysList)
            hashMap.put(key, true);
        return hashMap;
    }

    public List<String> getCommentsKeys() {
        return new ArrayList<>(commentsKeys.keySet());
    }
    public void setCommentsKeysList(List<String> commentsKeysList) {
        this.commentsKeysList.clear();
        this.commentsKeysList.addAll(commentsKeysList);
    }
    @Exclude public HashMap<String, Boolean> getCommentsKeysHash() {
        HashMap<String,Boolean> hashMap = new HashMap<>();
        for (String key : this.commentsKeysList)
            hashMap.put(key, true);
        return hashMap;
    }

    public List<String> getUnsubscribedCards() { return new ArrayList<>(unsubscribedCards.keySet()); }
    public void setUnsubscribedCardsList(List<String> unsubscribedCardsList) {
        this.unsubscribedCardsList.clear();
        this.unsubscribedCardsList.addAll(unsubscribedCardsList);
    }
    @Exclude public HashMap<String,Boolean> getUnsubscribedCardsHash() {
        HashMap<String,Boolean> hashMap = new HashMap<>();
        for (String key : this.unsubscribedCardsList)
            hashMap.put(key, true);
        return hashMap;
    }

    @Exclude public boolean hasAvatar() {
        return !TextUtils.isEmpty(getAvatarURL());
    }

    @Exclude public boolean isSubscribedToCardComments(String cardId) {
        return this.unsubscribedCardsList.contains(cardId);
    }


    public void setCardsKeys(HashMap<String, Boolean> cardsKeys) {
        this.cardsKeys = cardsKeys;
    }

    public void setCommentsKeys(HashMap<String, Boolean> commentsKeys) {
        this.commentsKeys = commentsKeys;
    }

    public void setUnsubscribedCards(HashMap<String, Boolean> unsubscribedCards) {
        this.unsubscribedCards = unsubscribedCards;
    }

}
