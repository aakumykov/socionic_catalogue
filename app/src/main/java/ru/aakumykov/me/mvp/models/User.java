package ru.aakumykov.me.mvp.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class User implements Parcelable {
    
    private String name;

    public User() {}
    
    public User(String name, String email, @Nullable String about) throws  IllegalArgumentException {
        if (TextUtils.isEmpty(name)) throw new IllegalArgumentException("Name cannot be empty.");
        this.name = name;

        // TODO: проверять REGEXP-ом
        if (TextUtils.isEmpty(email)) throw new IllegalArgumentException("Email cannot be empty.");
        this.email = email;

        if (null != about) this.about = about;
    }


    // Преобразователи
    @Override
    @Exclude
    public String toString() {
        return "User { key: "+key+", name: "+name+", email: "+email+", about: "+about+" }";
    }

    @Exclude
    public HashMap<String, Object> toMap() {
        HashMap<String,Object> map = new HashMap<>();
        map.put("key", key);
        map.put("name", name);
        map.put("email", email);
        map.put("about", about);
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
    }

    private User(Parcel in) {
        // важен порядок чтения
        key = in.readString();
        name = in.readString();
        email = in.readString();
        about = in.readString();
    }
    /* Parcelable */


    @Exclude
    public String getName() {
        return name;
    }
    @Exclude
    public void setName(String name) {
        this.name = name;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public void setAbout(String about) {
        this.about = about;
    }
}
