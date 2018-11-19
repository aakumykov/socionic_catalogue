package ru.aakumykov.me.mvp.services;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

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
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
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
    public void createUser(String uid, CreateCallbacks callbacks) {

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
        Log.d(TAG, "saveUser(), "+user);

        DatabaseReference userRef = usersRef.child(user.getKey());

        //TODO: транзакция
        //TODO: update вместо save
        userRef.setValue(user)
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
}
