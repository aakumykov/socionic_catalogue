package ru.aakumykov.me.sociocat.d_backup_job;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.utils.MyUtils;
import ru.aakumykov.me.sociocat.z_base_view.BaseView;

public class BackupStatus_Activity extends BaseView {

    @BindView(R.id.messageView) TextView messageView;
    @BindView(R.id.progressBar) ProgressBar progressBar;

    private final static String TAG = "BackupStatus_Activity";
    private BroadcastReceiver broadcastReceiver;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate()");

        setContentView(R.layout.backup_status_activity);
        ButterKnife.bind(this);

        setPageTitle(R.string.BACKUP_STATUS_page_title);

        setupBroadcastReceiver();

        processInputIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d(TAG, "onNewIntent()");
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
    public void onUserGloballyLoggedIn() {
    }

    @Override
    public void onUserGloballyLoggedOut() {
    }



    // Внутренние методы
    private void setupBroadcastReceiver() {
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(TAG, "onReceive()");
                if (null != intent)
                    processInputIntent(intent);
            }
        };

        registerReceiver(broadcastReceiver, new IntentFilter(BackupService.BROADCAST_BACKUP_PROGRESS));
        registerReceiver(broadcastReceiver, new IntentFilter(BackupService.BROADCAST_BACKUP_RESULT));
    }

    private void processInputIntent(@Nullable Intent intent) {
        if (null != intent) {
            String action = intent.getAction() + "";
            switch (action) {
                case BackupService.ACTION_BACKUP_PROGRESS:
                    displayBackupProgress(intent);
                    break;
                case BackupService.ACTION_BACKUP_RESULT:
                    displayBackupResult(intent);
                    break;
                case BackupService.BROADCAST_BACKUP_PROGRESS:
                    displayBackupProgress(intent);
                    break;
                case BackupService.BROADCAST_BACKUP_RESULT:
                    displayBackupResult(intent);
                    break;
                default:
                    throw new RuntimeException("Unknown intent's action: "+action);
            }
        }
    }

    private void displayBackupProgress(Intent intent) {
        String message = intent.getStringExtra(BackupService.EXTRA_MESSAGE);
        messageView.setText(message);
        MyUtils.show(progressBar);
    }

    private void displayBackupResult(Intent intent) {
        String message = intent.getStringExtra(BackupService.EXTRA_MESSAGE);
        messageView.setText(message);
        MyUtils.hide(progressBar);

        int resultNotificationId = intent.getIntExtra(BackupService.EXTRA_RESULT_NOTIFICATION_ID, -1);
        BackupService.removeResultNotification(this, resultNotificationId);
    }
}
