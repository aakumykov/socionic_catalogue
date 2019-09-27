package ru.aakumykov.me.sociocat.backup_job;

import android.app.Notification;
import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.concurrent.TimeUnit;

import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.utils.MyUtils;

public class Backup_JobService extends JobService {

    private final static String TAG = "Backup_JobService";
    private final static int sJobId = R.id.backup_job_service_id;

    public final static String BACKUP_JOB_NOTIFICATION_CHANNEL = "BACKUP_JOB_NOTIFICATION_CHANNEL";
    private NotificationManagerCompat notificationManager;


    public Backup_JobService() {
        super();
        notificationManager = NotificationManagerCompat.from(this);
    }


    public static void createNotificationChannel(Context context)
    {
        MyUtils.createNotificationChannel(
                context,
                BACKUP_JOB_NOTIFICATION_CHANNEL,
                context.getResources().getString(R.string.BACKUP_JOB_SERVICE_channel_title),
                context.getResources().getString(R.string.BACKUP_JOB_SERVICE_channel_description),
                NotificationManagerCompat.IMPORTANCE_LOW
        );
    }

    public static void scheduleJob(Context context)
    {
        ComponentName backupJobService = new ComponentName(context, Backup_JobService.class);

        JobInfo.Builder jobBuilder = new JobInfo.Builder(sJobId, backupJobService);
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
        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        jobScheduler.cancel(sJobId);
        Log.d(TAG, "Задача (должно быть) удалена из планировщика");
    }


    @Override
    public boolean onStartJob(JobParameters params) {
        Log.d(TAG, "onStartJob()");

        /*NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this, )
                        .setSmallIcon(iconId)
                        .setContentTitle(title)
                        .setAutoCancel(true);*/

//        startForeground();

        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.d(TAG, "onStopJob()");
        return false;
    }
}
