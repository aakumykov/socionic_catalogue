package ru.aakumykov.me.sociocat.utils.auth;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.vk.api.sdk.VK;
import com.vk.api.sdk.VKApiCallback;
import com.vk.api.sdk.VKTokenExpiredHandler;
import com.vk.api.sdk.auth.VKAccessToken;
import com.vk.api.sdk.auth.VKAuthCallback;
import com.vk.api.sdk.exceptions.VKApiExecutionException;
import com.vk.api.sdk.requests.VKRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class VKInteractor {

    // Интерфейсы
    public interface LoginVK_Callbacks {
        void onVKLoginSuccess(VKAuthResult vkAuthResult);
        void onVKLoginError(int errorCode, @Nullable String errorMsg);
    }

    public interface LogoutVK_Callbacks {
        void onVKLogoutSuccess();
        void onVKLogoutError();
    }

    public interface GetVKUserInfo_Callbacks {
        void onGetVKUserInfoSuccess(VKUser vkUser);
        void onGetVKUserInfoError(String errorMsg);
    }

    public interface VKAuthExpiredCallback {
        void onVKAuthExpired();
    }


    // Свойства
    private static final String TAG = "VKInteractor";


    // Публичные методы
    public static void login(Activity activity) {
        VK.initialize(activity.getApplicationContext());
        VK.login(activity);
    }

    public static void logout(LogoutVK_Callbacks callbacks) {
        VK.logout();
        if (!VK.isLoggedIn())
            callbacks.onVKLogoutSuccess();
        else
            callbacks.onVKLogoutError();
    }

    public static boolean isLoggedIn() {
        return VK.isLoggedIn();
    }

    public static boolean isVKActivityResult(int requestCode, int resultCode, @Nullable Intent data,
                                             @Nullable LoginVK_Callbacks callbacks) {
        try {
            return VK.onActivityResult(requestCode, resultCode, data, new VKAuthCallback() {
                @Override
                public void onLogin(@NonNull VKAccessToken vkAccessToken) {
                    if (null != callbacks) {
                        VKAuthResult vkAuthResult = new VKAuthResult(vkAccessToken.getAccessToken(), vkAccessToken.getUserId());
                        callbacks.onVKLoginSuccess(vkAuthResult);
                    }
                }

                @Override
                public void onLoginFailed(int i) {
                    if (null != callbacks) {
                        callbacks.onVKLoginError(i, "VK: onLoginFailed()");
                    }
                }
            });
        }
        catch (Exception e) {
            if (null != callbacks) {
                String errorMsg = e.getMessage();
                if (null != errorMsg)
                    if (!errorMsg.equals("null")) // хак для VK
                        callbacks.onVKLoginError(-1, errorMsg);
            }
            e.printStackTrace();
            return false;
        }
    }

    public static void trackVKAuthExpired(VKAuthExpiredCallback callback) {
        VK.addTokenExpiredHandler(new VKTokenExpiredHandler() {
            @Override
            public void onTokenExpired() {
                callback.onVKAuthExpired();
            }
        });
    }

    public static void getUserInfo(@Nullable Integer userId, GetVKUserInfo_Callbacks callbacks) {

        VKUserInfoRequest vkUserInfoRequest = new VKUserInfoRequest("users.get");
        vkUserInfoRequest.addParam("lang", 1);
        if (null != userId)
            vkUserInfoRequest.addParam("user_ids", userId);

        VK.execute(vkUserInfoRequest, new VKApiCallback<Object>() {
            @Override
            public void success(Object o) {
                try {
                    String rawJSONString = o.toString();
                    //Log.d(TAG, rawJSONString);
                    JSONObject jsonObject = new JSONObject(rawJSONString);
                    JSONArray responseArray = jsonObject.getJSONArray("response");
                    String response = responseArray.get(0).toString();
                    VKUser vkUser = VKUser.parse(response);
                    callbacks.onGetVKUserInfoSuccess(vkUser);
                }
                catch (JSONException e) {
                    String errorMsg = e.getMessage();
                    Log.e(TAG, errorMsg);
                    e.printStackTrace();
                    callbacks.onGetVKUserInfoError(errorMsg);
                }
            }

            @Override
            public void fail(@NonNull VKApiExecutionException e) {
                String errorMsg = e.getMessage();
                Log.e(TAG, errorMsg);
                e.printStackTrace();
                callbacks.onGetVKUserInfoError(errorMsg);
            }
        });
    }


    // Внутренние классы
    private static class VKUserInfoRequest extends VKRequest {
        public VKUserInfoRequest(@NonNull String method) {
            super(method);
        }
    }

    public static class VKUser {
        private final static String TAG = "VKUser";

        public static VKUser parse(String jsonData) {
            try {
                JSONObject jsonObject = new JSONObject(jsonData);

                int id = jsonObject.getInt("id");
                String first_name = jsonObject.has("first_name") ? jsonObject.getString("first_name") : "";
                String last_name = jsonObject.has("last_name") ? jsonObject.getString("last_name") : "";

                return new VKUser(id, first_name, last_name);
            }
            catch (JSONException e) {
                Log.e(TAG, e.getMessage());
                e.printStackTrace();
                return null;
            }
        }

        private int id;
        private String firstName;
        private String lastName;

        public VKUser(int id, String firstName, String lastName) {
            this.id = id;
            this.firstName = firstName;
            this.lastName = lastName;
        }

        public int getId() {
            return id;
        }

        public String getFirstName() {
            return firstName;
        }

        public String getLastName() {
            return lastName;
        }
    }

    public static class VKAuthResult {
        private String accessToken = null;
        private int userId = -1;

        public VKAuthResult(String accessToken, int userId) {
            this.accessToken = accessToken;
            this.userId = userId;
        }

        public String getAccessToken() {
            return accessToken;
        }

        public int getUserId() {
            return userId;
        }
    }

    public static class VKAuthExpiredEvent {

    }
}

