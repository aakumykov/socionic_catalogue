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
import java.util.List;

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

    private String dropboxAccessToken;
    private DropboxBackuper dropboxBackuper;

    private List<String> backupSuccessList = new ArrayList<>();
    private List<String> backupErrorsList = new ArrayList<>();

    private CollectionPool collectionPool = new CollectionPool();

    /*private static class BackupInfo {

    }*/


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.backup_activity);
        ButterKnife.bind(this);

        collectionPool.push(new CollectionPair("admins", User.class));
        collectionPool.push(new CollectionPair("user", User.class));
        collectionPool.push(new CollectionPair("cards", Card.class));
        collectionPool.push(new CollectionPair("tags", Tag.class));
        collectionPool.push(new CollectionPair("comments", Comment.class));

        dropboxAccessToken = getResources().getString(R.string.DROPBOX_ACCESS_TOKEN);
        dropboxBackuper = new DropboxBackuper(dropboxAccessToken);
    }

    @Override
    public void onUserLogin() {

    }
    @Override
    public void onUserLogout() {

    }


    // Нажатия
    @BindView(R.id.startButton) Button startButton;
    @OnClick(R.id.startButton)
    void onStartButonClicked() {
        startBackup();
    }

    @OnClick(R.id.dropboxTestButton)
    void onDropboxTestButtonClicked() {

        showToast("Не реализовано");

        /*final String dirName = "qwerty";

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                DbxRequestConfig dbxRequestConfig = new DbxRequestConfig("dropbox/java-tutorial", "en_US");
                DbxClientV2 client = new DbxClientV2(dbxRequestConfig, dropboxAccessToken);

                *//*try {
                    Metadata metadata = client.files().getMetadata("/" + dirName);
                    Log.d(TAG, "File '"+dirName+"' exitst");
                }
                catch (DbxException e) {

                    Log.d(TAG, "File '"+dirName+"' does not exitst");
                    processException(TAG, e);
                }
                catch (Exception e) {
                    processException(TAG, e);
                }*//*

                *//*try {
                    CreateFolderResult createFolderResult = client.files()
                            .createFolderV2("/" + dirName, true);
                    FolderMetadata folderMetadata = createFolderResult.getMetadata();
                    String createdFolderName = folderMetadata.getName();
                    throw new Exception("Создан каталог '"+createdFolderName+"'");
                }
                catch (Exception e) {
                    MyUtils.processException(TAG, e);
                }*//*

                *//*String stringData = "Строка текста";
                String fileName = "файл";
                String fileExtension = "txt";

                try {
                    String firstHash = MyUtils.md5sum(stringData) + "";
                    String remoteFileName = "/" + dirName + "/" + fileName + "." + fileExtension;
                    byte[] textBytes = stringData.getBytes();

                    try (InputStream byteArrayInputStream = new ByteArrayInputStream(textBytes)) {
                        // Отправка на сервер
                        FileMetadata uploadMetadata = client.files()
                                .uploadBuilder(remoteFileName)
                                .withAutorename(true)
                                .uploadAndFinish(byteArrayInputStream);

                        String uploadedFileName = uploadMetadata.getName();
                        Log.d(TAG, "uploadedFileName: "+uploadedFileName);

                        hideProgressBar();
                    }
                    catch (Exception e) {
                        MyUtils.processException(TAG, e);
                    }
                }
                catch (Exception e) {
                    MyUtils.processException(TAG, e);
                }*//*
            }
        };

        new Thread(runnable).start();*/
    }


    // Внутренние методы
    private void startBackup() {
        String dirName = "qwerty";
        String initialDirName = MyUtils.quoteString(this, dirName);

        String msg = "Создаётся каталог "+initialDirName;
        Log.d(TAG, msg);

        dropboxBackuper.createDir(dirName, true, new DropboxBackuper.iCreateDirCallbacks() {
            @Override
            public void onCreateDirSuccess(String createdDirName) {
                String successMsg = "Создан каталог " + MyUtils.quoteString(BackupActivity.this, createdDirName);
                backupSuccessList.add(successMsg);
                Log.d(TAG, successMsg);

                performCollectionsBackup(createdDirName);
            }

            @Override
            public void onCreateDirFail(String errorMsg) {
                backupErrorsList.add(errorMsg);

                String msg = "Ошибка создания кталога " + initialDirName;
                Log.e(TAG, msg);
            }
        });
    }

    private void performCollectionsBackup(String targetDirName) {

        CollectionPair collectionPair = collectionPool.pop();

        if (null != collectionPair)
        {
            String collectionName = collectionPair.name;
            Class itemClass = collectionPair.itemClass;

            String msg = "Загрузка коллекции "+collectionPair.getName();
            Log.d(TAG, msg);

            loadCollection(collectionName, itemClass, new iLoadCollectionCallbacks() {
                @Override
                public void onLoadCollectionSuccess(List<Object> itemsList, List<String> errorsList) {
                    String jsonData = listOfObjects2JSON(itemsList);

                    String msg = "Сохранение коллекции "+collectionName;
                    Log.d(TAG, msg);

                    dropboxBackuper.backupString(
                            targetDirName,
                            collectionName,
                            "json",
                            jsonData,
                            true,
                            new DropboxBackuper.iBackupStringCallbacks() {
                                @Override
                                public void onBackupSuccess(DropboxBackuper.BackupItemInfo backupItemInfo) {
                                    backupSuccessList.add("Коллекция "+collectionName+" обработана");
                                    String msg = "Коллекция "+collectionName+" сохранена";
                                    Log.d(TAG, msg);

                                    performCollectionsBackup(targetDirName);
                                }

                                @Override
                                public void onBackupFail(String errorMsg) {
                                    String msg = "Ошибка обработки "+collectionName+": "+errorMsg;
                                    backupErrorsList.add(msg);
                                    Log.e(TAG, msg);

                                    performCollectionsBackup(targetDirName);
                                }
                            }
                    );
                }

                @Override
                public void onLoadCollectionError(String errorMsg) {
                    String msg = "Ошибка получения коллекции "+collectionName;
                    Log.e(TAG, msg);

                    performCollectionsBackup(targetDirName);
                }
            });
        }
        else {
            Log.d(TAG, "Все коллекции обработаны");
        }
    }


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

        return "[\n" + TextUtils.join(",\n", jsonList) + "\n]";
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


    // Внутренние классы
    private static class CollectionPair {
        private String name;
        private Class itemClass;

        public CollectionPair(String name, Class itemClass) {
            this.name = name;
            this.itemClass = itemClass;
        }

        public String getName() {
            return name;
        }

        public Class getItemClass() {
            return itemClass;
        }
    }
    private static class CollectionPool {
        private List<CollectionPair> list = new ArrayList<>();

        public CollectionPair pop() {
            int index = list.size()-1;
            if (index >= 0) {
                CollectionPair collectionPair = list.get(index);
                list.remove(index);
                return collectionPair;
            }
            else {
                return null;
            }
        }

        public void push(CollectionPair collectionPair) {
            list.add(collectionPair);
        }
    }
}
