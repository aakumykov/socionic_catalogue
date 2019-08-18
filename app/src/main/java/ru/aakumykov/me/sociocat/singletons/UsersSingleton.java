package ru.aakumykov.me.sociocat.singletons;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import ru.aakumykov.me.sociocat.Constants;
import ru.aakumykov.me.sociocat.models.Card;
import ru.aakumykov.me.sociocat.models.User;
import ru.aakumykov.me.sociocat.utils.MVPUtils.MVPUtils;

// TODO: пункт меню "Обновить"

public class UsersSingleton implements iUsersSingleton {

    /* Одиночка */
    private static volatile UsersSingleton ourInstance;
    public synchronized static UsersSingleton getInstance() {
        synchronized (UsersSingleton.class) {
            if (null == ourInstance) ourInstance = new UsersSingleton();
            return ourInstance;
        }
    }
    private UsersSingleton() {
    }
    /* Одиночка */

    private final static String TAG = "UsersSingleton";
    private User currentUser;
    private HashMap<String,Boolean> adminsList = new HashMap<>();
    private DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference().child("/");
    private DatabaseReference usersRef = rootRef.child(Constants.USERS_PATH);
    private DatabaseReference adminsRef = rootRef.child(Constants.ADMINS_PATH);
    private DatabaseReference deviceIdRef = rootRef.child(Constants.DEVICE_ID_PATH);


    @Override
    public void refreshUserFromServer(String userId, RefreshCallbacks callbacks) {

        if (null == userId) {
            String errorMsg = "userId == NULL";
            Log.e(TAG, errorMsg);
            callbacks.onUserRefreshFail(errorMsg);
            return;
        }

        if (null != currentUser && !userId.equals(currentUser.getKey())) {
            String errorMsg = "Attempt to refresh user (" + currentUser.getKey() + ") with different userId (" + userId + ")";
            Log.e(TAG, errorMsg);
            if (null != callbacks)
                callbacks.onUserRefreshFail(errorMsg);
            return;
        }

        readUser(userId, new ReadCallbacks() {
            @Override
            public void onUserReadSuccess(User user) {
                readAdminsList(new ReadAdminsListCallbacks() {
                    @Override
                    public void onReadAdminsListSuccess() {
                        if (null != callbacks)
                            callbacks.onUserRefreshSuccess(user);
                    }

                    @Override
                    public void onReadAdminsListFail(String errorMsg) {
                        if (null != callbacks)
                            callbacks.onUserRefreshFail(errorMsg);
                    }
                });
            }

            @Override
            public void onUserReadFail(String errorMsg) {
                if (null != callbacks)
                    callbacks.onUserRefreshFail(errorMsg);
            }
        });
    }


    @Override
    public void storeCurrentUser(User user) {
        currentUser = user;
    }

    @Override public void clearCurrentUser() {
        currentUser = null;
    }

    @Override
    public User getCurrentUser() {
        return currentUser;
    }

    @Override
    public void storeAdminsList(HashMap<String, Boolean> list) {
        this.adminsList = list;
    }

    @Override public boolean currentUserIsAdmin() {
        return adminsList.containsKey(currentUser.getKey());
    }

    @Override public String currentUserName() {
        return currentUser.getName();
    }

    @Override
    public boolean isCardOwner(Card card) {

        if (null == card)
            return false;

        if (null == currentUser)
            return false;

        return (card.getUserId().equals(currentUser.getKey()));
    }


    @Override
    public void listUsers(final ListCallbacks callbacks) {
        final ArrayList<User> list = new ArrayList<>();

        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                Log.d(TAG, "dataSnapshot: "+dataSnapshot);

                for (DataSnapshot dataSnapshotItem : dataSnapshot.getChildren()) {
//                    Log.d(TAG, "dataSnapshotItem: "+dataSnapshotItem);

                    User user = dataSnapshotItem.getValue(User.class);
                    if (null != user) {
                        user.setKey(dataSnapshotItem.getKey());
                        list.add(user);
                    }
                }

                callbacks.onListRecieved(list);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callbacks.onListFail(databaseError.getMessage());
                databaseError.toException().printStackTrace();
            }
        });
    }

    @Override
    public void createUser(final String userId, final String name, String email, final CreateCallbacks callbacks) {

        final User user = new User(userId);
         user.setEmail(email);
         user.setName(name);
         user.setEmailVerified(true);

        final DatabaseReference newUserRef = usersRef.child(userId);

        // Проверяю на дубликат
        newUserRef.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                // Создаю, если пусто
                if (null == dataSnapshot.getValue()) {

                    newUserRef.setValue(user)
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

                } else {
                    callbacks.onUserCreateFail("User with id '"+userId+"' already exists.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callbacks.onUserCreateFail(databaseError.getMessage());
                databaseError.toException().printStackTrace();
            }
        });
    }

    @Override
    public void getUserById(final String userId, final ReadCallbacks callbacks) {
        Log.d(TAG, "getUserById("+userId+", callbacks)");

        usersRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (null != user) {
                    user.setKey(dataSnapshot.getKey());
                    callbacks.onUserReadSuccess(user);
                } else {
                    callbacks.onUserReadFail("User with key: "+userId+" is null");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callbacks.onUserReadFail(databaseError.getMessage());
                databaseError.toException().printStackTrace();
            }
        });
    }

    @Override
    public void getUserByEmail(String email, final iUsersSingleton.ReadCallbacks callbacks) {
        Query query =usersRef.orderByChild("email").equalTo(email);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long count = dataSnapshot.getChildrenCount();
                if (0 == count) callbacks.onUserReadSuccess(null);
                else {
                    for (DataSnapshot oneSnapshot : dataSnapshot.getChildren()) {
                        User user = oneSnapshot.getValue(User.class);
                        callbacks.onUserReadSuccess(user);
                        break;
                    }
                }
            }

            @Override public void onCancelled(@NonNull DatabaseError databaseError) {
                callbacks.onUserReadFail(databaseError.getMessage());
                databaseError.toException().printStackTrace();
            }
        });
    }

    @Override
    public void saveUser(final User user, final SaveCallbacks callbacks) {
        Map<String, Object> updatePool = new HashMap<>();

        // Сохраняю (обновляю) собственно пользователя
        updatePool.put(Constants.USERS_PATH+"/"+user.getKey(), user);

        // Обновляю имя в его карточках
        Map<String,Boolean> userCards = user.getCardsKeysHash();
        if (null != userCards) {
            for (Map.Entry entry : userCards.entrySet()) {
                updatePool.put(Constants.CARDS_PATH + "/" + entry.getKey() + "/userName", user.getName());
            }
        }

        // Обновляю имя и аватар в его комментариях
        Map<String,Boolean> userComments = user.getCommentsKeysHash();
        if (null != userComments) {
            for (Map.Entry entry : userComments.entrySet()) {
                updatePool.put(Constants.COMMENTS_PATH + "/" + entry.getKey() + "/userName", user.getName());
                updatePool.put(Constants.COMMENTS_PATH + "/" + entry.getKey() + "/userAvatar", user.getAvatarURL());
            }
        }

        rootRef.updateChildren(updatePool)
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
                        e.printStackTrace();
                    }
                })
                .addOnCanceledListener(new OnCanceledListener() {
                    @Override
                    public void onCanceled() {
                        callbacks.onUserSaveFail("User saving cancelled");
                    }
                });
    }

    @Override
    public void deleteUser(User user, DeleteCallbacks callbacks) {

    }

    @Override
    public void checkNameExists(String name, final CheckExistanceCallbacks callbacks) {

        Query query =rootRef.child(Constants.USERS_PATH).orderByChild("name").equalTo(name);
        checkExistance(query, callbacks);
    }

    @Override
    public void checkEmailExists(String email, CheckExistanceCallbacks callbacks) {
        Query query =usersRef.orderByChild("email").equalTo(email);
//        Query query = rootRef.child(Constants.USERS_PATH).orderByChild("email").equalTo(email);
        checkExistance(query, callbacks);
    }

    @Override
    public void setEmailVerified(String userId, final boolean isVerified, final EmailVerificationCallbacks callbacks) {
        DatabaseReference emailVerifiedRef = usersRef.child(userId).child("emailVerified");

        emailVerifiedRef.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                Boolean emailVerified = mutableData.getValue(Boolean.class);

                if (null == emailVerified) return Transaction.success(mutableData);

                mutableData.setValue(isVerified);

                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {
                if (null == databaseError) {
                    if (null != dataSnapshot) {
                        callbacks.OnEmailVerificationSuccess();
                    } else {
                        callbacks.OnEmailVerificationFail("dataSnapshot is NULL");
                    }
                } else {
                    callbacks.OnEmailVerificationFail(databaseError.getMessage());
                    databaseError.toException().printStackTrace();
                }
            }
        });
    }

    @Override public void storeDeviceId(String userId, String deviceId, SaveDeviceIdCallbacks callbacks) {

        long currentTime = new Date().getTime();

        deviceIdRef.child(deviceId).setValue(userId+"__"+currentTime)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override public void onSuccess(Void aVoid) {
                        callbacks.onStoreDeviceIdSuccess();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override public void onFailure(@NonNull Exception e) {
                        callbacks.onStoreDeviceIdFailed(e.getMessage());
                        e.printStackTrace();
                    }
                });

    }

    @Override
    public void subscribeToCardComments(Context context, boolean enableSubscription,
                                        String userId, String cardId,
                                        CardCommentsSubscriptionCallbacks callbacks) {

        if (enableSubscription) {
            MVPUtils.subscribeToTopicNotifications(context, cardId, new MVPUtils.TopicNotificationsCallbacks.SubscribeCallbacks() {
                @Override
                public void onSubscribeSuccess() {
                    changeCardCommentsSubscription(cardId, userId, true, new ChangeCardCommentsSubscriptionCallbacks() {
                        @Override
                        public void onChangeSuccess() {
                            callbacks.onSubscribeSuccess();
                        }

                        @Override
                        public void onChangeFail(String errorMsg) {
                            callbacks.onSubscribeFail(errorMsg);
                        }
                    });
                }

                @Override
                public void onSubscribeFail(String errorMsg) {
                    callbacks.onSubscribeFail(errorMsg);
                }
            });
        }
        else {
            MVPUtils.unsubscribeFromTopicNotifications(context, cardId, new MVPUtils.TopicNotificationsCallbacks.UnsbscribeCallbacks() {
                @Override
                public void onUnsubscribeSuccess() {
                    changeCardCommentsSubscription(cardId, userId, false, new ChangeCardCommentsSubscriptionCallbacks() {
                        @Override
                        public void onChangeSuccess() {
                            callbacks.onUnsubscribeSuccess();
                        }

                        @Override
                        public void onChangeFail(String errorMsg) {
                            callbacks.onUnsubscribeFail(errorMsg);
                        }
                    });
                }

                @Override
                public void onUnsubscribeFail(String errorMsg) {
                    callbacks.onUnsubscribeFail(errorMsg);
                }
            });
        }

    }

    @Override
    public void createOrUpdateExternalUser(String internalUserId, String externalUserId, String userName, CreateOrUpdateExternalUser_Callbacks callbacks) {

        readUser(internalUserId, new ReadCallbacks() {
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


    // Внутренние методы
    private void readUser(String userId, ReadCallbacks callbacks) {

        usersRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                User user = dataSnapshot.getValue(User.class);

                if (null != user) {
                    storeCurrentUser(user);
                    callbacks.onUserReadSuccess(user);
                    return;
                }

                callbacks.onUserReadFail("User from dataSnapshot is NULL");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                String errorMsg = databaseError.getMessage();
                callbacks.onUserReadFail(errorMsg);
                Log.e(TAG, errorMsg);
                databaseError.toException().printStackTrace();
            }

        });
    }

    private void readAdminsList(ReadAdminsListCallbacks callbacks) {

        adminsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                HashMap<String,Boolean> list = new HashMap<>();
                for (DataSnapshot snapshotItem : dataSnapshot.getChildren())
                    list.put(snapshotItem.getKey(), true);

                storeAdminsList(list);

                callbacks.onReadAdminsListSuccess();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                String errorMsg = databaseError.getMessage();
                callbacks.onReadAdminsListFail(errorMsg);
                Log.e(TAG, errorMsg);
                databaseError.toException().printStackTrace();
            }
        });
    }

    private void checkExistance(Query query, final CheckExistanceCallbacks callbacks) {

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                callbacks.onCheckComplete();

                if (0L == dataSnapshot.getChildrenCount()) callbacks.onNotExists();
                else callbacks.onExists();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callbacks.onCheckComplete();
                callbacks.onCheckFail(databaseError.getMessage());
                databaseError.toException().printStackTrace();
            }
        });
    }

    private interface ChangeCardCommentsSubscriptionCallbacks {
        void onChangeSuccess();
        void onChangeFail(String errorMsg);
    }

    private void changeCardCommentsSubscription(String cardId, String userId, boolean enableSubscription,
                                                ChangeCardCommentsSubscriptionCallbacks callbacks) {

        String path = userId+"/unsubscribedCards/"+cardId;
        DatabaseReference reference = usersRef.child(path);

        Boolean value = (enableSubscription) ? null : true;

        reference.setValue(value)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        callbacks.onChangeSuccess();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        callbacks.onChangeFail(e.getMessage());
                        e.printStackTrace();
                    }
                });
    }
}
