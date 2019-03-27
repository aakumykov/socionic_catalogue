package ru.aakumykov.me.sociocat.services;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ru.aakumykov.me.sociocat.Constants;
import ru.aakumykov.me.sociocat.interfaces.iUsersSingleton;
import ru.aakumykov.me.sociocat.models.User;

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
    /* Одиночка */

    private final static String TAG = "UsersSingleton";
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference rootRef = firebaseDatabase.getReference().child("/");
    private DatabaseReference usersRef = firebaseDatabase.getReference().child(Constants.USERS_PATH);


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
        Map<String,Boolean> userCards = user.getCardsKeys();
        if (null != userCards) {
            for (Map.Entry entry : userCards.entrySet()) {
                updatePool.put(Constants.CARDS_PATH + "/" + entry.getKey() + "/userName", user.getName());
            }
        }

        // Обновляю имя и аватар в его комментариях
        Map<String,Boolean> userComments = user.getCommentsKeys();
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

    @Override public void updatePushToken(String token, @Nullable iUsersSingleton.PushTokenCallbacks callbacks) {

        usersRef.child(PUSH_TOKEN_NAME).setValue(token)
                .addOnCompleteListener(new OnCompleteListener<Void>() {

                    @Override public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {
                            if (null != callbacks)
                                callbacks.onPushTokenUpdateSuccess(token);
                        } else {
                            Exception exception = task.getException();
                            if (null != exception) {
                                if (null != callbacks)
                                    callbacks.onPushTokenUpdateError(exception.getMessage());
                                exception.printStackTrace();
                            }
                        }
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
}
