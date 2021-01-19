package io.gitlab.aakumykov.sociocat.singletons;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.firestore.CollectionReference;

import java.util.HashMap;
import java.util.List;

import io.gitlab.aakumykov.sociocat.models.Card;
import io.gitlab.aakumykov.sociocat.models.User;

// TODO: глобальный goOffline() для Firebase

public interface iUsersSingleton {

    void createUser(String userId, String userName, String email, iCreateCallbacks callbacks);

    void getUserById(String userId, iReadCallbacks callbacks);
    void getUserByEmail(String email, iReadCallbacks callbacks);
    void saveUser(User user, @Nullable iSaveCallbacks callbacks);
    void deleteUser(User user, boolean recursive, iDeleteCallbacks callbacks);
    void listUsers(iListCallbacks callbacks);
    void checkNameExists(@Nullable String name, iCheckExistanceCallbacks callbacks);
    void checkEmailExists(@Nullable String email, iCheckExistanceCallbacks callbacks);
    void checkUserExists(@Nullable String userId, iCheckExistanceCallbacks callbacks);
    void setEmailVerified(String userId, boolean isVerified, final iEmailVerificationCallbacks callbacks);

    void storeDeviceId(String userId, String deviceId, iSaveDeviceIdCallbacks callbacks);
    void subscribeToCardComments(Context context, boolean enableSubscription, String userId, String cardId,
                                 iCardCommentsSubscriptionCallbacks callbacks);

    void createOrUpdateExternalUser(String internalUserId, String externalUserId, String userName,
                                    iCreateOrUpdateExternalUser_Callbacks callbacks) throws UsersSingleton.UsersSingletonException;

    void refreshUserFromServer(String userId, iRefreshCallbacks callbacks);
    void storeCurrentUser(User user);
    void clearCurrentUser();
    User getCurrentUser();

    void storeAdminsList(HashMap<String,Boolean> list);
    boolean currentUserIsAdmin();
    String currentUserName();


    boolean isCardOwner(Card card);

    void updateUserFromServer(String userId);

    void changeEmail(@NonNull String newEmail, iChangeEmailCallbacks callbacks);

    CollectionReference getUsersCollection();


    interface iCreateCallbacks {
        void onUserCreateSuccess(User user);
        void onUserCreateFail(String errorMsg);
    }

    interface iReadCallbacks {
        void onUserReadSuccess(User user);
        void onUserReadFail(String errorMsg);
    }

    interface iSaveCallbacks {
        void onUserSaveSuccess(User user);
        void onUserSaveFail(String errorMsg);
    }

    interface iChangeEmailCallbacks {
        void onEmailChangeSuccess();
        void onEmailChangeError(String errorMsg);
    }

    interface iDeleteCallbacks {
        void onUserDeleteSuccess(User user);
        void onUserDeleteFail(String errorMsg);
    }

    interface iRefreshCallbacks {
        void onUserRefreshSuccess(@NonNull User user);
        void onUserNotExists();
        void onUserRefreshFail(String errorMsg);
    }

    interface iReadAdminsListCallbacks {
        void onReadAdminsListSuccess();
        void onReadAdminsListFail(String errorMsg);
    }

    interface iListCallbacks {
        void onListRecieved(List<User> usersList);
        void onListFail(String errorMsg);
    }

    interface iCheckExistanceCallbacks {
        void onCheckComplete();
        void onExists();
        void onNotExists();
        void onCheckFail(String errorMsg);
    }

    interface iEmailVerificationCallbacks {
        void OnEmailVerificationSuccess();
        void OnEmailVerificationFail(String errorMsg);
    }

    interface iPushTokenCallbacks {
        void onPushTokenUpdateSuccess(String token);
        void onPushTokenUpdateError(String errorMsg);
    }

    interface iSaveDeviceIdCallbacks {
        void onStoreDeviceIdSuccess();
        void onStoreDeviceIdFailed(String errorMSg);
    }

    interface iCardCommentsSubscriptionCallbacks {
        void onSubscribeSuccess();
        void onSubscribeFail(String errorMsg);
        void onUnsubscribeSuccess();
        void onUnsubscribeFail(String errorMsg);
    }

    interface iCreateOrUpdateExternalUser_Callbacks {
        void onCreateOrUpdateExternalUser_Success(User user);
        void onCreateOrUpdateExternalUser_Error(String errorMsg);
    }
}
