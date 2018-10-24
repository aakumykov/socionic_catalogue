package ru.aakumykov.me.mvp.services;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import ru.aakumykov.me.mvp.Constants;
import ru.aakumykov.me.mvp.interfaces.iUsers;
import ru.aakumykov.me.mvp.models.User;


public class Users implements iUsers {

    /* Одиночка */
    private static volatile Users ourInstance;
    public synchronized static Users getInstance() {
        synchronized (Users.class) {
            if (null == ourInstance) ourInstance = new Users();
            return ourInstance;
        }
    }
    private Users() {
    }
    /* Одиночка */

    private final static String TAG = "Users Singleton";
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference usersRef = firebaseDatabase.getReference().child(Constants.USERS_PATH);


    @Override
    public void listUsers(final ListCallbacks callbacks) {
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "dataSnapshot: "+dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callbacks.onListFail(databaseError.getMessage());
                databaseError.toException().printStackTrace();
            }
        });
    }

    @Override
    public void createUser(String name, String email) {

    }

    @Override
    public void getUser(String id) {

    }

    @Override
    public void saveUser(User user) {

    }

    @Override
    public void deleteUser(User user) {

    }
}
