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
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.concurrent.TimeUnit;

import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.utils.MyUtils;

public class Backup_JobService extends JobService {

    private final static String TAG = "Backup_JobService";
    private final static int backupJobServiceId = R.id.backup_job_service_id;

    public final static String BACKUP_JOB_NOTIFICATION_CHANNEL = "BACKUP_JOB_NOTIFICATION_CHANNEL";
    private int notificationIdProgress = R.id.backup_job_notification_id_progress;
    private int notificationIdResult = R.id.backup_job_notification_id_result;

    public final static int ACTION_BACKUP_PROGRESS = 10;
    public final static int ACTION_BACKUP_RESULT = 20;

    public static final String INTENT_EXTRA_BACKUP_INFO = "INTENT_EXTRA_BACKUP_INFO";


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

        displayResultNotification("Пробное резервное копирование");

        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.d(TAG, "onStopJob()");
        return false;
    }


    // Внутренние методы
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

        startForeground(notificationIdProgress, notification);
    }

    private void displayResultNotification(String name) {
        Log.d(TAG, "displayResultNotification()");

        BackupInfo backupInfo = new BackupInfo();
                   backupInfo.setStatus(BackupStatus.BACKUP_SUCCESS);
                   backupInfo.setName(name);

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
        notificationManager.notify(notificationIdResult, notification);
    }


    // Сведения о РК
    public static class BackupInfo implements Parcelable {

        private String name;
        private BackupStatus backupStatus;
        private long progress;
        private int progressMax;

        public BackupInfo() {

        }

        public String getName() {
            return name;
        }
        public BackupStatus getStatus() {
            return backupStatus;
        }
        public long getProgress() {
            return progress;
        }
        public int getProgressMax() {
            return progressMax;
        }

        public void setName(String name) {
            this.name = name;
        }
        public void setStatus(BackupStatus backupStatus) {
            this.backupStatus = backupStatus;
        }
        public void setProgress(long progress) {
            this.progress = progress;
        }
        public void setProgressMax(int progressMax) {
            this.progressMax = progressMax;
        }

        // Конверт
        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(name);
            dest.writeString(backupStatus.name());
            dest.writeLong(progress);
            dest.writeInt(progressMax);
        }

        protected BackupInfo(Parcel in) {
            name = in.readString();
            backupStatus = BackupStatus.valueOf(in.readString());
            progress = in.readLong();
            progressMax = in.readInt();
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<BackupInfo> CREATOR = new Creator<BackupInfo>() {
            @Override
            public BackupInfo createFromParcel(Parcel in) {
                return new BackupInfo(in);
            }

            @Override
            public BackupInfo[] newArray(int size) {
                return new BackupInfo[size];
            }
        };
        // Конверт
    }

    public enum BackupStatus {
        BACKUP_SUCCESS,
        BACKUP_ERROR,
        BACKUP_RUNNING
    }
}
