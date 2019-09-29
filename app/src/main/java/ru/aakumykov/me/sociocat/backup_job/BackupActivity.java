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

        BackupService.createNotificationChannel(this);
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
