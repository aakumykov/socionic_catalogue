package ru.aakumykov.me.mvp.services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AdditionalUserInfo;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseUserMetadata;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import ru.aakumykov.me.mvp.Constants;
import ru.aakumykov.me.mvp.R;
import ru.aakumykov.me.mvp.interfaces.iAuthService;
import ru.aakumykov.me.mvp.models.User;

public class AuthService extends Service implements
        iAuthService
{
    // Внутренний класс
    public class LocalBinder extends Binder {
        public AuthService getService() {
            return AuthService.this;
        }
    }

    // Свойства
    private final static String TAG = "AuthService";
    private final IBinder binder;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private DatabaseReference usersRef = FirebaseDatabase.getInstance()
            .getReference().child(Constants.USERS_PATH);


    // Системные методы
    public AuthService() {
//        Log.d(TAG, "new AuthService()");
        binder = new LocalBinder();
    }

    @Override
    public IBinder onBind(Intent intent) {
//        Log.d(TAG, "onBind()");
        return binder;
    }


    // Интерфейсные методы
    // TODO: как быть с Гостем?
    @Override
    public boolean isUserLoggedIn() {
        return null != firebaseAuth.getCurrentUser();
    }

    @Override
    public void registerWithEmail(
            String email,
            String password,
            final iAuthService.RegisterCallbacks callbacks) throws Exception
    {
        Log.d(TAG, "registerWithEmail("+email+", ***)");

        firebaseAuth.createUserWithEmailAndPassword(email, password)
        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                callbacks.onRegSucsess(authResult.getUser().getUid());
            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                callbacks.onRegFail(e.getMessage());
                e.printStackTrace();
            }
        });
    }

    @Override
    public void createUser(final String uid, final User userDraft,
                           final CreateUserCallbacks callbacks)  throws Exception
    {
        Log.d(TAG, "createUser("+uid+"), "+userDraft);

        // Проверяю на существование
        usersRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (null == dataSnapshot.getValue()) {
                    Log.d(TAG, "Ключа "+uid+" в ветке /users нет.");

                    // Если данных нет, значит ключа такого нет
                    usersRef.child(uid).setValue(userDraft)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    User newUser = userDraft;
                                         newUser.setKey(uid);
                                    callbacks.onCreateSuccess(newUser);
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    callbacks.onCreateFail(e.getMessage());
                                    e.printStackTrace();
                                }
                            });

                } else {
                    Log.e(TAG, "Ключ "+uid+" в ветке /users УЖЕ ЕСТЬ.");
                    callbacks.onCreateFail("Key already exists");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callbacks.onCreateFail(databaseError.getMessage());
                databaseError.toException().printStackTrace();
            }
        });
    }

    @Override
    public void login(String email, String password, final LoginCallbacks callbacks) throws Exception {

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        callbacks.onLoginSuccess();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        callbacks.onLoginFail(e.getMessage());
                        e.printStackTrace();
                    }
                });
    }

    // TODO: Exception ?
    @Override
    public void logout(LogoutCallbacks callbacks) {
        FirebaseUser firebaseUser1 = firebaseAuth.getCurrentUser();

        if (null != firebaseUser1) {

            firebaseAuth.signOut();
            FirebaseUser firebaseUser2 = firebaseAuth.getCurrentUser();

            if (null == firebaseUser2) {
                getResources().getString(R.string.AUTH_error_logging_out);
                callbacks.onLogoutSuccess();
            }
            else {
                Log.e(TAG, "firebaseUser != null: " + firebaseUser2);
                callbacks.onLogoutFail(getResources().getString(R.string.AUTH_error_logging_out));
            }
        }
        else {
            // TODO: это временно
            callbacks.onLogoutFail("А вы ещё не заходили!");
        }
    }

    @Override
    public void cancelLogin() {
        firebaseAuth.signOut();
    }
}

