package ru.aakumykov.me.sociocat.backup_job;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
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
import java.util.Set;
import java.util.concurrent.TimeUnit;

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

    /*private static class BackupInfo {

    }*/


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.backup_activity);
        ButterKnife.bind(this);

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

        final String dirName = "qwerty";

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                DbxRequestConfig dbxRequestConfig = new DbxRequestConfig("dropbox/java-tutorial", "en_US");
                DbxClientV2 client = new DbxClientV2(dbxRequestConfig, dropboxAccessToken);

                /*try {
                    Metadata metadata = client.files().getMetadata("/" + dirName);
                    Log.d(TAG, "File '"+dirName+"' exitst");
                }
                catch (DbxException e) {

                    Log.d(TAG, "File '"+dirName+"' does not exitst");
                    processException(TAG, e);
                }
                catch (Exception e) {
                    processException(TAG, e);
                }*/

                /*try {
                    CreateFolderResult createFolderResult = client.files()
                            .createFolderV2("/" + dirName, true);
                    FolderMetadata folderMetadata = createFolderResult.getMetadata();
                    String createdFolderName = folderMetadata.getName();
                    throw new Exception("Создан каталог '"+createdFolderName+"'");
                }
                catch (Exception e) {
                    MyUtils.processException(TAG, e);
                }*/

                /*String stringData = "Строка текста";
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
                }*/
            }
        };

        new Thread(runnable).start();
    }


    // Внутренние методы
    private void startBackup() {
        String dirName = "qwerty";
        String initialDirName = MyUtils.quoteString(this, dirName);

        showProgressBar();
        showInfoMsg("Создаётся каталог "+initialDirName);

        dropboxBackuper.createDir(dirName, true, new DropboxBackuper.iCreateDirCallbacks() {
            @Override
            public void onCreateDirSuccess(String createdDirName) {
                String successMsg = "Создан каталог "+MyUtils.quoteString(BackupActivity.this, createdDirName);
                backupSuccessList.add(successMsg);

                hideProgressBar();
                showInfoMsg(successMsg);

                performCollectionsBackup(createdDirName);
            }

            @Override
            public void onCreateDirFail(String errorMsg) {
                backupErrorsList.add(errorMsg);

                hideProgressBar();
                showErrorMsg("Ошибка создания кталога "+initialDirName, errorMsg);
            }
        });
    }

    private void performCollectionsBackup(String targetDirName) {

        HashMap<String,Class> collectionsMap = new HashMap<>();
        collectionsMap.put("admins", User.class);
        collectionsMap.put("users", User.class);
        collectionsMap.put("cards", Card.class);
        collectionsMap.put("comments", Comment.class);
        collectionsMap.put("tags", Tag.class);

        for (String collectionName : collectionsMap.keySet())
        {
            Class itemClass = collectionsMap.get(collectionName);

            showProgressBar();
            showInfoMsg("Загрузка коллекции "+collectionName);

            loadCollection(collectionName, itemClass, new iLoadCollectionCallbacks() {
                @Override
                public void onLoadCollectionSuccess(List<Object> itemsList, List<String> errorsList) {
                    String jsonData = listOfObjects2JSON(itemsList);

                    showInfoMsg("Сохранение коллекции "+collectionName);

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

                                    hideProgressBar();
                                    showInfoMsg("Коллекция "+collectionName+" сохранена");
                                }

                                @Override
                                public void onBackupFail(String errorMsg) {
                                    backupErrorsList.add("Ошибка обработки "+collectionName+": "+errorMsg);

                                    hideProgressBar();
                                    showErrorMsg(errorMsg, errorMsg);
                                }
                            }
                    );
                }

                @Override
                public void onLoadCollectionError(String errorMsg) {
                    hideMsg();
                    hideProgressBar();
                    showErrorMsg("Ошибка получения коллекции "+collectionName, errorMsg);
                }
            });

            try {
                TimeUnit.SECONDS.sleep(5); }
            catch (InterruptedException e) {
                Log.e(TAG, e.getMessage());
                MyUtils.processException(TAG, e);
            }
        }

        showInfoMsg("Все коллекции обработаны");
        Log.d(TAG, "Все коллекции обработаны");
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
}
