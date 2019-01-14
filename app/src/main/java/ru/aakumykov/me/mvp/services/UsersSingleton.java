package ru.aakumykov.me.mvp.services;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ru.aakumykov.me.mvp.Constants;
import ru.aakumykov.me.mvp.interfaces.iUsersSingleton;
import ru.aakumykov.me.mvp.models.User;

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
    public void createUser(final String userId, String email, final CreateCallbacks callbacks) {

        final User user = new User(userId);
         user.setEmail(email);
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
    public void getUser(final String userId, final ReadCallbacks callbacks) {
        Log.d(TAG, "getUser("+userId+", callbacks)");

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
    public void checkNameExists(String name, CheckExistanceCallbacks callbacks) {
        Query query =rootRef.child(Constants.USERS_PATH).orderByChild("name").equalTo(name);
        checkExistance(query, callbacks);
    }

    @Override
    public void checkEmailExists(String email, CheckExistanceCallbacks callbacks) {
        Query query =usersRef.orderByChild("email").equalTo(email);
        checkExistance(query, callbacks);
    }

    private void checkExistance(Query query, final CheckExistanceCallbacks callbacks) {

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                callbacks.onCheckComplete();
                if (0 == dataSnapshot.getChildrenCount()) callbacks.onExists();
                else callbacks.onNotExists();
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
