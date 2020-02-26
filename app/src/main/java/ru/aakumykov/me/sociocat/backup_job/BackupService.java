package ru.aakumykov.me.sociocat.backup_job;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

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

import ru.aakumykov.me.sociocat.Config;
import ru.aakumykov.me.sociocat.Constants;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.models.Card;
import ru.aakumykov.me.sociocat.models.Comment;
import ru.aakumykov.me.sociocat.models.Tag;
import ru.aakumykov.me.sociocat.models.User;
import ru.aakumykov.me.sociocat.singletons.CardsSingleton;
import ru.aakumykov.me.sociocat.singletons.StorageSingleton;
import ru.aakumykov.me.sociocat.singletons.iCardsSingleton;
import ru.aakumykov.me.sociocat.singletons.iStorageSingleton;
import ru.aakumykov.me.sociocat.utils.DropboxBackuper;
import ru.aakumykov.me.sociocat.utils.MyUtils;

public class BackupService extends Service {


    // ======== НАСТРОЙКА ОБЪЕКТОВ РЕЗЕРВНОГО КОПИРОВАНИЯ ========
    private CollectionPool collectionPool = new CollectionPool(
            new CollectionPair(Constants.USERS_PATH, User.class),
            new CollectionPair(Constants.ADMINS_PATH, User.class),
            new CollectionPair(Constants.CARDS_PATH, Card.class),
            new CollectionPair(Constants.TAGS_PATH, Tag.class),
            new CollectionPair(Constants.COMMENTS_PATH, Comment.class)
    );

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

        public CollectionPool(CollectionPair... pairs) {
            for (CollectionPair pair : pairs)
                this.push(pair);
        }

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

        public int size() {
            return  list.size();
        }
    }

    private final static String TAG = "BackupService";
    private static boolean backupImpossible = true;
    private String targetDirName;
    private Map<String, String> previousCollection = new HashMap<>();
    private Map<String, String> images2backup = new HashMap<>();



    // ======================== УПРАВЛЕНИЕ СЛУЖБОЙ ========================
    @Override
    public void onCreate() {
        super.onCreate();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        final String KEY_NAME = "dropbox_access_token";

        String dropboxAccessToken = (sharedPreferences.contains(KEY_NAME)) ?
                sharedPreferences.getString(KEY_NAME, null) : null;

        if (null != dropboxAccessToken)
            dropboxAccessToken = dropboxAccessToken.trim();

        String dropboxAccessTokenStub = getString(R.string.PREFERENCE_dropbox_access_token_stub);

        // Если dropboxAccessToken содержит заглушку
        if (dropboxAccessTokenStub.equals(dropboxAccessToken)) {

            showCustomNotification(
                    R.string.BACKUP_SERVICE_backup_impossible,
                    R.string.BACKUP_SERVICE_dropbox_access_token_missing
            );

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("perform_database_backup", false);
            editor.commit();

            return;
        }

        backupImpossible = false;
        dropboxBackuper = new DropboxBackuper(dropboxAccessToken);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        //Log.d(TAG, "onStartCommand()");

        if (backupImpossible)
            return START_NOT_STICKY;

        sendServiceBroadcast(SERVICE_STATUS_START);

        startBackup();

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy()");
    }

    @Nullable @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void finishWithError(String errorMsg) {

        removeProgressNotification();
        displayResultNotification(errorMsg, BACKUP_STATUS_ERROR);

        sendBackupResultBroadcast(errorMsg, BACKUP_STATUS_ERROR);

        stopSelf();
        sendServiceBroadcast(SERVICE_STATUS_FINISH);
    }

    private void finishWithSuccess() {

        String message = MyUtils.getString(
                this,
                R.string.BACKUP_SERVICE_all_collections_are_processed,
                MyUtils.date2string()
        );

        removeProgressNotification();
        displayResultNotification(message, BACKUP_STATUS_SUCCESS);

        sendBackupResultBroadcast(message, BACKUP_STATUS_SUCCESS);

        stopSelf();
        sendServiceBroadcast(SERVICE_STATUS_FINISH);
    }



    // ======================== РЕЗЕРВНОЕ КОПИРОВАНИЕ ========================
    private DropboxBackuper dropboxBackuper;
    private List<String> backupSuccessList = new ArrayList<>();
    private List<String> backupErrorsList = new ArrayList<>();

    private void startBackup() {
        String dirName = MyUtils.date2string();

        notifyAboutBackupProgress(MyUtils.getString(this, R.string.BACKUP_SERVICE_backup_started));

        dropboxBackuper.createDir(dirName, true, new DropboxBackuper.iCreateDirCallbacks() {
            @Override
            public void onCreateDirSuccess(String dirName) {
                targetDirName = dirName;

                String successMsg = MyUtils.getString(BackupService.this, R.string.BACKUP_SERVICE_directory_created, dirName);
                backupSuccessList.add(successMsg);
                Log.d(TAG, successMsg);

                //performCollectionsBackup();
                produceBackup();
            }

            @Override
            public void onCreateDirFail(String errorMsg) {
                backupErrorsList.add(errorMsg);
                finishWithError(errorMsg);
            }
        });
    }

    private void performCollectionsBackup() {

        if (0 == collectionPool.size()) {
            finishWithError("Список резервного копирования пуст");
            return;
        }

        CollectionPair collectionPair = collectionPool.pop();

        if (null != collectionPair) {
            String collectionName = collectionPair.getName();
            Class itemClass = collectionPair.getItemClass();

            notifyAboutBackupProgress(MyUtils.getString(
                    this,
                    R.string.BACKUP_SERVICE_loading_collection,
                    collectionPair.getName())
            );

            loadCollection(collectionName, itemClass, new iLoadCollectionCallbacks() {
                @Override
                public void onLoadCollectionSuccess(List<Object> itemsList, List<String> errorsList) {
                    String jsonData = listOfObjects2JSON(itemsList);

                    if (errorsList.size() > 0) {
                        Log.e(TAG, "== КОЛЛЕКЦИЯ ЗАГРУЖЕНА С ОШИБКАМИ ==");
                        Log.e(TAG, "(успех: "+itemsList.size()+", провал: "+errorsList.size()+")");
                        for (String errorMsg : errorsList) {
                            Log.e(TAG, errorMsg);
                        }
                    }

                    notifyAboutBackupProgress(MyUtils.getString(
                            BackupService.this,
                            R.string.BACKUP_SERVICE_saving_collection,
                            collectionName
                    ));

                    dropboxBackuper.backupString(
                            targetDirName,
                            collectionName,
                            "json",
                            jsonData,
                            true,
                            new DropboxBackuper.iBackupStringCallbacks() {
                                @Override
                                public void onBackupStringSuccess(DropboxBackuper.BackupItemInfo backupItemInfo) {
                                    String msg = MyUtils.getString(
                                            BackupService.this,
                                            R.string.BACKUP_SERVICE_collection_is_saved,
                                            collectionName
                                    );
                                    backupSuccessList.add(msg);
                                    Log.d(TAG, msg);
                                    performCollectionsBackup();
                                }

                                @Override
                                public void onBackupStringFail(String errorMsg) {
                                    String msg = MyUtils.getString(
                                            BackupService.this,
                                            R.string.BACKUP_SERVICE_error_saving_collection,
                                            collectionName,
                                            errorMsg
                                    );
                                    backupErrorsList.add(msg);
                                    Log.e(TAG, msg);
                                    performCollectionsBackup();
                                }
                            }
                    );
                }

                @Override
                public void onLoadCollectionError(String errorMsg) {
                    String msg = MyUtils.getString(BackupService.this, R.string.BACKUP_SERVICE_error_loading_collection, collectionName);
                    Log.e(TAG, msg);
                    performCollectionsBackup();
                }
            });
        }
        else {
            finishWithSuccess();
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

                        // TODO: выводить предупреждение, если коллекция пуста

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
                Log.e(TAG, e.getMessage());
                e.printStackTrace();
                //MyUtils.printError(TAG, e);

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

    private <T> String listOfObjects2JSON_2(List<T> objectsList) {
        List<String> jsonList = new ArrayList<>();
        List<String> errorsList = new ArrayList<>();

        Gson gson = new Gson();

        for (Object item : objectsList) {
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

    private interface iLoadCollectionCallbacks {
        void onLoadCollectionSuccess(List<Object> itemsList, List<String> errorsList);
        void onLoadCollectionError(String errorMsg);
    }


    // Новый механизм
    private void produceBackup() {

        CollectionPair collectionPair = collectionPool.pop();

        if (null != collectionPair) {
            String collectionName = collectionPair.getName();

            switch (collectionName) {
                case Constants.CARDS_PATH:
                    backupCards();
                    return;
                case Constants.COMMENTS_PATH:
                    backupComments();
                    return;
                case Constants.TAGS_PATH:
                    backupTags();
                    return;
                case Constants.USERS_PATH:
                    backupUsers();
                    return;
                case Constants.ADMINS_PATH:
                    backupAdmins();
                    return;
                default:
                    String errorMsg = MyUtils.getString(this, R.string.BACKUP_SERVICE_error_unknown_collection_name, collectionName);
                    finishWithError(errorMsg);
            }
        }

        if (images2backup.size() > 0)
            backupImages();
        else
            finishWithSuccess();
    }

    private void backupCards() {
        String collectionName = "cards";

        notifyAboutBackupProgress(MyUtils.getString(
                this,
                R.string.BACKUP_SERVICE_loading_collection,
                collectionName)
        );

        CardsSingleton.getInstance().loadAllCards(new iCardsSingleton.ListCallbacks() {
            @Override
            public void onListLoadSuccess(List<Card> list) {

                for (Card card : list) {
                    if (card.isImageCard() && cardHasNewImage(card))
                        images2backup.put(card.getKey(), card.getFileName());
                }

                String jsonData = listOfObjects2JSON_2(list);

                notifyAboutBackupProgress(MyUtils.getString(
                        BackupService.this,
                        R.string.BACKUP_SERVICE_saving_collection,
                        collectionName
                ));

                dropboxBackuper.backupString(
                        targetDirName,
                        collectionName,
                        "json",
                        jsonData,
                        true,
                        new DropboxBackuper.iBackupStringCallbacks() {
                            @Override
                            public void onBackupStringSuccess(DropboxBackuper.BackupItemInfo backupItemInfo) {
                                String msg = MyUtils.getString(
                                        BackupService.this,
                                        R.string.BACKUP_SERVICE_collection_is_saved,
                                        collectionName
                                );
                                backupSuccessList.add(msg);
                                Log.d(TAG, msg);
                                produceBackup();
                            }

                            @Override
                            public void onBackupStringFail(String errorMsg) {
                                String msg = MyUtils.getString(
                                        BackupService.this,
                                        R.string.BACKUP_SERVICE_error_saving_collection,
                                        collectionName,
                                        errorMsg
                                );
                                backupErrorsList.add(msg);
                                Log.e(TAG, msg);
                                produceBackup();
                            }
                        }
                );
            }

            @Override
            public void onListLoadFail(String errorMessage) {
                // TODO: сообщать об этом явно
                Log.d(TAG, errorMessage);
                produceBackup();
            }
        });
    }

    private boolean cardHasNewImage(Card newCard) {
        // Нет сведений о предыдущем резервном копировании - считаем, что есть новая картинка.
        if (0 == previousCollection.size())
            return true;

        String newCardKey = newCard.getKey();

        // Карточка не найдена, значит, есть новая картинка.
        if (!previousCollection.containsKey(newCardKey)) {
            return true;
        }
        else {
            try {
                String cardJSON = previousCollection.get(newCardKey);
                Card oldCard = new Gson().fromJson(cardJSON, Card.class);
                if (null == oldCard)
                    return true;

                String oldImageURL = oldCard.getImageURL();

                // Если нет сведений о старом imageURL
                if (null == oldImageURL)
                    return true;

                String newImageURL = newCard.getImageURL();

                return ! oldImageURL.equals(newImageURL);
            }
            catch (Exception e) {
                // При ошибке восстановления из JSON выполняем рез. копирование
                MyUtils.printError(TAG, e);
                return true;
            }
        }
    }

    private void backupComments() {
        produceBackup();
    }

    private void backupTags() {
        produceBackup();
    }

    private void backupUsers() {
        produceBackup();
    }

    private void backupAdmins() {
        produceBackup();
    }

    // Метод должен запускаться ПОСЛЕ обработки карточек, т.к. получает информацию оттуда.
    private void backupImages() {
        String imagesDir = targetDirName + "/images";

        dropboxBackuper.createDir(imagesDir, true, new DropboxBackuper.iCreateDirCallbacks() {
            @Override
            public void onCreateDirSuccess(String createdDirName) {
                uploadImages2Server(createdDirName);
            }

            @Override
            public void onCreateDirFail(String errorMsg) {
                // TODO: уведомить об ошибке
                Log.e(TAG, errorMsg);
            }
        });
    }

    private void uploadImages2Server(String remoteTargetDir) {
        List<String> errorsList = new ArrayList<>();
        List<String> successList = new ArrayList<>();

        for (String cardKey : images2backup.keySet()) {
            String imageFileName = images2backup.get(cardKey);

            StorageSingleton.getInstance()
                    .getImage(imageFileName, new iStorageSingleton.GetFileCallbacks() {
                        @Override
                        public void onFileDownloadSuccess(byte[] imageBytes) {

                            dropboxBackuper.backupFile(remoteTargetDir, imageFileName, imageBytes, new DropboxBackuper.iBackupFileCallbacks() {
                                @Override
                                public void onBackupFileSuccess(String uploadedFileName) {
                                    // TODO: уведомить
                                    successList.add(imageFileName);
                                }

                                @Override
                                public void onBackupFileError(String errorMsg) {
                                    // TODO: уведомить
                                    Log.e(TAG, errorMsg);
                                    errorsList.add(errorMsg);
                                }
                            });
                        }

                        @Override
                        public void onFileDownloadError(String errorMsg) {
                            Log.e(TAG, errorMsg);
                            errorsList.add(errorMsg);
                        }
                    });
        }
    }


    // ======================== ШИРОКОВЕЩАТЕЛЬНЫЕ СООБЩЕНИЯ ========================
    public static final String BROADCAST_SERVICE_STATUS = "ru.aakumykov.me.sociocat.BROADCAST_SERVICE_STATUS";
    public static final String BROADCAST_BACKUP_PROGRESS = "ru.aakumykov.me.sociocat.BROADCAST_BACKUP_PROGRESS";
    public static final String BROADCAST_BACKUP_RESULT = "ru.aakumykov.me.sociocat.BROADCAST_BACKUP_RESULT";

    public final static String SERVICE_STATUS_START =   "SERVICE_STATUS_START";
    public final static String SERVICE_STATUS_RUNNING = "SERVICE_STATUS_RUNNING";
    public final static String SERVICE_STATUS_FINISH =  "SERVICE_STATUS_FINISH";

    public final static String BACKUP_STATUS_START = "BACKUP_STATUS_START";
    public final static String BACKUP_STATUS_RUNNING = "BACKUP_STATUS_START";
    public final static String BACKUP_STATUS_SUCCESS = "BACKUP_STATUS_SUCCESS";
    public final static String BACKUP_STATUS_ERROR = "BACKUP_STATUS_ERROR";

    public static final String EXTRA_SERVICE_STATUS = "EXTRA_SERVICE_STATUS";
    public static final String EXTRA_BACKUP_STATUS = "EXTRA_BACKUP_STATUS";
    public static final String EXTRA_MESSAGE = "EXTRA_MESSAGE";
    public static final String EXTRA_RESULT_NOTIFICATION_ID = "EXTRA_RESULT_NOTIFICATION_ID";

    private void sendServiceBroadcast(String serviceStatus) {
        Intent intent = new Intent(BROADCAST_SERVICE_STATUS);
        intent.putExtra(EXTRA_SERVICE_STATUS, serviceStatus);
        sendBroadcast(intent);
    }

    private void sendBackupProgressBroadcast(String message, String backupStatus) {
        Intent intent = new Intent(BROADCAST_BACKUP_PROGRESS);
        intent.putExtra(EXTRA_MESSAGE, message);
        intent.putExtra(EXTRA_BACKUP_STATUS, backupStatus);
        sendBroadcast(intent);
    }

    private void sendBackupResultBroadcast(String message, String backupStatus) {
        Intent intent = new Intent(BROADCAST_BACKUP_RESULT);
        intent.putExtra(EXTRA_BACKUP_STATUS, backupStatus);
        intent.putExtra(EXTRA_MESSAGE, message);
        intent.putExtra(EXTRA_RESULT_NOTIFICATION_ID, resultNotificationId);
        sendBroadcast(intent);
    }



    // ======================== УВЕДОМЛЕНИЯ ========================
    public static final String BACKUP_JOB_NOTIFICATION_CHANNEL = "BACKUP_JOB_NOTIFICATION_CHANNEL";

    public static final int PENDING_INTENT_ACTION_BACKUP_PROGRESS = 10;
    public static final int PENDING_INTENT_ACTION_BACKUP_RESULT = 20;

    public static final String ACTION_BACKUP_PROGRESS = "ACTION_BACKUP_PROGRESS";
    public static final String ACTION_BACKUP_RESULT = "ACTION_BACKUP_RESULT";

    private int progressNotificationId = 10;
    private static int resultNotificationId = 20;

    public static void createNotificationChannel(Context context) {
        MyUtils.createNotificationChannel(
                context,
                BACKUP_JOB_NOTIFICATION_CHANNEL,
                context.getResources().getString(R.string.BACKUP_SERVICE_channel_title),
                context.getResources().getString(R.string.BACKUP_SERVICE_channel_description),
                NotificationManagerCompat.IMPORTANCE_LOW
        );
    }

    public static void removeResultNotification(Context context, int resultNotificationId) {
        NotificationManagerCompat.from(context).cancel(resultNotificationId);
    }

    private void displayProgressNotification(String message, String status) {

        // Сведения для страницы сведений о РК
        Intent intent = new Intent(this, BackupStatus_Activity.class);
        intent.setAction(ACTION_BACKUP_PROGRESS);
        intent.putExtra(EXTRA_BACKUP_STATUS, status);
        intent.putExtra(EXTRA_MESSAGE, message);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                PENDING_INTENT_ACTION_BACKUP_PROGRESS,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        // Для уведомления
        String notificationTitle = getResources().getString(R.string.BACKUP_SERVICE_backup);

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, BACKUP_JOB_NOTIFICATION_CHANNEL)
                        .setSmallIcon(R.drawable.ic_backup_job_colored)
                        .setContentTitle(notificationTitle)
                        .setContentText(message)
                        .setUsesChronometer(true)
                        .setOngoing(true)
                        .setProgress(0,0,true)
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true);

        Notification notification = notificationBuilder.build();

        startForeground(progressNotificationId, notification);
    }

    private void removeProgressNotification() {
        stopForeground(true);
    }

    private void displayResultNotification(String message, String status) {

        // Сведения для страницы сведений о РК
        Intent intent = new Intent(this, BackupStatus_Activity.class);
        intent.setAction(ACTION_BACKUP_RESULT);
        intent.putExtra(EXTRA_BACKUP_STATUS, status);
        intent.putExtra(EXTRA_MESSAGE, message);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                PENDING_INTENT_ACTION_BACKUP_RESULT,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        // Для уведомления
        String notificationTitle = getResources().getString(R.string.BACKUP_SERVICE_backup);

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, BACKUP_JOB_NOTIFICATION_CHANNEL)
                        .setSmallIcon(R.drawable.ic_backup_job_colored)
                        .setContentTitle(notificationTitle)
                        .setContentText(message)
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true);

        Notification notification = notificationBuilder.build();

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(resultNotificationId, notification);
    }

    private void showCustomNotification(int titleID, int messageId) {

        String title = getResources().getString(titleID);
        String message = getResources().getString(messageId);

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, BACKUP_JOB_NOTIFICATION_CHANNEL)
                        .setSmallIcon(R.drawable.ic_backup_job_colored)
                        .setContentTitle(title)
                        .setContentText(message)
                        .setAutoCancel(true);

        Notification notification = notificationBuilder.build();

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(resultNotificationId, notification);
    }


    // ======================== СЛУЖЕБНЫЕ МЕТОДЫ ========================
    public static boolean isTimeToDoBackup(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.SHARED_PREFERENCES_ADMIN, MODE_PRIVATE);

        long lastBackupTime = sharedPreferences.getLong(Constants.PREFERENCE_KEY_LAST_BACKUP_TIME, -1);
        long currentTimeInSeconds = MyUtils.getCurrentTimeInSeconds();
        long elapsedTime = currentTimeInSeconds - lastBackupTime;

        return elapsedTime > Config.BACKUP_INTERVAL_SECONDS;
    }

    public static void saveLastBackupTime(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.SHARED_PREFERENCES_ADMIN, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        long currentTimeInSeconds = MyUtils.getCurrentTimeInSeconds();
        editor.putLong(Constants.PREFERENCE_KEY_LAST_BACKUP_TIME, currentTimeInSeconds);
        editor.apply();
    }

    private void notifyAboutBackupProgress(String message) {
        displayProgressNotification(message, BACKUP_STATUS_RUNNING);
        sendBackupProgressBroadcast(message, BACKUP_STATUS_RUNNING);
    }
}
