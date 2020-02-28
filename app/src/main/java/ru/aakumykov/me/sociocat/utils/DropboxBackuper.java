package ru.aakumykov.me.sociocat.utils;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.dropbox.core.DbxDownloader;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.CreateFolderResult;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.FolderMetadata;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class DropboxBackuper /*implements iCloudBackuper*/ {

    public interface iCreateDirCallbacks {
        void onCreateDirSuccess(String createdDirName);
        void onCreateDirFail(String errorMsg);
    }
    public interface iBackupStringCallbacks {
        void onBackupStringSuccess(BackupItemInfo backupItemInfo);
        void onBackupStringFail(String errorMsg);
    }
    public interface iDownloadStringCallbacks {
        void onDownloadStringSuccess(String text);
        void onDownloadStringError(String errorMsg);
    }
    public interface iBackupFileCallbacks {
        void onBackupFileSuccess(String uploadedFileName);
        void onBackupFileError(String errorMsg);
    }

    public final static int MESSAGE_WORK_SUCCESS = 20;
    public final static int MESSAGE_WORK_FAIL = 30;

    private final static String TAG = "DropboxBackuper";
    private DbxClientV2 client;


    // Конструктор
    public DropboxBackuper(String accessToken) {
        DbxRequestConfig dbxRequestConfig = new DbxRequestConfig("dropbox/java-tutorial", "en_US");
        client = new DbxClientV2(dbxRequestConfig, accessToken);
    }


    // Внешние методы
    public void createDir(String dirName, boolean autorename, iCreateDirCallbacks callbacks) {

        Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case MESSAGE_WORK_SUCCESS:
                        String dirName = (String) msg.obj;
                        callbacks.onCreateDirSuccess(dirName);
                        break;
                    case MESSAGE_WORK_FAIL:
                        String errorMsg = (String) msg.obj;
                        callbacks.onCreateDirFail(errorMsg);
                        break;
                    default:
                        super.handleMessage(msg);
                }
            }
        };

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    CreateFolderResult createFolderResult = client.files().createFolderV2(
                            "/" + dirName,
                            autorename
                    );

                    FolderMetadata folderMetadata = createFolderResult.getMetadata();

                    String createdFolderName = folderMetadata.getName();

                    Message message = handler.obtainMessage(MESSAGE_WORK_SUCCESS, createdFolderName);
                    handler.sendMessage(message);
                }
                catch (Exception e) {
                    MyUtils.processException(TAG, e);

                    Message message = handler.obtainMessage(MESSAGE_WORK_FAIL, MyUtils.getExceptionMessage(e));
                    handler.sendMessage(message);
                }
            }
        };

        new Thread(runnable).start();
    }

    public void backupString(
            String dirName,
            String fileName,
            String fileExtension,
            String inputString,
            boolean autorenameFile,
            iBackupStringCallbacks callbacks
    ) {
        Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case MESSAGE_WORK_SUCCESS:
                        BackupItemInfo backupItemInfo = (BackupItemInfo) msg.obj;
                        callbacks.onBackupStringSuccess(backupItemInfo);
                        break;
                    case MESSAGE_WORK_FAIL:
                        String errorMsg = (String) msg.obj;
                        callbacks.onBackupStringFail(errorMsg);
                        break;
                    default:
                        super.handleMessage(msg);
                }
            }
        };

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                String fileNameWithExtention = fileName + "." + fileExtension;
                String remoteFileName = "/" + dirName + "/" + fileNameWithExtention;

                try (InputStream byteArrayInputStream = new ByteArrayInputStream(inputString.getBytes())) {

                    FileMetadata uploadMetadata = client.files()
                            .uploadBuilder(remoteFileName)
                            .withAutorename(autorenameFile)
                            .uploadAndFinish(byteArrayInputStream);


                    String uploadedFileName = uploadMetadata.getName();

                    downloadString(remoteFileName, new iDownloadStringCallbacks() {
                        @Override
                        public void onDownloadStringSuccess(String text) {

                            String firstHash = MyUtils.md5sum(inputString) + "";
                            String secondHash = MyUtils.md5sum(text);

                            BackupItemInfo backupItemInfo = new BackupItemInfo();
                            backupItemInfo.setDirName(dirName);
                            backupItemInfo.setFileName(fileNameWithExtention);
                            backupItemInfo.setMd5hash(secondHash);

                            if (firstHash.equals(secondHash)) {
                                Message message = handler.obtainMessage(MESSAGE_WORK_SUCCESS, backupItemInfo);
                                handler.sendMessage(message);
                            }
                            else {
                                String errorMsg = "Hashes of string '" + inputString + "' are mismatch: " + firstHash + " - " + secondHash;
                                Message message = handler.obtainMessage(MESSAGE_WORK_FAIL, errorMsg);
                                handler.sendMessage(message);
                            }
                        }

                        @Override
                        public void onDownloadStringError(String errorMsg) {
                            callbacks.onBackupStringFail(errorMsg);
                        }
                    });
                }
                catch (Exception e) {
                    callbacks.onBackupStringFail(MyUtils.getExceptionMessage(e));
                    MyUtils.processException(TAG, e);
                }
            }
        };

        new Thread(runnable).start();
    }

    public void backupFile(
            String dirName,
            String fileName,
            byte[] dataBytes,
            iBackupFileCallbacks callbacks
    ) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                String remoteFileName = "/" + dirName + "/" + fileName;

                try (InputStream byteArrayInputStream = new ByteArrayInputStream(dataBytes)) {

                    FileMetadata uploadMetadata = client.files()
                            .uploadBuilder(remoteFileName)
                            .withAutorename(false)
                            .uploadAndFinish(byteArrayInputStream);

                    String uploadedFileName = uploadMetadata.getName();

                    callbacks.onBackupFileSuccess(uploadedFileName);
                }
                catch (Exception e) {
                    /*MyUtils.getExceptionMessage используется потому, что исключение может быть
                    кривым, не содержать сообщения в нужно месте.*/
                    callbacks.onBackupFileError(MyUtils.getExceptionMessage(e));
                    MyUtils.processException(TAG, e);
                }
            }
        };

        new Thread(runnable).start();
    }


    // Внутренние методы
    private void downloadString(String remoteFileName, iDownloadStringCallbacks callbacks) {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

            DbxDownloader<FileMetadata> downloader = client.files().download(remoteFileName);
            downloader.download(byteArrayOutputStream);

            String text = byteArrayOutputStream.toString();
            byteArrayOutputStream.close();

            callbacks.onDownloadStringSuccess(text);
        }
        catch (Exception e) {
            MyUtils.printError(TAG, e);
        }
    }


    // Внутренние классы
    public static class BackupItemInfo {
        private String fileName;
        private String dirName;
        private int fileSize;
        private String md5hash;

        public BackupItemInfo() {
        }

        public BackupItemInfo(String fileName) {
            this.fileName = fileName;
        }

        public String getFileName() {
            return fileName;
        }
        public void setFileName(String name) {
            this.fileName = name;
        }

        public String getDirName() {
            return dirName;
        }
        public void setDirName(String dirName) {
            this.dirName = dirName;
        }

        public int getFileSize() {
            return fileSize;
        }
        public void setFileSize(int fileSize) {
            this.fileSize = fileSize;
        }

        public String getMd5hash() {
            return md5hash;
        }
        public void setMd5hash(String md5hash) {
            this.md5hash = md5hash;
        }
    }
}

