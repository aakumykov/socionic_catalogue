package ru.aakumykov.me.mvp.services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import ru.aakumykov.me.mvp.Constants;
import ru.aakumykov.me.mvp.MyUtils;
import ru.aakumykov.me.mvp.interfaces.MyInterfaces;
import ru.aakumykov.me.mvp.models.Card;

public class CardsService extends Service implements MyInterfaces.CardsService
{
    // Внутренний класс
    class LocalBinder extends Binder {
        CardsService getService() {
            return CardsService.this;
        }
    }

    // Свойства
    private final static String TAG = "CardsService";
    private final IBinder binder;
    private FirebaseDatabase firebaseDatabase;

    // Слежебные методы
    public CardsService() {
        Log.d(TAG, "new CardsService()");
        binder = new LocalBinder();
        firebaseDatabase = FirebaseDatabase.getInstance();
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind()");
        return binder;
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate()");
        super.onCreate();
        firebaseDatabase.goOnline(); // нужно ли?
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy()");
        super.onDestroy();
        firebaseDatabase.goOffline();
    }

    // Пользовательские методы
    @Override
    public void loadCard(String key, final CardCallbacks callbacks) {
        Log.d(TAG, "loadCard("+key+")");

        DatabaseReference cardRef = firebaseDatabase.getReference()
                .child(Constants.CARDS_PATH).child(key);

        cardRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Card card = dataSnapshot.getValue(Card.class);
                callbacks.onLoadSuccess(card);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callbacks.onLoadFailed(databaseError.getMessage());
                databaseError.toException().printStackTrace();
            }
        });
    }

    @Override
    public void loadList(final ListCallbacks callbacks) {
        Log.d(TAG, "loadList()");

        DatabaseReference listRef = firebaseDatabase.getReference()
                .child(Constants.CARDS_PATH);

        listRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                // TODO: сложно это всё. Проверить. Где обрабатывать ошибку? Где выделять Card?
//                try {
//                    Card card = MyUtils.snapshot2card(dataSnapshot);
//                } catch (IllegalArgumentException e) {
//                    callbacks.onBadData(e.getMessage());
//                    e.printStackTrace();
//                }
                Card card = MyUtils.snapshot2card(dataSnapshot);
                callbacks.onChildAdded(card);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Card card = MyUtils.snapshot2card(dataSnapshot);
                callbacks.onChildChanged(card, s);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                Card card = MyUtils.snapshot2card(dataSnapshot);
                callbacks.onChildRemoved(card);
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Card card = MyUtils.snapshot2card(dataSnapshot);
                callbacks.onChildMoved(card, s);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callbacks.onCancelled(databaseError.getMessage());
                databaseError.toException().printStackTrace();
            }
        });
    }
}
