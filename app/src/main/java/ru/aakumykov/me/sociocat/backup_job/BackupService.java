package ru.aakumykov.me.sociocat.backup_job;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.concurrent.TimeUnit;

public class BackupService extends Service {

    public static final String BROADCAST_BACKUP_SERVICE = "ru.aakumykov.me.sociocat.BROADCAST_BACKUP_SERVICE";

    public static final String EXTRA_BACKUP_INFO = "EXTRA_BACKUP_INFO";

    public final static String BACKUP_STATUS_START =   "BACKUP_STATUS_START";
    public final static String BACKUP_STATUS_RUNNING = "BACKUP_STATUS_RUNNING";
    public final static String BACKUP_STATUS_FINISH =  "BACKUP_STATUS_FINISH";

    public final static String BACKUP_RESULT_SUCCESS = "BACKUP_RESULT_SUCCESS";
    public final static String BACKUP_RESULT_ERROR =   "BACKUP_RESULT_ERROR";

    private final static String TAG = "BackupService";


    // Системные методы
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate()");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.d(TAG, "onStartCommand()");

        String name = "Служба резервного копирования";

        sendBroadcast(new BackupInfo(name, BACKUP_STATUS_START));

        new Thread(new Runnable() {
            @Override
            public void run() {
                int max = 5;

                for (int i=0; i<max; i++) {
                    try {
                        TimeUnit.SECONDS.sleep(1);
                    }
                    catch (InterruptedException e) {}

                    sendBroadcast(new BackupInfo(name, BACKUP_STATUS_RUNNING).setProgress(i).setProgressMax(max));
                }

                stopSelf(startId);

                sendBroadcast(new BackupInfo(name, BACKUP_STATUS_FINISH));
            }
        }).start();

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy()");
    }

    @Nullable @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    // Внутренние методы
    private void sendBroadcast(BackupInfo backupInfo) {
        Intent intent = new Intent(BROADCAST_BACKUP_SERVICE);
        intent.putExtra(EXTRA_BACKUP_INFO, backupInfo);
        sendBroadcast(intent);
    }


    // Вложенные классы
    public static class BackupInfo implements Parcelable {

        private String name;
        private String backupStatus;
        private String backupResult;
        private long progress;
        private int progressMax;

        public BackupInfo() {

        }

        public BackupInfo(String name, String backupStatus) {
            this.name = name;
            this.backupStatus = backupStatus;
        }

        public String getName() {
            return name;
        }
        public String getBackupStatus() {
            return backupStatus;
        }
        public String getBackupResult() {
            return backupResult;
        }
        public long getProgress() {
            return progress;
        }
        public int getProgressMax() {
            return progressMax;
        }

        public BackupInfo setName(String name) {
            this.name = name;
            return this;
        }
        public BackupInfo setBackupStatus(String backupStatus) {
            this.backupStatus = backupStatus;
            return this;
        }
        public BackupInfo setBackupResult(String backupResult) {
            this.backupResult = backupResult;
            return this;
        }
        public BackupInfo setProgress(long progress) {
            this.progress = progress;
            return this;
        }
        public BackupInfo setProgressMax(int progressMax) {
            this.progressMax = progressMax;
            return this;
        }

        @NonNull
        @Override
        public String toString() {
            return "BackupInfo {" +
                    "name: " + getName() +
                    ", backupStatus: " + getBackupStatus() +
                    ", backupResult: " + getBackupResult() +
                    ", progress: " + getProgress() +
                    ", progressMax: " + getProgressMax() +
            "}";
        }

        // Конверт
        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(name);
            dest.writeString(backupStatus);
            dest.writeLong(progress);
            dest.writeInt(progressMax);
        }

        protected BackupInfo(Parcel in) {
            name = in.readString();
            backupStatus = in.readString();
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
}
