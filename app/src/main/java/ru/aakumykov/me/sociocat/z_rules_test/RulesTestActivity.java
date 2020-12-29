package ru.aakumykov.me.sociocat.z_rules_test;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.singletons.AuthSingleton;
import ru.aakumykov.me.sociocat.singletons.CardsSingleton;
import ru.aakumykov.me.sociocat.singletons.UsersSingleton;
import ru.aakumykov.me.sociocat.z_base_view.BaseView;

public class RulesTestActivity extends BaseView {

    private static final String TAG = RulesTestActivity.class.getSimpleName();
    private AuthSingleton mAuthSingleton = AuthSingleton.getInstance();
    private UsersSingleton mUsersSingleton = UsersSingleton.getInstance();
    private CardsSingleton mCardsSingleton = CardsSingleton.getInstance();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rules_test_activity);
        ButterKnife.bind(this);
    }

    @Override public void onUserLogin() { }
    @Override public void onUserLogout() { }

    @OnClick(R.id.updateNonexistentUserButton)
    void updateNonexistentUser() {

        String badUserId = "qwerty";

        WriteBatch writeBatch = FirebaseFirestore.getInstance().batch();
        CollectionReference usersCollection = mUsersSingleton.getUsersCollection();
        DocumentReference badUserRef = usersCollection.document(badUserId);

        writeBatch.update(badUserRef, "cardsKeys", FieldValue.arrayRemove("123"));


        showProgressMessage("Выполняю");

        writeBatch.commit()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        showInfoMsg(R.string.yes);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showErrorMsg(R.string.error, e.getMessage());
                        Log.e(TAG, e.getMessage(), e);
                    }
                });
    }
}
