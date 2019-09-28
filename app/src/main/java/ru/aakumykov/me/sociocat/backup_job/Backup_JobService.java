package ru.aakumykov.me.sociocat.backup_job;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;

import androidx.annotation.NonNull;
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

public class Backup_JobService extends JobService {

    public final static String BACKUP_JOB_NOTIFICATION_CHANNEL = "BACKUP_JOB_NOTIFICATION_CHANNEL";
    public static final String INTENT_EXTRA_BACKUP_INFO = "EXTRA_KEY_BACKUP_INFO";

    public final static int ACTION_BACKUP_PROGRESS = 10;
    public final static int ACTION_BACKUP_RESULT = 20;

    private final static String TAG = "Backup_JobService";
    private final static int backupJobServiceId = R.id.backup_job_service_id;
    private int progressNotificationId = 10;
    private int resultNotificationId = 20;


    private List<String> backupSuccessList = new ArrayList<>();
    private List<String> backupErrorsList = new ArrayList<>();

    private String dropboxAccessToken;
    private DropboxBackuper dropboxBackuper;

    private CollectionPool collectionPool = new CollectionPool(
            new CollectionPair("admins", User.class),
            new CollectionPair("user", User.class),
            new CollectionPair("cards", Card.class),
            new CollectionPair("tags", Tag.class),
            new CollectionPair("comments", Comment.class)
    );


    public Backup_JobService() {
        super();
        dropboxAccessToken = getResources().getString(R.string.DROPBOX_ACCESS_TOKEN);
        dropboxBackuper = new DropboxBackuper(dropboxAccessToken);
    }

    // Внешние статические методы
    public static void createNotificationChannel(Context context)
    {
        MyUtils.createNotificationChannel(
                context,
                BACKUP_JOB_NOTIFICATION_CHANNEL,
                context.getResources().getString(R.string.BACKUP_JOB_SERVICE_channel_title),
                context.getResources().getString(R.string.BACKUP_JOB_SERVICE_channel_description),
                NotificationManagerCompat.IMPORTANCE_HIGH
        );
    }

    public static void scheduleJob(Context context)
    {
        Log.d(TAG, "scheduleJob()");

        ComponentName backupJobService = new ComponentName(context, Backup_JobService.class);

        JobInfo.Builder jobBuilder = new JobInfo.Builder(backupJobServiceId, backupJobService);
                        jobBuilder.setPeriodic(TimeUnit.MINUTES.toMillis(1));
                        jobBuilder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
                        jobBuilder.setRequiresDeviceIdle(false);
                        jobBuilder.setRequiresCharging(false);
                        jobBuilder.setPersisted(true);

        jobBuilder.setBackoffCriteria(
                TimeUnit.SECONDS.toMillis(10),
                JobInfo.BACKOFF_POLICY_LINEAR
        );

        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);

        int result = jobScheduler.schedule(jobBuilder.build());

        if (JobScheduler.RESULT_SUCCESS == result) {
            String msg = MyUtils.getString(context, R.string.BACKUP_JOB_SERVICE_job_is_scheduled);
            Log.d(TAG, msg);
        }
        else {
            String msg = MyUtils.getString(context, R.string.BACKUP_JOB_SERVICE_error_scheduling_job);
            Log.e(TAG, msg);
        }

    }

    public static void unscheduleJob(Context context)
    {
        Log.d(TAG, "scheduleJob()");

        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        jobScheduler.cancel(backupJobServiceId);
        Log.d(TAG, "Задача (должно быть) удалена из планировщика");
    }


    // Системные методы
    @Override
    public boolean onStartJob(JobParameters params) {
        Log.d(TAG, "onStartJob()");

        displayProgressNotification();

//        jobFinished(params, false);

        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.d(TAG, "onStopJob()");
        return false;
    }


    // Внутренние методы уведомлений
    private void displayProgressNotification() {
        Log.d(TAG, "displayProgressNotification()");

        Intent intent = new Intent(this, BackupStatus_Activity.class);
//        intent.setAction(ACTION_BACKUP_PROGRESS);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                ACTION_BACKUP_PROGRESS,
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
    private void displayResultNotification(String name) {
        Log.d(TAG, "displayResultNotification()");

        /*BackupInfo backupInfo = new BackupInfo();
                   backupInfo.setStatus(BackupStatus.BACKUP_SUCCESS);
                   backupInfo.setName(name + ": " + resultNotificationId);

        Intent intent = new Intent(this, BackupStatus_Activity.class);
        intent.putExtra(INTENT_EXTRA_BACKUP_INFO, backupInfo);
//        intent.setAction(ACTION_BACKUP_PROGRESS);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                ACTION_BACKUP_RESULT,
                intent,
                PendingIntent.FLAG_CANCEL_CURRENT
        );

        String notificationTitle = getResources().getString(R.string.BACKUP_JOB_notification_title);
        String notificationDescription = getResources().getString(R.string.BACKUP_JOB_result_notification_description);

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, BACKUP_JOB_NOTIFICATION_CHANNEL)
                        .setSmallIcon(R.drawable.ic_backup_job_colored)
                        .setContentTitle(notificationTitle)
                        .setContentText(notificationDescription)
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true);

        Notification notification = notificationBuilder.build();

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(resultNotificationId++, notification);*/
    }

    // Внутренние методы резервного копирования
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

    private void startBackup() {
        String dirName = "qwerty";
        String initialDirName = MyUtils.quoteString(this, dirName);

        String msg = "Создаётся каталог "+initialDirName;
        Log.d(TAG, msg);

        dropboxBackuper.createDir(dirName, true, new DropboxBackuper.iCreateDirCallbacks() {
            @Override
            public void onCreateDirSuccess(String createdDirName) {
                String successMsg = "Создан каталог " + MyUtils.quoteString(Backup_JobService.this, createdDirName);
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
    private interface iLoadCollectionCallbacks {
        void onLoadCollectionSuccess(List<Object> itemsList, List<String> errorsList);
        void onLoadCollectionError(String errorMsg);
    }

}
