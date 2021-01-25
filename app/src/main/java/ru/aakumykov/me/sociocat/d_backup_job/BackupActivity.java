package ru.aakumykov.me.sociocat.d_backup_job;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import butterknife.ButterKnife;
import butterknife.OnClick;
import io.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.z_base_view.BaseView;

public class BackupActivity extends BaseView {

    private final static String TAG = "BackupActivity";


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
    }

    @Override
    public void onUserGloballyLoggedIn() {

    }
    @Override
    public void onUserGloballyLoggedOut() {

    }


    // Нажатия
    @OnClick(R.id.startServiceButton)
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
