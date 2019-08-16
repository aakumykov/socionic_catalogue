package ru.aakumykov.me.sociocat.singletons;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ru.aakumykov.me.sociocat.Constants;
import ru.aakumykov.me.sociocat.models.Card;
import ru.aakumykov.me.sociocat.models.User;

public class UsersSingleton_CF implements iUsersSingleton {

    private final static String TAG = "UsersSingleton_CF";

    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

    private CollectionReference usersCollection = firebaseFirestore.collection(Constants.USERS_PATH);
    private User currentUser;

    private CollectionReference adminsCollection = firebaseFirestore.collection(Constants.ADMINS_PATH);
    private List<String> adminsList = new ArrayList<>();


    /* Одиночка */
    private static volatile UsersSingleton_CF ourInstance;
    public synchronized static UsersSingleton_CF getInstance() {
        synchronized (UsersSingleton_CF.class) {
            if (null == ourInstance) ourInstance = new UsersSingleton_CF();
            return ourInstance;
        }
    }
    private UsersSingleton_CF() { }
    /* Одиночка */



    @Override
    public void createUser(String userId, String userName, String email, CreateCallbacks callbacks) {

        final User user = new User(userId);
        user.setEmail(email);
        user.setName(userName);
        user.setEmailVerified(true);

        DocumentReference userDocumentReference = usersCollection.document(userId);

        // Проверка существования такой записи в БД
        userDocumentReference.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            callbacks.onUserCreateFail("User with id "+userId+" already exists.");
                        }
                        else {

                            // Создание новой записи о пользователе в БД
                            userDocumentReference.set(user)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            callbacks.onUserCreateSuccess(user);
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            e.printStackTrace();
                                            callbacks.onUserCreateFail(e.getMessage());
                                        }
                                    });

                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                        callbacks.onUserCreateFail(e.getMessage());
                    }
                });
    }

    @Override
    public void getUserById(String userId, ReadCallbacks callbacks) {
        usersCollection.document(userId).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        try {
                            User user = documentSnapshot.toObject(User.class);
                            callbacks.onUserReadSuccess(user);
                        } catch (Exception e) {
                            e.printStackTrace();
                            callbacks.onUserReadFail(e.getMessage());
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                        callbacks.onUserReadFail(e.getMessage());
                    }
                });
    }

    @Override
    public void getUserByEmail(String email, ReadCallbacks callbacks) {
        Query query = usersCollection.whereEqualTo(Constants.USER_EMAIL_KEY, email);

        query.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> documentSnapshots = queryDocumentSnapshots.getDocuments();
                        int resultsCount = documentSnapshots.size();
                        if (resultsCount > 1) {
                            callbacks.onUserReadFail("Found too more users with email '"+email+"'");
                        }
                        else if (0 == resultsCount) {
                            callbacks.onUserReadFail("There is no user with email '"+email+"'");
                        }
                        else {
                            try {
                                User user = documentSnapshots.get(0).toObject(User.class);
                                callbacks.onUserReadSuccess(user);
                            } catch (Exception e) {
                                e.printStackTrace();
                                callbacks.onUserReadFail(e.getMessage());
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                        callbacks.onUserReadFail(e.getMessage());
                    }
                });
    }

    @Override
    public void saveUser(User user, SaveCallbacks callbacks) {
        String userId = user.getKey();

        if (TextUtils.isEmpty(userId)) {
            callbacks.onUserSaveFail("There is no id in User object.");
            return;
        }

        usersCollection.document(userId).set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        callbacks.onUserSaveSuccess(user);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                        callbacks.onUserSaveFail(e.getMessage());
                    }
                });
    }

    @Override
    public void deleteUser(User user, DeleteCallbacks callbacks) {
        String userId = user.getKey();

        if (TextUtils.isEmpty(userId)) {
            callbacks.onUserDeleteFail("There is no id in User object.");
            return;
        }

        usersCollection.document(userId).delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        callbacks.onUserDeleteSuccess(user);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                        callbacks.onUserDeleteFail(e.getMessage());
                    }
                });
    }

    @Override
    public void listUsers(ListCallbacks callbacks) {
        usersCollection.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<User> usersList = new ArrayList<>();
                        boolean error = false;

                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                            try {
                                usersList.add(documentSnapshot.toObject(User.class));
                            } catch (Exception e) {
                                error = true;
                                e.printStackTrace();
                            }
                        }

                        if (usersList.isEmpty() && error) {
                            callbacks.onListFail("Errors during excract users list.");
                        }
                        else {
                            callbacks.onListRecieved(usersList);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                        callbacks.onListFail(e.getMessage());
                    }
                });

    }

    @Override
    public void checkNameExists(String name, CheckExistanceCallbacks callbacks) {
        checkUserWithAttributeExists(Constants.USER_NAME_KEY, name, callbacks);
    }

    @Override
    public void checkEmailExists(String email, CheckExistanceCallbacks callbacks) {
        checkUserWithAttributeExists(Constants.USER_EMAIL_KEY, email, callbacks);
    }



    @Override
    public void setEmailVerified(String userId, boolean isVerified, EmailVerificationCallbacks callbacks) {

        if (TextUtils.isEmpty(userId)) {
            callbacks.OnEmailVerificationFail("There is no id in User object.");
            return;
        }

        usersCollection.document(userId).update(Constants.USER_EMAIL_VERIFIED_KEY, true)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        callbacks.OnEmailVerificationSuccess();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                        callbacks.OnEmailVerificationFail(e.getMessage());
                    }
                });
    }

    @Override
    public void storeDeviceId(String userId, String deviceId, SaveDeviceIdCallbacks callbacks) {
        throw new RuntimeException("Не реализовано, так как не используется");
    }

    @Override
    public void subscribeToCardComments(Context context, boolean enableSubscription, String userId, String cardId, CardCommentsSubscriptionCallbacks callbacks) {
        throw new RuntimeException("Не реализовано, так как не используется");
    }

    @Override
    public void createOrUpdateExternalUser(String internalUserId, String externalUserId, String userName,
                                           CreateOrUpdateExternalUser_Callbacks callbacks) {
        if (TextUtils.isEmpty(internalUserId)) {
            callbacks.onCreateOrUpdateExternalUser_Error("internalUserId cannot be empty");
            return;
        }

        if (TextUtils.isEmpty(externalUserId)) {
            callbacks.onCreateOrUpdateExternalUser_Error("externalUserId cannot be empty");
            return;
        }

        if (TextUtils.isEmpty(userName)) {
            callbacks.onCreateOrUpdateExternalUser_Error("userName cannot be empty");
            return;
        }

        getUserById(internalUserId, new ReadCallbacks() {
            @Override
            public void onUserReadSuccess(User user) {
                user.setName(userName);
                saveUser(user, new SaveCallbacks() {
                    @Override
                    public void onUserSaveSuccess(User user) {
                        callbacks.onCreateOrUpdateExternalUser_Success(user);
                    }

                    @Override
                    public void onUserSaveFail(String errorMsg) {
                        callbacks.onCreateOrUpdateExternalUser_Error(errorMsg);
                    }
                });
            }

            @Override
            public void onUserReadFail(String errorMsg) {

                createUser(internalUserId, userName, "", new CreateCallbacks() {
                    @Override
                    public void onUserCreateSuccess(User user) {
                        callbacks.onCreateOrUpdateExternalUser_Success(user);
                    }

                    @Override
                    public void onUserCreateFail(String errorMsg) {
                        callbacks.onCreateOrUpdateExternalUser_Error(errorMsg);
                    }
                });
            }
        });
    }

    @Override
    public void refreshUserFromServer(String userId, RefreshCallbacks callbacks) throws RuntimeException {
        if (TextUtils.isEmpty(userId))
            throw new IllegalArgumentException("User id cannot be empty");

        if (null == currentUser)
            throw new RuntimeException("Current user id null");

        if (currentUser.getKey().equals(userId))
            throw new RuntimeException("Attempt to refresh user (" + currentUser.getKey() + ") with different userId (" + userId + ")");

        getUserById(userId, new ReadCallbacks() {
            @Override
            public void onUserReadSuccess(User user) {
                currentUser = user;
                readAdminsListFromServer(new ReadAdminsListCallbacks() {
                    @Override
                    public void onReadAdminsListSuccess() {
                        callbacks.onUserRefreshSuccess(user);
                    }

                    @Override
                    public void onReadAdminsListFail(String errorMsg) {
                        callbacks.onUserRefreshFail(errorMsg);
                    }
                });
            }

            @Override
            public void onUserReadFail(String errorMsg) {
                callbacks.onUserRefreshFail(errorMsg);
            }
        });
    }

    @Override
    public void storeCurrentUser(User user) {

    }

    @Override
    public void clearCurrentUser() {

    }

    @Override
    public User getCurrentUser() {
        return null;
    }

    @Override
    public void storeAdminsList(HashMap<String, Boolean> list) {

    }

    @Override
    public boolean currentUserIsAdmin() {
        return false;
    }

    @Override
    public String currentUserName() {
        return null;
    }

    @Override
    public boolean isCardOwner(Card card) {
        return false;
    }


    // Внутренние методы
    private void checkUserWithAttributeExists(String attrName, String attrValue, CheckExistanceCallbacks callbacks) {
        Query query = usersCollection.whereEqualTo(attrName, attrValue);

        query.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots.size() > 0)
                            callbacks.onExists();
                        else
                            callbacks.onNotExists();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                        callbacks.onCheckFail(e.getMessage());
                    }
                });
    }

    private void readCurrentUserFromServer(ReadCallbacks callbacks) throws Exception {
        if (null == currentUser)
            throw new RuntimeException("Current user id null");

        String userId = currentUser.getKey();
        if (TextUtils.isEmpty(userId))
            throw new RuntimeException("User id cannot be empty.");

        getUserById(userId, new ReadCallbacks() {
            @Override
            public void onUserReadSuccess(User user) {
                currentUser = user;
            }

            @Override
            public void onUserReadFail(String errorMsg) {
                callbacks.onUserReadFail(errorMsg);
            }
        });
    }

    private void readAdminsListFromServer(ReadAdminsListCallbacks callbacks) {
        adminsCollection.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments())
                            adminsList.add(documentSnapshot.getId());
                        callbacks.onReadAdminsListSuccess();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                        callbacks.onReadAdminsListFail(e.getMessage());
                    }
                });
    }
}
