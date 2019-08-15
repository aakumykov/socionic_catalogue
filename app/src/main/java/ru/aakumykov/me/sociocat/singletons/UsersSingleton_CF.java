package ru.aakumykov.me.sociocat.singletons;

import android.content.Context;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.List;

import ru.aakumykov.me.sociocat.Constants;
import ru.aakumykov.me.sociocat.models.Card;
import ru.aakumykov.me.sociocat.models.User;

public class UsersSingleton_CF implements iUsersSingleton {

    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private CollectionReference usersCollection = firebaseFirestore.collection(Constants.USERS_PATH);

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

    }

    @Override
    public void checkNameExists(String name, CheckExistanceCallbacks callbacks) {

    }

    @Override
    public void checkEmailExists(String email, CheckExistanceCallbacks callbacks) {

    }

    @Override
    public void setEmailVerified(String userId, boolean isVerified, EmailVerificationCallbacks callbacks) {

    }

    @Override
    public void updatePushToken(String token, PushTokenCallbacks callbacks) {

    }

    @Override
    public void storeDeviceId(String userId, String deviceId, SaveDeviceIdCallbacks callbacks) {

    }

    @Override
    public void subscribeToCardComments(Context context, boolean enableSubscription, String userId, String cardId, CardCommentsSubscriptionCallbacks callbacks) {

    }

    @Override
    public void createOrUpdateExternalUser(String internalUserId, String externalUserId, String userName, CreateOrUpdateExternalUser_Callbacks callbacks) {

    }

    @Override
    public void refreshUserFromServer(@Nullable RefreshCallbacks callbacks) {

    }

    @Override
    public void refreshUserFromServer(String userId, @Nullable RefreshCallbacks callbacks) {

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
}
