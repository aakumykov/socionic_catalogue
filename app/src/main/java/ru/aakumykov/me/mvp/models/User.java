package ru.aakumykov.me.mvp.models;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {
    
    private String key;
    private String name;
    private String about;
    
    public User() {}
    
    public User(String name, String about) {
        this.name = name;
        this.about = about;
    }

    @Override
    public String toString() {
        return "User { key: "+key+", name: "+name+", about: "+ about +" }";
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
        key = in.readString();
        name = in.readString();
        about = in.readString();
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
        dest.writeString(about);
    }
    /* Parcelable */


    public String getKey() {
        return key;
    }
    public String getName() {
        return name;
    }
    public String getAbout() {
        return about;
    }

    public void setKey(String key) {
        this.key = key;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setAbout(String about) {
        this.about = about;
    }
}
