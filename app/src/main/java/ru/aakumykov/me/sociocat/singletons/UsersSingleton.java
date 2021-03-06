package ru.aakumykov.me.sociocat.singletons;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import ru.aakumykov.me.sociocat.constants.Constants;
import ru.aakumykov.me.sociocat.models.Card;
import ru.aakumykov.me.sociocat.models.Comment;
import ru.aakumykov.me.sociocat.models.User;
import ru.aakumykov.me.sociocat.utils.MyUtils;

public class UsersSingleton implements iUsersSingleton {

    private final static String TAG = "UsersSingleton";

    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private CollectionReference usersCollection = firebaseFirestore.collection(Constants.USERS_PATH);
    private CollectionReference cardsCollection = firebaseFirestore.collection(Constants.CARDS_PATH);
    private CollectionReference commentsCollection = firebaseFirestore.collection(Constants.COMMENTS_PATH);
    private CollectionReference adminsCollection = firebaseFirestore.collection(Constants.ADMINS_PATH);

    private User currentUser;
    private List<String> adminsList = new ArrayList<>();


    // Шаблон Единоличник
    private static volatile UsersSingleton ourInstance;
    public synchronized static UsersSingleton getInstance() {
        synchronized (UsersSingleton.class) {
            if (null == ourInstance) ourInstance = new UsersSingleton();
            return ourInstance;
        }
    }
    private UsersSingleton() { }
    // Шаблон Единоличник



    @Override
    public void createUser(String userId, String userName, String email, iCreateCallbacks callbacks) {

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
    public void createUser(@NonNull User user, iCreateCallbacks callbacks) {
        if (null == user) {
            callbacks.onUserCreateFail("User cannot be null");
            return;
        }

        usersCollection
                .document(user.getKey())
                .set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        callbacks.onUserCreateSuccess(user);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        callbacks.onUserCreateFail(e.getMessage());
                        e.printStackTrace();
                    }
                });
    }

    @Override
    public void getUserById(String userId, iReadCallbacks callbacks) {
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
    public void getUserByEmail(String email, iReadCallbacks callbacks) {
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

    /* Запись пользователя на сервере обновляется "вручную", выборочно (не все поля).
    Сохранять просто объект User нельзя! Это ведёт к потерям в полях cardsKeys, commentsKeys и других. */
    @Override
    public void saveUser(User user, @Nullable iSaveCallbacks callbacks) {

        if (null == user)
            throw new IllegalArgumentException("User cannot be null");

        String userId = user.getKey();

        if (null == userId)
            throw new IllegalArgumentException("User argument does not contain key property");

        WriteBatch writeBatch = firebaseFirestore.batch();

        // Обновляю пользователя
        HashMap<String,Object> userMap = new HashMap<>();
            userMap.put(User.KEY_NAME, user.getName());
            userMap.put(User.KEY_EMAIL, user.getEmail());
            userMap.put(User.KEY_ABOUT, user.getAbout());
            userMap.put(User.KEY_AVATAR_URL, user.getAvatarURL());
            userMap.put(User.KEY_AVATAR_FILE_NAME, user.getAvatarFileName());

        writeBatch.update(usersCollection.document(userId), userMap);

        // Обновляю карточки пользователя
        for (String cardKey : user.getCardsKeys()) {
            writeBatch.update(cardsCollection.document(cardKey), Card.KEY_USER_NAME, user.getName());
        }

        // Обновляю комментарии пользователя
        for (String commentKey : user.getCommentsKeys()) {
            writeBatch.update(commentsCollection.document(commentKey), Comment.KEY_USER_NAME, user.getName());
            writeBatch.update(commentsCollection.document(commentKey), Comment.KEY_USER_AVATAR, user.getAvatarURL());
        }

        writeBatch.commit()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        callbacks.onUserSaveSuccess(user);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        callbacks.onUserSaveFail(e.getMessage());
                        Log.e(TAG, Arrays.toString(e.getStackTrace()));
                    }
                })
                .addOnCanceledListener(new OnCanceledListener() {
                    @Override
                    public void onCanceled() {
                        Log.d(TAG, "Пакетная запись отменена");
                    }
                });
    }

    @Override
    public void deleteUser(User user, boolean recursive, iDeleteCallbacks callbacks) {
        String userId = user.getKey();

        if (TextUtils.isEmpty(userId)) {
            callbacks.onUserDeleteFail("There is no userId.");
            return;
        }

        WriteBatch writeBatch = firebaseFirestore.batch();

        // Сам пользователь
        writeBatch.delete(usersCollection.document(userId));

        if (recursive) {
            // Карточки пользователя
            for (String cardKey : user.getCardsKeys()) {
                writeBatch.delete(cardsCollection.document(cardKey));
            }

            // Комментарии пользователя
            for (String commentKey : user.getCommentsKeys()) {
                writeBatch.delete(commentsCollection.document(commentKey));
            }
        }

        writeBatch.commit()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        callbacks.onUserDeleteSuccess(user);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        callbacks.onUserDeleteFail(e.getMessage());
                        MyUtils.printError(TAG, e);
                    }
                });
    }

    @Override
    public void listUsers(iListCallbacks callbacks) {
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
    public void checkNameExists(@Nullable String name, iCheckExistanceCallbacks callbacks) {
        if (null == name) {
            callbacks.onNotExists();
            return;
        }
        checkUserWithAttributeExists(Constants.USER_NAME_KEY, name, callbacks);
    }

    @Override
    public void checkEmailExists(@Nullable String email, iCheckExistanceCallbacks callbacks) {
        if (null == email) {
            callbacks.onNotExists();
            return;
        }
        checkUserWithAttributeExists(Constants.USER_EMAIL_KEY, email, callbacks);
    }

    @Override
    public void checkUserExists(@Nullable String userId, iCheckExistanceCallbacks callbacks) {

        if (null == userId) {
            callbacks.onNotExists();
            return;
        }

        usersCollection
                .document(userId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (!task.isSuccessful()) {
                            callbacks.onCheckFail("Task of checking user existance is unsuccessfull");
                            return;
                        }

                        DocumentSnapshot documentSnapshot = task.getResult();

                        if (documentSnapshot.exists())
                            callbacks.onExists();
                        else
                            callbacks.onNotExists();
                    }
                });
    }


    @Override
    public void setEmailVerified(String userId, boolean isVerified, iEmailVerificationCallbacks callbacks) {

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
    public void storeDeviceId(String userId, String deviceId, iSaveDeviceIdCallbacks callbacks) {
        throw new RuntimeException("Не реализовано, так как не используется");
    }

    @Override
    public void subscribeToCardComments(Context context, boolean enableSubscription, String userId, String cardId, iCardCommentsSubscriptionCallbacks callbacks) {
        throw new RuntimeException("Не реализовано, так как не используется");
    }

    @Override
    public void createOrUpdateExternalUser(String internalUserId, String externalUserId, String userName,
                                           iCreateOrUpdateExternalUser_Callbacks callbacks) {
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

        getUserById(internalUserId, new iReadCallbacks() {
            @Override
            public void onUserReadSuccess(User user) {
                user.setName(userName);
                saveUser(user, new iSaveCallbacks() {
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

                createUser(internalUserId, userName, "", new iCreateCallbacks() {
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
    public void refreshUserFromServer(String userId, iRefreshCallbacks callbacks) {

        if (TextUtils.isEmpty(userId)) {
            callbacks.onUserRefreshFail("User id cannot be empty");
            return;
        }

        if (null != currentUser && !currentUser.getKey().equals(userId)) {
            callbacks.onUserRefreshFail("Attempt to refresh user (" + currentUser.getKey() + ") with different userId (" + userId + ")");
            return;
        }

        getUserById(userId, new iReadCallbacks() {
            @Override
            public void onUserReadSuccess(User user) {

                if (null == user) {
                    callbacks.onUserNotExists();
                    return;
                }

                storeCurrentUser(user);
                callbacks.onUserRefreshSuccess(user);

                readAdminsListFromServer(new iReadAdminsListCallbacks() {
                    @Override
                    public void onReadAdminsListSuccess() {

                    }

                    @Override
                    public void onReadAdminsListFail(String errorMsg) {
                        Log.e(TAG, "Error loading admins list from server: "+errorMsg);
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
        if (null != user)
            currentUser = user;
    }

    @Override
    public void clearCurrentUser() {
        currentUser = null;
    }

    @Override
    public User getCurrentUser() {
        return currentUser;
    }

    @Override
    public void storeAdminsList(HashMap<String, Boolean> list) {
        throw new RuntimeException("Не используется в реализации на Cloid Firestore");
    }

    @Override
    public boolean currentUserIsAdmin() {
        return null != currentUser && adminsList.contains(currentUser.getKey());
    }

    @Override
    public String currentUserName() {
        return currentUser.getName();
    }

    @Override
    public boolean isCardOwner(Card card) {
        return card.getUserId().equals(currentUser.getKey());
    }

    @Override
    public void updateUserFromServer(String userId) {

        getUserById(userId, new iReadCallbacks() {
            @Override
            public void onUserReadSuccess(User user) {
                currentUser = user;
            }

            @Override
            public void onUserReadFail(String errorMsg) {
                Log.e(TAG, errorMsg);
            }
        });
    }

    @Override
    public void changeEmail(@NonNull String newEmail, iChangeEmailCallbacks callbacks)
    {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (null == firebaseUser) {
            callbacks.onEmailChangeError("FirebaseUser is null");
            return;
        }

        if (null == currentUser) {
            callbacks.onEmailChangeError("Current user id null");
            return;
        }

        firebaseUser.updateEmail(newEmail)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        currentUser.setEmail(newEmail);


                        saveUser(currentUser, new iSaveCallbacks() {
                                @Override
                                public void onUserSaveSuccess(User user) {
                                    callbacks.onEmailChangeSuccess();
                                }

                                @Override
                                public void onUserSaveFail(String errorMsg) {
                                    callbacks.onEmailChangeError(errorMsg);
                                }
                            }
                        );
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        callbacks.onEmailChangeError(e.getMessage());
                        MyUtils.printError(TAG, e);
                    }
                });
    }

    @Override
    public CollectionReference getUsersCollection() {
        return firebaseFirestore.collection(Constants.USERS_PATH);
    }


    // Внутренние методы
    private void checkUserWithAttributeExists(String attrName, String attrValue, iCheckExistanceCallbacks callbacks) {
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

/*
    private void readCurrentUserFromServer(iReadCallbacks callbacks) throws Exception {
        if (null == currentUser)
            throw new RuntimeException("Current user id null");

        String userId = currentUser.getKey();
        if (TextUtils.isEmpty(userId))
            throw new RuntimeException("User id cannot be empty.");

        getUserById(userId, new iReadCallbacks() {
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
*/

    private void readAdminsListFromServer(iReadAdminsListCallbacks callbacks) {
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


    public static class UsersSingletonException extends Exception {
        public UsersSingletonException(String message) {
            super(message);
        }
    }
    public static class UsersSingleton_WrongArgumentException extends UsersSingletonException {
        public UsersSingleton_WrongArgumentException(String message) {
            super(message);
        }
    }
    public static class UsersSingleton_IllegalDataException extends UsersSingletonException {
        public UsersSingleton_IllegalDataException(String message) {
            super(message);
        }
    }
}
