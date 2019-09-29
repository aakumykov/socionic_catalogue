package ru.aakumykov.me.sociocat.backup_job;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;
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
import java.util.List;
import java.util.concurrent.TimeUnit;

import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.models.Card;
import ru.aakumykov.me.sociocat.models.Comment;
import ru.aakumykov.me.sociocat.models.Tag;
import ru.aakumykov.me.sociocat.models.User;
import ru.aakumykov.me.sociocat.utils.MyUtils;

public class BackupService extends Service {


    // ======== НАСТРОЙКА ОБЪЕКТОВ РЕЗЕРВНОГО КОПИРОВАНИЯ ========
    private CollectionPool collectionPool = new CollectionPool(
            new CollectionPair("admins", User.class),
            new CollectionPair("user", User.class),
            new CollectionPair("cards", Card.class),
            new CollectionPair("tags", Tag.class),
            new CollectionPair("comments", Comment.class)
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
    }



    // ======================== РЕЗЕРВНОЕ КОПИРОВАНИЕ ========================
    private DropboxBackuper dropboxBackuper;
    private List<String> backupSuccessList = new ArrayList<>();
    private List<String> backupErrorsList = new ArrayList<>();

    private void startBackup() {
        String dirName = "qwerty";
        String initialDirName = MyUtils.quoteString(this, dirName);
        Log.d(TAG, "startBackup(), initialDirName: "+initialDirName);

        displayProgressNotification();

        dropboxBackuper.createDir(dirName, true, new DropboxBackuper.iCreateDirCallbacks() {
            @Override
            public void onCreateDirSuccess(String createdDirName) {
                String successMsg = "Создан каталог " + MyUtils.quoteString(BackupService.this, createdDirName);
                backupSuccessList.add(successMsg);
                Log.d(TAG, successMsg);

                performCollectionsBackup(createdDirName);
            }

            @Override
            public void onCreateDirFail(String errorMsg) {
                backupErrorsList.add(errorMsg);
                finishWithError(errorMsg);
            }
        });
    }

    private void performCollectionsBackup(String targetDirName) {

        CollectionPair collectionPair = collectionPool.pop();

        if (null != collectionPair) {
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
            finishWithSuccess("Все коллекции обработаны");
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

    private interface iLoadCollectionCallbacks {
        void onLoadCollectionSuccess(List<Object> itemsList, List<String> errorsList);
        void onLoadCollectionError(String errorMsg);
    }



    // ======================== ШИРОКОВЕЩАТЕЛЬНЫЕ СООБЩЕНИЯ ========================
    public static class BackupServiceInfo implements Parcelable {

        private String status;

        // Конверт
        protected BackupServiceInfo(Parcel in) {

        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {

        }

        public static final Creator<BackupServiceInfo> CREATOR = new Creator<BackupServiceInfo>() {
            @Override
            public BackupServiceInfo createFromParcel(Parcel in) {
                return new BackupServiceInfo(in);
            }

            @Override
            public BackupServiceInfo[] newArray(int size) {
                return new BackupServiceInfo[size];
            }
        };
        @Override public int describeContents() {
            return 0;
        }
        // Конверт
    }
    public static class BackupProgressInfo implements Parcelable {

        private String name;
        private String backupStatus;
        private String backupResult;
        private String message;
        private int progress;
        private int progressMax;

        public BackupProgressInfo() {

        }

        public BackupProgressInfo(String name, String backupStatus) {
            this.name = name;
            this.backupStatus = backupStatus;
        }

        public String getName() {
            return name;
        }
        public String getBackupStatus() {
            return backupStatus;
        }
        public String getBackupResult() {
            return backupResult;
        }
        public String getMessage() {
            return this.message;
        }
        public int getProgress() {
            return progress;
        }
        public int getProgressMax() {
            return progressMax;
        }

        public BackupProgressInfo setName(String name) {
            this.name = name;
            return this;
        }
        public BackupProgressInfo setBackupStatus(String backupStatus) {
            this.backupStatus = backupStatus;
            return this;
        }
        public BackupProgressInfo setBackupResult(String backupResult) {
            this.backupResult = backupResult;
            return this;
        }
        public BackupProgressInfo setMessage(String message) {
            this.message = message;
            return this;
        }
        public BackupProgressInfo setProgress(int progress) {
            this.progress = progress;
            return this;
        }
        public BackupProgressInfo setProgressMax(int progressMax) {
            this.progressMax = progressMax;
            return this;
        }

        @NonNull
        @Override
        public String toString() {
            return "BackupProgressInfo {" +
                    "name: " + getName() +
                    ", backupStatus: " + getBackupStatus() +
                    ", backupResult: " + getBackupResult() +
                    ", progress: " + getProgress() +
                    ", progressMax: " + getProgressMax() +
                    "}";
        }

        // Конверт
        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(name);
            dest.writeString(backupStatus);
            dest.writeLong(progress);
            dest.writeInt(progressMax);
        }

        protected BackupProgressInfo(Parcel in) {
            name = in.readString();
            backupStatus = in.readString();
            progress = in.readInt();
            progressMax = in.readInt();
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<BackupProgressInfo> CREATOR = new Creator<BackupProgressInfo>() {
            @Override
            public BackupProgressInfo createFromParcel(Parcel in) {
                return new BackupProgressInfo(in);
            }

            @Override
            public BackupProgressInfo[] newArray(int size) {
                return new BackupProgressInfo[size];
            }
        };
        // Конверт
    }

    public static final String BROADCAST_BACKUP_SERVICE_STATUS = "ru.aakumykov.me.sociocat.BROADCAST_BACKUP_SERVICE_STATUS";
    public static final String BROADCAST_BACKUP_PROGRESS_STATUS = "ru.aakumykov.me.sociocat.BROADCAST_BACKUP_PROGRESS_STATUS";

    public static final String EXTRA_SERVICE_STATUS = "EXTRA_SERVICE_STATUS";
    public static final String EXTRA_BACKUP_PROGRESS = "EXTRA_BACKUP_PROGRESS";

    public final static String SERVICE_STATUS_START =   "SERVICE_STATUS_START";
    public final static String SERVICE_STATUS_RUNNING = "SERVICE_STATUS_RUNNING";
    public final static String SERVICE_STATUS_FINISH =  "SERVICE_STATUS_FINISH";

    public final static String BACKUP_RESULT_SUCCESS = "BACKUP_RESULT_SUCCESS";
    public final static String BACKUP_RESULT_ERROR =   "BACKUP_RESULT_ERROR";

    private void sendServiceStatusBroadcast(String serviceStatus) {
        Intent intent = new Intent(BROADCAST_BACKUP_SERVICE_STATUS);
        intent.putExtra(EXTRA_SERVICE_STATUS, serviceStatus);
        sendBroadcast(intent);
    }

    private void sendBackupProgressBroadcast(BackupProgressInfo backupProgressInfo) {
        Intent intent = new Intent(BROADCAST_BACKUP_PROGRESS_STATUS);
        intent.putExtra(EXTRA_BACKUP_PROGRESS, backupProgressInfo);
    }



    // ======================== УВЕДОМЛЕНИЯ ========================
    public static final String BACKUP_JOB_NOTIFICATION_CHANNEL = "BACKUP_JOB_NOTIFICATION_CHANNEL";
    public static final int PENDING_INTENT_ACTION_BACKUP_PROGRESS = 10;
    public static final int PENDING_INTENT_ACTION_BACKUP_RESULT = 20;
    public static final String INTENT_ACTION_BACKUP_PROGRESS = "INTENT_ACTION_BACKUP_PROGRESS";
    public static final String INTENT_ACTION_BACKUP_RESULT = "INTENT_ACTION_BACKUP_RESULT";
    public static final String INTENT_EXTRA_BACKUP_RESULT_TEXT = "INTENT_EXTRA_BACKUP_RESULT_TEXT";
    private int progressNotificationId = 10;
    private int resultNotificationId = 20;

    public static void createNotificationChannel(Context context) {
        MyUtils.createNotificationChannel(
                context,
                BACKUP_JOB_NOTIFICATION_CHANNEL,
                context.getResources().getString(R.string.BACKUP_JOB_SERVICE_channel_title),
                context.getResources().getString(R.string.BACKUP_JOB_SERVICE_channel_description),
                NotificationManagerCompat.IMPORTANCE_HIGH
        );
    }

    private void displayProgressNotification() {
        Log.d(TAG, "displayProgressNotification()");

        Intent intent = new Intent(this, BackupStatus_Activity.class);
        intent.setAction(INTENT_ACTION_BACKUP_PROGRESS);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                PENDING_INTENT_ACTION_BACKUP_PROGRESS,
                intent,
                PendingIntent.FLAG_CANCEL_CURRENT
        );

        String notificationTitle = getResources().getString(R.string.BACKUP_JOB_notification_title);
        String notificationDescription = getResources().getString(R.string.BACKUP_JOB_progress_notification_description);

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, BACKUP_JOB_NOTIFICATION_CHANNEL)
                        .setSmallIcon(R.drawable.ic_backup_job_colored)
                        .setContentTitle(notificationTitle)
                        .setContentText(notificationDescription)
//                        .setContentInfo("Content Info") // На новых версиях не отображается
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

    private void displayResultNotification(@Nullable String text) {
        Log.d(TAG, "displayResultNotification()");

//        BackupProgressInfo backupInfo = new BackupProgressInfo();
//                   backupInfo.setStatus(BackupStatus.BACKUP_SUCCESS);
//                   backupInfo.setName(name + ": " + resultNotificationId);

        Intent intent = new Intent(this, BackupStatus_Activity.class);
        intent.putExtra(INTENT_EXTRA_BACKUP_RESULT_TEXT, text);
//        intent.putExtra(INTENT_EXTRA_BACKUP_INFO, backupInfo);
//        intent.setAction(ACTION_BACKUP_PROGRESS);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                PENDING_INTENT_ACTION_BACKUP_RESULT,
                intent,
                PendingIntent.FLAG_CANCEL_CURRENT
        );

        String notificationTitle = getResources().getString(R.string.BACKUP_JOB_notification_title);
        String notificationDescription = getResources().getString(R.string.BACKUP_JOB_result_notification_description);

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, BACKUP_JOB_NOTIFICATION_CHANNEL)
                        .setSmallIcon(R.drawable.ic_backup_job_colored)
                        .setContentTitle(notificationTitle)
//                        .setContentText(notificationDescription)
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true);

        if (null != text)
            notificationBuilder.setContentText(text);

        Notification notification = notificationBuilder.build();

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(resultNotificationId++, notification);
    }



    // ======================== УПРАВЛЕНИЕ СЛУЖБОЙ ========================
    private final static String TAG = "BackupService";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate()");
        // ========  ========
        String dropboxAccessToken = getResources().getString(R.string.DROPBOX_ACCESS_TOKEN);
        dropboxBackuper = new DropboxBackuper(dropboxAccessToken);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.d(TAG, "onStartCommand()");

        sendServiceStatusBroadcast(SERVICE_STATUS_START);

        displayProgressNotification();

        new Thread(new Runnable() {
            @Override
            public void run() {
                int max = 5;

                for (int i=0; i<max; i++) {
                    try {
                        TimeUnit.SECONDS.sleep(1);
                    }
                    catch (InterruptedException e) {}

                    sendServiceStatusBroadcast(SERVICE_STATUS_RUNNING);

                    sendBackupProgressBroadcast(
                            new BackupProgressInfo()
                            .setMessage("Шаг "+i)
                            .setProgress(i)
                            .setProgressMax(max)
                    );
                }

                finishWithSuccess("Работа выполнена "+MyUtils.date2string());
            }
        }).start();

//        startBackup();

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
        stopSelf();
        removeProgressNotification();
        sendServiceStatusBroadcast(SERVICE_STATUS_FINISH);
    }

    private void finishWithSuccess(@Nullable String message) {
        Log.d(TAG, message);

        stopSelf();

        removeProgressNotification();

        displayResultNotification(message);

        sendServiceStatusBroadcast(SERVICE_STATUS_FINISH);
    }

}
