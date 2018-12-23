package ru.aakumykov.me.mvp.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.TextView;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class User implements Parcelable {
    
    private String key;
    private String name;
    private String email;
    private String about;
    private String avatarURL;
    private boolean emailVerified = false;
    private HashMap<String, Boolean> commentsKeys;

    public User() {}

    public User(String userId) throws IllegalArgumentException {
        if (TextUtils.isEmpty(userId)) throw new IllegalArgumentException("userId cannot be empty");
        this.key = userId;
    }

    public User(
            String name,
            String email,
            @Nullable String about,
            @Nullable String avatarURL
    ) throws  IllegalArgumentException
    {
        if (TextUtils.isEmpty(name)) throw new IllegalArgumentException("Name cannot be empty.");
        this.name = name;

        // TODO: проверять REGEXP-ом
        if (TextUtils.isEmpty(email)) throw new IllegalArgumentException("Email cannot be empty.");
        this.email = email;

        if (null != about) this.about = about;

        if (null != avatarURL) this.avatarURL = avatarURL;
    }


    // Преобразователи
    @Override
    @Exclude
    public String toString() {
        return "User { "+
                "key: "+key+
                ", name: "+name+
                ", email: "+email+
                ", about: "+about+
                ", avatarURL: "+avatarURL+
                ", emailVerified: "+emailVerified+
                ", commentsKeys: "+commentsKeys+
                " }";
    }

    @Exclude
    public HashMap<String, Object> toMap() {
        HashMap<String,Object> map = new HashMap<>();
        map.put("key", key);
        map.put("name", name);
        map.put("email", email);
        map.put("about", about);
        map.put("avatarURL", avatarURL);
        map.put("emailVerified", emailVerified);
        map.put("commentsKeys", commentsKeys);
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
        dest.writeString(about);
        dest.writeString(avatarURL);
        dest.writeString(String.valueOf(emailVerified));
        dest.writeMap(this.commentsKeys);
    }

    private User(Parcel in) {
        // важен порядок чтения
        key = in.readString();
        name = in.readString();
        email = in.readString();
        about = in.readString();
        avatarURL = in.readString();
        emailVerified = in.readString().equals("1");
        commentsKeys = (HashMap<String,Boolean>) in.readHashMap(HashMap.class.getClassLoader());
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

    public String getAbout() {
        return about;
    }
    public void setAbout(String about) {
        this.about = about;
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

    public HashMap<String, Boolean> getCommentsKeys() {
        return commentsKeys;
    }
    public void setCommentsKeys(HashMap<String, Boolean> commentsKeys) {
        this.commentsKeys = commentsKeys;
    }

    @Exclude public boolean hasAvatar() {
        return !TextUtils.isEmpty(getAvatarURL());
    }
}
