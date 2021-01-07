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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.singletons.AuthSingleton;
import ru.aakumykov.me.sociocat.singletons.CardsSingleton;
import ru.aakumykov.me.sociocat.singletons.TagsSingleton;
import ru.aakumykov.me.sociocat.singletons.UsersSingleton;
import ru.aakumykov.me.sociocat.singletons.iTagsSingleton;
import ru.aakumykov.me.sociocat.singletons.iUsersSingleton;
import ru.aakumykov.me.sociocat.z_base_view.BaseView;

public class RulesTestActivity extends BaseView {

    private static final String TAG = RulesTestActivity.class.getSimpleName();
    private AuthSingleton mAuthSingleton = AuthSingleton.getInstance();
    private UsersSingleton mUsersSingleton = UsersSingleton.getInstance();
    private CardsSingleton mCardsSingleton = CardsSingleton.getInstance();
    private TagsSingleton mTagsSingleton = TagsSingleton.getInstance();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rules_test_activity);
        ButterKnife.bind(this);
    }

    @Override public void onUserLogin() { }
    @Override public void onUserLogout() { }

    @OnClick(R.id.updateNonexistentUserButton)
    void onUpdateNonexistentUserButtonClicked() {
        threadPoolTest();
    }

    private void updateNonexistentUser() {
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

    private void threadPoolTest() {

        List<String> checksList = new ArrayList<>();
        checksList.add("userId");
        checksList.add("tag1");
        checksList.add("БЛ");

        ExecutorService executorService = Executors.newFixedThreadPool(2);

        executorService.submit(new Runnable() {
            @Override
            public void run() {
                mUsersSingleton.checkUserExists("qwerty", new iUsersSingleton.iCheckExistanceCallbacks() {
                    @Override
                    public void onCheckComplete() {

                    }

                    @Override
                    public void onExists() {

                    }

                    @Override
                    public void onNotExists() {

                    }

                    @Override
                    public void onCheckFail(String errorMsg) {

                    }
                });
            }
        });

        executorService.submit(new Runnable() {
            @Override
            public void run() {
                mTagsSingleton.checkTagExists("БЛ", new iTagsSingleton.ExistanceCallbacks() {
                    @Override
                    public void onTagExists(@NonNull String tagName) {
                        checksList.remove(tagName);
                    }

                    @Override
                    public void onTagNotExists(@Nullable String tagName) {

                    }

                    @Override
                    public void onTagExistsCheckFailed(@NonNull String errorMsg) {

                    }
                });
            }
        });

        executorService.submit(new Runnable() {
            @Override
            public void run() {
                mTagsSingleton.checkTagExists("tag1", new iTagsSingleton.ExistanceCallbacks() {
                    @Override
                    public void onTagExists(@NonNull String tagName) {
                        checksList.remove(tagName);
                    }

                    @Override
                    public void onTagNotExists(@Nullable String tagName) {

                    }

                    @Override
                    public void onTagExistsCheckFailed(@NonNull String errorMsg) {

                    }
                });
            }
        });


        executorService.shutdown();
        Log.d(TAG, "Работа выполнена: "+checksList);
    }

}
