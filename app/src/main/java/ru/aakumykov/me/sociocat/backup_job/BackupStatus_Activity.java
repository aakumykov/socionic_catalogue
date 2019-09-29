package ru.aakumykov.me.sociocat.backup_job;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.aakumykov.me.sociocat.BaseView;
import ru.aakumykov.me.sociocat.R;

public class BackupStatus_Activity extends BaseView {

    @BindView(R.id.textView) TextView textView;
    @BindView(R.id.progressBar) ProgressBar progressBar;

    private BroadcastReceiver broadcastReceiver;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.backup_status_activity);
        ButterKnife.bind(this);

        setPageTitle(R.string.BACKUP_STATUS_page_title);

        setupBroadcastReceiver();

        processInputIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        processInputIntent(getIntent());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public void onUserLogin() {
    }

    @Override
    public void onUserLogout() {
    }



    // Внутренние методы
    private void setupBroadcastReceiver() {
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                BackupService.BackupProgressInfo backupProgressInfo = intent.getParcelableExtra(BackupService.EXTRA_BACKUP_PROGRESS);
                displayBackupInfo(backupProgressInfo);
            }
        };

        registerReceiver(broadcastReceiver, new IntentFilter(BackupService.BROADCAST_BACKUP_PROGRESS_STATUS));
    }

    private void processInputIntent(@Nullable Intent intent) {
        /*if (null != intent) {
            Backup_JobService.BackupProgressInfo backupInfo = intent.getParcelableExtra(Backup_JobService.INTENT_EXTRA_BACKUP_INFO);
            if (null != backupInfo) {
                displayBackupInfo(backupInfo);
            }
        }*/
    }

    private void displayBackupInfo(BackupService.BackupProgressInfo backupProgressInfo) {
        textView.setText(backupProgressInfo.getMessage());
        progressBar.setProgress(backupProgressInfo.getProgress());
        progressBar.setMax(backupProgressInfo.getProgressMax());
    }
}
