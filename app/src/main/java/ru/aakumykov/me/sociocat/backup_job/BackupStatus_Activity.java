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
import ru.aakumykov.me.sociocat.utils.MyUtils;

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
                if (null != intent)
                    displayBackupProgress(intent);
            }
        };

        registerReceiver(broadcastReceiver, new IntentFilter(BackupService.BROADCAST_BACKUP_PROGRESS_STATUS));
    }

    private void processInputIntent(@Nullable Intent intent) {
        if (null != intent) {
//            BackupService.ProgressInfo progressInfo = intent.getParcelableExtra(BackupService.EXTRA_BACKUP_PROGRESS);
        }
    }

    private void displayBackupResult(Intent intent) {
        MyUtils.show(progressBar);
    }

    private void displayBackupProgress(Intent intent) {
        MyUtils.hide(progressBar);
    }

    private void displayBackupInfo(BackupService.ProgressInfo progressInfo) {
        textView.setText(progressInfo.getMessage());
        progressBar.setProgress(progressInfo.getProgress());
        progressBar.setMax(progressInfo.getProgressMax());
    }
}
