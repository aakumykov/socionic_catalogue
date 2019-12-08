package ru.aakumykov.me.sociocat.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import androidx.annotation.Nullable;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.IgnoreExtraProperties;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@IgnoreExtraProperties
public class Comment implements
        Parcelable,
        iCommentable
{
    public static final String KEY_USER_NAME = "userName";
    public static final String KEY_USER_AVATAR = "userAvatarURL";
    public static final String KEY_TEXT = "text";
    public static final String KEY_CREATED_AT = "createdAt";
    public static final String KEY_EDITED_AT = "editedAt";
    public static final String KEY_PARENT_ID = "parentKey";
    public static final String KEY_PARENT_TEXT = "parentText";
    public static final String KEY_RATING = "rating";

    private String key;
    private String text;
    private String cardId;
    private String parentId;
    private String parentText;
    private String userId;
    private String userName;
    private String userAvatarURL;
    private Long createdAt = 0L;
    private Long editedAt = 0L;
    private Integer rating = 0;
    private List<String> rateUpList = new ArrayList<>();
    private List<String> rateDownList = new ArrayList<>();


    public Comment(){

    }

    public Comment(
            String text,
            String cardId,
            iCommentable parent,
            User user
    ) {
        setCommentText(text);
        setCardId(cardId);
        setParent(parent);
        setUser(user);

        setCreatedAt(new Date().getTime());
    }

    public Comment(
            String text,
            String cardId,
            String parentId,
            String parentText,
            String userId,
            String userName,
            String userAvatarURL
    ) {
        this.text = text;
        this.cardId = cardId;
        this.parentId = parentId;
        this.parentText = parentText;
        this.userId = userId;
        this.userName = userName;
        this.userAvatarURL = userAvatarURL;
        this.rating = 0;
        this.rateUpList = new ArrayList<>();
        this.rateDownList = new ArrayList<>();
    }

    @Override @Exclude
    public String toString() {
        return "Comment { "
                +"key: "+key
                +", text: "+text
                +", cardId: "+cardId
                +", parentId: "+parentId
                +", parentText: "+parentText
                +", userId: "+userId
                +", userName: "+userName
                +", userAvatarURL: "+ userAvatarURL
                +", createdAt: "+createdAt
                +", editedAt: "+editedAt
                +", rating: "+rating
                +", rateUpList: "+rateUpList
                +", rateDownList: "+rateDownList
            +" }";
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> map = new HashMap<>();
            map.put("key", key);
            map.put("text", text);
            map.put("cardId", cardId);
            map.put("parentId", parentId);
            map.put("parentText", parentText);
            map.put("userId", userId);
            map.put("userName", userName);
            map.put("userAvatarURL", userAvatarURL);
            map.put("createdAt", createdAt);
            map.put("editedAt", editedAt);
            map.put("rating", rating);
            map.put("rateUpList", rateUpList);
            map.put("rateDownList", rateDownList);
        return map;
    }


    // Конверт, начало
    public static final Creator<Comment> CREATOR = new Creator<Comment>() {
        @Override
        public Comment createFromParcel(Parcel in) {
            return new Comment(in);
        }

        @Override
        public Comment[] newArray(int size) {
            return new Comment[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // важен порядок заполнения
        dest.writeString(key); // 01
        dest.writeString(text); // 02
        dest.writeString(cardId); // 03
        dest.writeString(parentId); // 04
        dest.writeString(parentText); // 05
        dest.writeString(userId); // 06
        dest.writeString(userName); // 07
        dest.writeString(userAvatarURL); // 08
        dest.writeLong(createdAt); // 09
        dest.writeLong(editedAt); // 10
        dest.writeInt(rating); // 11
        dest.writeList(this.rateUpList); // 12
        dest.writeList(this.rateDownList); // 13
    }

    public Comment(Parcel in) {
        // важен порядок считывания
        key = in.readString(); // 01
        text = in.readString(); // 02
        cardId = in.readString(); // 03
        parentId = in.readString(); // 04
        parentText = in.readString(); // 05
        userId = in.readString(); // 06
        userName = in.readString(); // 07
        userAvatarURL = in.readString(); // 08
        createdAt = in.readLong(); // 09
        editedAt = in.readLong(); // 10
        rating = in.readInt(); // 11
        in.readStringList(rateUpList); // 12
        in.readStringList(rateDownList); // 13
    }
    // Конверт, конец


    // iCommentable
    @Override
    public String getKey() {
        return key;
    }

    @Override
    public String getText() {
        return text;
    }


    // Геттеры и сеттеры
    public void setKey(String key) {
        this.key = key;
    }

    public void setCommentText(String text) {
        this.text = text;
    }

    public String getCardId() {
        return cardId;
    }
    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public String getParentId() {
        return parentId;
    }
    @Deprecated
    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getParentText() {
        return parentText;
    }
    @Deprecated
    public void setParentText(String parentText) {
        this.parentText = parentText;
    }

    public String getUserId() {
        return userId;
    }
    @Deprecated
    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }
    @Deprecated
    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserAvatarURL() {
        return userAvatarURL;
    }
    @Deprecated
    public void setUserAvatarURL(String userAvatarURL) {
        this.userAvatarURL = userAvatarURL;
    }

    public Long getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }

    public Long getEditedAt() {
        return editedAt;
    }
    public void setEditedAt(Long editedAt) {
        this.editedAt = editedAt;
    }

    public int getRating() {
        if (null == this.rating) return 0;
        else return rating;
    }
    public void setRating(int rating) {
        this.rating = rating;
    }

    public List<String> getRateUpList() {
        return rateUpList;
    }
    public void setRateUpList(List<String> rateUpList) {
        this.rateUpList = rateUpList;
    }

    public List<String> getRateDownList() {
        return rateDownList;
    }
    public void setRateDownList(List<String> rateDownList) {
        this.rateDownList = rateDownList;
    }


    @Exclude public void rateUp(String userId) {
        if (!rateUpList.contains(userId)) {
            rateUpList.add(userId);
            setRating(rating+1);
        }
        rateDownList.remove(userId);
    }
    @Exclude public void rateDown(String userId) {
        if (!rateDownList.contains(userId)) {
            rateDownList.add(userId);
            setRating(rating-1);
        }
        rateUpList.remove(userId);
    }

    @Exclude public boolean isRatedUpBy(String userId) {
        return getRateUpList().contains(userId);
    }
    @Exclude public boolean isRatedDownBy(String userId) {
        return getRateDownList().contains(userId);
    }

    @Exclude public boolean isCreatedBy(String checkedUserId) {
        return (!TextUtils.isEmpty(checkedUserId) && !TextUtils.isEmpty(checkedUserId) && this.userId.equals(checkedUserId));
    }

    @Exclude public boolean isCreatedBy(@Nullable User user) {
        return ( null != user && userId.equals(user.getKey()) );
    }

    @Exclude public void setParent(iCommentable commentableObject) {
        this.parentId = commentableObject.getKey();

        if (commentableObject instanceof Comment)
            this.parentText = commentableObject.getText();
    }

    @Exclude public void removeParent() {
        this.parentId = null;
        this.parentText = null;
    }

    @Exclude public void setUser(User user) {
        this.userId = user.getKey();
        this.userName = user.getName();
        this.userAvatarURL = user.getAvatarURL();
    }

    @Exclude public void updateCommentText(String commentText) {
        this.text = commentText;
        this.editedAt = new Date().getTime();
    }
}
