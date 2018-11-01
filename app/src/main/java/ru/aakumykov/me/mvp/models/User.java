package ru.aakumykov.me.mvp.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class User implements Parcelable {
    
    private String name;

    public User() {}
    
    public User(String name) throws Exception {
        if (TextUtils.isEmpty(name)) throw new Exception("Name cannot be empty.");
        this.name = name;
    }


    @Exclude
    @Override
    public String toString() {
        return "User { name: "+name+" }";
    }

    @Exclude
    public HashMap<String, Object> toMap() {
        HashMap<String,Object> map = new HashMap<>();
        map.put("name", name);
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

    private User(Parcel in) {
        name = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // порядок заполнения важен (или нет?)
        dest.writeString(name);
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
}
