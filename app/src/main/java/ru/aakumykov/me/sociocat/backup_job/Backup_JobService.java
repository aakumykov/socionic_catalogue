package ru.aakumykov.me.sociocat.backup_job;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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

    private final static String TAG = "Backup_JobService";
    private final static int backupJobServiceId = R.id.backup_job_service_id;
    private BroadcastReceiver broadcastReceiver;


    // Внешние статические методы
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
    public void onCreate() {
        super.onCreate();

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
//                Log.d(TAG, "onReceive()");

                String serviceStatus = intent.getStringExtra(BackupService.EXTRA_SERVICE_STATUS);
                Log.d(TAG, "service status: "+serviceStatus);

                /*switch (backupProgressInfo.getBackupStatus()) {
                    case BackupService.SERVICE_STATUS_START:
                        Log.d(TAG, "SERVICE_STATUS_START");
                        break;
                    case BackupService.SERVICE_STATUS_RUNNING:
                        Log.d(TAG, "SERVICE_STATUS_RUNNING: "+ backupProgressInfo.getProgress()+" из "+ backupProgressInfo.getProgressMax());
                        break;
                    case BackupService.SERVICE_STATUS_FINISH:
                        Log.d(TAG, "SERVICE_STATUS_FINISH");
                        break;
                    default:
                        throw new RuntimeException("Unknown backup status");
                }*/
            }
        };

        registerReceiver(
                broadcastReceiver,
                new IntentFilter(BackupService.BROADCAST_BACKUP_SERVICE_STATUS)
        );
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }


    @Override
    public boolean onStartJob(JobParameters params) {
        Log.d(TAG, "onStartJob()");
        startService(new Intent(this, BackupService.class));
        return true;
    }
    @Override
    public boolean onStopJob(JobParameters params) {
        Log.d(TAG, "onStopJob()");
        return false;
    }

}
