package ru.aakumykov.me.mvp.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class Tag implements Parcelable {

    private String key;
    private String name;

    public Tag() {}

    public Tag(String name, String about) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Tag { key: "+key+", name: "+name+" }";
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("key", key);
        map.put("name", name);
        return map;
    }


    /* Parcelable */
    public static final Creator<Tag> CREATOR = new Creator<Tag>() {
        @Override
        public Tag createFromParcel(Parcel in) {
            return new Tag(in);
        }

        @Override
        public Tag[] newArray(int size) {
            return new Tag[size];
        }
    };

    private Tag(Parcel in) {
        key = in.readString();
        name = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // порядок заполнения важен (или нет?)
        dest.writeString(key);
        dest.writeString(name);
    }
    /* Parcelable */


    public String getKey() {
        return key;
    }
    public String getName() {
        return name;
    }

    public void setKey(String key) {
        this.key = key;
    }
    public void setName(String name) {
        this.name = name;
    }
}
