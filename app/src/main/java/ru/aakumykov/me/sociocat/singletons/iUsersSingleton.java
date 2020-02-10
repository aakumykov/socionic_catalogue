package ru.aakumykov.me.sociocat.singletons;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.HashMap;
import java.util.List;

import ru.aakumykov.me.sociocat.models.Card;
import ru.aakumykov.me.sociocat.models.User;

// TODO: глобальный goOffline() для Firebase

public interface iUsersSingleton {

    void createUser(String userId, String userName, String email, CreateCallbacks callbacks);

    void getUserById(String userId, ReadCallbacks callbacks);
    void getUserByEmail(String email, ReadCallbacks callbacks);
    void saveUser(User user, @Nullable SaveCallbacks callbacks);
    void deleteUser(User user, boolean recursive, DeleteCallbacks callbacks);
    void listUsers(ListCallbacks callbacks);
    void checkNameExists(String name, CheckExistanceCallbacks callbacks);
    void checkEmailExists(String email, CheckExistanceCallbacks callbacks);
    void setEmailVerified(String userId, boolean isVerified, final EmailVerificationCallbacks callbacks);

    void storeDeviceId(String userId, String deviceId, SaveDeviceIdCallbacks callbacks);
    void subscribeToCardComments(Context context, boolean enableSubscription, String userId, String cardId,
                                 CardCommentsSubscriptionCallbacks callbacks);

    void createOrUpdateExternalUser(String internalUserId, String externalUserId, String userName,
                                    CreateOrUpdateExternalUser_Callbacks callbacks) throws UsersSingleton.UsersSingletonException;

    void refreshUserFromServer(String userId, RefreshCallbacks callbacks);
    void storeCurrentUser(User user);
    void clearCurrentUser();
    User getCurrentUser();

    void storeAdminsList(HashMap<String,Boolean> list);
    boolean currentUserIsAdmin();
    String currentUserName();

    boolean isCardOwner(Card card);

    void updateUserFromServer(String userId);

    void changeEmail(@NonNull String newEmail, ChangeEmailCallbacks callbacks);


    interface CreateCallbacks {
        void onUserCreateSuccess(User user);
        void onUserCreateFail(String errorMsg);
    }

    interface ReadCallbacks {
        void onUserReadSuccess(User user);
        void onUserReadFail(String errorMsg);
    }

    interface UserExistenceCallbacks {
        void inUserExists(User user);
        void onUserNotExists();
    }

    interface SaveCallbacks {
        void onUserSaveSuccess(User user);
        void onUserSaveFail(String errorMsg);
    }

    interface ChangeEmailCallbacks {
        void onEmailChangeSuccess();
        void onEmailChangeError(String errorMsg);
    }

    interface DeleteCallbacks {
        void onUserDeleteSuccess(User user);
        void onUserDeleteFail(String errorMsg);
    }

    interface RefreshCallbacks {
        void onUserRefreshSuccess(User user);
        void onUserNotExists();
        void onUserRefreshFail(String errorMsg);
    }

    interface ReadAdminsListCallbacks {
        void onReadAdminsListSuccess();
        void onReadAdminsListFail(String errorMsg);
    }

    interface ListCallbacks {
        void onListRecieved(List<User> usersList);
        void onListFail(String errorMsg);
    }

    interface CheckExistanceCallbacks {
        void onCheckComplete();
        void onExists();
        void onNotExists();
        void onCheckFail(String errorMsg);
    }

    interface EmailVerificationCallbacks {
        void OnEmailVerificationSuccess();
        void OnEmailVerificationFail(String errorMsg);
    }

    interface PushTokenCallbacks {
        void onPushTokenUpdateSuccess(String token);
        void onPushTokenUpdateError(String errorMsg);
    }

    interface SaveDeviceIdCallbacks {
        void onStoreDeviceIdSuccess();
        void onStoreDeviceIdFailed(String errorMSg);
    }

    interface CardCommentsSubscriptionCallbacks {
        void onSubscribeSuccess();
        void onSubscribeFail(String errorMsg);
        void onUnsubscribeSuccess();
        void onUnsubscribeFail(String errorMsg);
    }

    interface CreateOrUpdateExternalUser_Callbacks {
        void onCreateOrUpdateExternalUser_Success(User user);
        void onCreateOrUpdateExternalUser_Error(String errorMsg);
    }
}
