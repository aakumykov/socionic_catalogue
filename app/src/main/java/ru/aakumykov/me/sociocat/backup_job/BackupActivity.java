package ru.aakumykov.me.sociocat.backup_job;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;

import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.aakumykov.me.sociocat.BaseView;
import ru.aakumykov.me.sociocat.R;

public class BackupActivity extends BaseView {

    private final static String TAG = "BackupActivity";
    private BroadcastReceiver broadcastReceiver;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.backup_activity);
        ButterKnife.bind(this);

        Backup_JobService.createNotificationChannel(this);

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                BackupService.BackupInfo backupInfo = intent.getParcelableExtra(BackupService.EXTRA_BACKUP_INFO);

                switch (backupInfo.getBackupStatus()) {
                    case BackupService.BACKUP_STATUS_START:
                        Log.d(TAG, "BACKUP_STATUS_START");
                        break;
                    case BackupService.BACKUP_STATUS_RUNNING:
                        Log.d(TAG, "BACKUP_STATUS_RUNNING: "+backupInfo.getProgress()+" из "+backupInfo.getProgressMax());
                        break;
                    case BackupService.BACKUP_STATUS_FINISH:
                        Log.d(TAG, "BACKUP_STATUS_FINISH");
                        break;
                    default:
                        throw new RuntimeException("Unknown backup status");
                }
            }
        };

        registerReceiver(
                broadcastReceiver,
                new IntentFilter(BackupService.BROADCAST_BACKUP_SERVICE)
        );
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }

    @Override
    public void onUserLogin() {

    }
    @Override
    public void onUserLogout() {

    }


    // Нажатия
    @OnClick(R.id.startButton)
    void onStartButtonClicked() {

        Intent intent = new Intent(this, BackupService.class);
        startService(intent);
    }

    @OnClick(R.id.scheduleJobButton)
    void onScheduleJobButtonClicked() {
        Backup_JobService.scheduleJob(this);
    }

    @OnClick(R.id.unscheduleJobButton)
    void onUnscheduleJobButtonClicked() {
        Backup_JobService.unscheduleJob(this);
    }
}
