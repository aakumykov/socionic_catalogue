package ru.aakumykov.me.sociocat.backup_job;

import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import java.util.concurrent.TimeUnit;

import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.utils.MyUtils;

public class Backup_JobService extends JobService {

    private final static String TAG = "Backup_JobService";
    private JobParameters jobParameters;
    private final static int backupJobServiceId = R.id.backup_job_service_id;
    private BroadcastReceiver broadcastReceiver;


    // Системные методы
    @Override
    public void onCreate() {
        super.onCreate();

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String serviceStatus = intent.getStringExtra(BackupService.INTENT_EXTRA_SERVICE_STATUS);
                Log.d(TAG, "onReceive(), serviceStatus: "+serviceStatus);

                // TODO: обработать результат "успех/поражение"
                if (BackupService.SERVICE_STATUS_FINISH.equals(serviceStatus)) {
                    Log.d(TAG, "Остановка Backup_JobService по получению сообщения");
                    jobFinished(jobParameters, false);
                }
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
        this.jobParameters = params;
        startService(new Intent(this, BackupService.class));
        return true; // true - работа продолжается
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.d(TAG, "onStopJob()");
        return false; // false - повторная постановка в очередь не требуется
    }


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

}
