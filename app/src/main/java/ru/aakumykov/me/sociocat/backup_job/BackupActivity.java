package ru.aakumykov.me.sociocat.backup_job;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.aakumykov.me.sociocat.BaseView;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.models.Card;
import ru.aakumykov.me.sociocat.models.Comment;
import ru.aakumykov.me.sociocat.models.Tag;
import ru.aakumykov.me.sociocat.models.User;
import ru.aakumykov.me.sociocat.utils.MyUtils;

public class BackupActivity extends BaseView {

    private final static String TAG = "BackupActivity";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.backup_activity);
        ButterKnife.bind(this);
    }

    @Override
    public void onUserLogin() {

    }

    @Override
    public void onUserLogout() {

    }


    // Нажатия
    @BindView(R.id.button2) Button button2;
    @OnClick(R.id.button2)
    void onButton2Click() {

        showProgressMessage(R.string.CARDS_GRID_load_old);

        MyUtils.disable(button2);

        button2.postDelayed(new Runnable() {
            @Override
            public void run() {
                hideProgressMessage();
                MyUtils.enable(button2);
            }
        }, 1000);
    }

    @BindView(R.id.startButton) Button startButton;
    @OnClick(R.id.startButton)
    void onStartButonClicked() {
        performCollectionsBackup();
    }


    // Внутренние методы
    private void performCollectionsBackup() {

        Map<String,Class> collectionsMap = new HashMap<>();
        collectionsMap.put("admins", User.class);
        collectionsMap.put("users", User.class);
        collectionsMap.put("cards", Card.class);
        collectionsMap.put("comments", Comment.class);
        collectionsMap.put("tags", Tag.class);

        for (String collectionName : collectionsMap.keySet())
        {
            Class itemClass = collectionsMap.get(collectionName);

            loadCollection(collectionName, itemClass, new iLoadCollectionCallbacks() {
                @Override
                public void onLoadCollectionSuccess(List<Object> itemsList, List<String> errorsList) {
                    String json = listOfObjects2JSON(itemsList);

                    upload2dropbox(json, new iUpload2DropboxCalbacks() {
                        @Override
                        public void onUpload2DropboxSuccess() {

                        }

                        @Override
                        public void onUpload2DropboxError(String errorMsg) {

                        }
                    });
                }

                @Override
                public void onLoadCollectionError(String errorMsg) {
                    Log.e(TAG, errorMsg);

                    hideMsg();
                    hideProgressBar();
                }
            });
        }
    }

    private void showConsoleError(String tag, Exception e) {
        Log.e(TAG, e.getMessage());
        Log.e(TAG, TextUtils.join("\n", e.getStackTrace()));
    }

/*
    private void backupFirestoreCollection2Dropbox(
            String dirName,
            String collectionName,
            Class itemClassDefinition,
            iFirestoreCollectionBackupCallbacks callbacks
    ) {
        callbacks.onCollectionBackupStart();

        loadCollection(collectionName, itemClassDefinition, new iLoadCollectionCallbacks() {
            @Override
            public void onLoadCollectionSuccess(List<Object> itemsList, List<String> errorsList) {
                if (0 != errorsList.size()) {
//                    showError("Ошибки при получении списка", null);
                    Log.e(TAG, errorsList.toString());
                }

                Pair<String, List<String>> pair = listOfObjects2JSON(itemsList);
                String collectionJSON = pair.first;
                if (0 != pair.second.size()) {
//                    showError("Ошибка преобразования объектов в JSON");
                    Log.e(TAG, "Errors converting objects list to JSON: "+pair.second.toString());
                }

                String accessToken = getResources().getString(R.string.DROPBOX_ACCESS_TOKEN);

                DropboxBackuper dropboxBackuper = new DropboxBackuper(accessToken);

                dropboxBackuper.backupString(
                        dirName,
                        collectionName,
                        "json",
                        collectionJSON,
                        new DropboxBackuper.iDropboxBackuperCallbacks() {
                            @Override
                            public void onBackupStart() {

                            }

                            @Override
                            public void onBackupFinish() {

                            }

                            @Override
                            public void onBackupSuccess(DropboxBackuper.BackupItemInfo backupItemInfo) {
//                                showInfo("Успех: "+ backupItemInfo.getFileName() + " в каталоге '" + backupItemInfo.getDirName() + "'");
                                callbacks.onCollectionBackupSuccess();
                            }

                            @Override
                            public void onBackupFail(String errorMsg) {
                                callbacks.onCollectionBackupError(errorMsg, null);
                            }
                        }
                );
            }

            @Override
            public void onLoadCollectionError(String errorMsg) {
                callbacks.onCollectionBackupError(errorMsg, null);
            }
        });
    }
*/


    private void loadCollection(String collectionName, Class itemClass, iLoadCollectionCallbacks callbacks) {
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        CollectionReference collectionReference = firebaseFirestore.collection(collectionName);

        collectionReference.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        Pair<List<Object>, List<String>> resultPair = extractCollectionObjects(queryDocumentSnapshots, itemClass);

                        List<Object> itemsList = resultPair.first;
                        List<String> errorsList = resultPair.second;

                        callbacks.onLoadCollectionSuccess(itemsList, errorsList);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        String errorMsg = e.getMessage();
                        Log.e(TAG, errorMsg);
                        e.printStackTrace();

                        callbacks.onLoadCollectionError(errorMsg);
                    }
                });
    }

    private void upload2dropbox(String json, iUpload2DropboxCalbacks calbacks) {

    }

    private Pair<List<Object>, List<String>> extractCollectionObjects(QuerySnapshot queryDocumentSnapshots, Class itemClass) {

        List<String> errorsList = new ArrayList<>();
        List<Object> itemsList = new ArrayList<>();

        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots)
        {
            try {
                Object item = documentSnapshot.toObject(itemClass);
                itemsList.add(item);
            }
            catch (Exception e) {
                errorsList.add(Arrays.toString(e.getStackTrace()));
            }
        }

        return new Pair<>(itemsList, errorsList);
    }

    private String listOfObjects2JSON(List<Object> itemsList) {

        List<String> jsonList = new ArrayList<>();
        List<String> errorsList = new ArrayList<>();

        Gson gson = new Gson();

        for (Object item : itemsList) {
            try {
                jsonList.add(gson.toJson(item));
            }
            catch (Exception e) {
                errorsList.add(Arrays.toString(e.getStackTrace()));
            }
        }

        if (errorsList.size() > 0) {
            Log.e(TAG, errorsList.toString());
        }

        return "[" + TextUtils.join(",", jsonList) + "]";
    }

    private String object2JSON(Object o) {
        return new Gson().toJson(o);
    }

    // Внутренние интерфейсы
    private interface iFirestoreCollectionBackupCallbacks {
        void onCollectionBackupStart();
        void onCollectionBackupFinish();
        void onCollectionBackupSuccess();
        void onCollectionBackupError(String errorMsg, List<String> errorsList);
    }

    private interface iLoadCollectionCallbacks {
        void onLoadCollectionSuccess(List<Object> itemsList, List<String> errorsList);
        void onLoadCollectionError(String errorMsg);
    }

    private interface iUpload2DropboxCalbacks {
        void onUpload2DropboxSuccess();
        void onUpload2DropboxError(String errorMsg);
    }
}
