package ru.aakumykov.me.mvp.models;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {
    
    private String key;
    private String name;
    private String email;
    
    public User() {}
    
    public User(String name, String email) {
        this.name = name;
        this.email = email;
    }

    @Override
    public String toString() {
        return "User { key: "+key+", name: "+name+", email: "+email+" }";
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
        email = in.readString();
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
        dest.writeString(email);
    }
    /* Parcelable */


    public String getKey() {
        return key;
    }
    public String getName() {
        return name;
    }
    public String getEmail() {
        return email;
    }

    public void setKey(String key) {
        this.key = key;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setEmail(String email) {
        this.email = email;
    }
}
