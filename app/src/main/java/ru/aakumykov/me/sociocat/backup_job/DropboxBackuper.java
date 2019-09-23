package ru.aakumykov.me.sociocat.backup_job;

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

import ru.aakumykov.me.sociocat.utils.MyUtils;

public class DropboxBackuper /*implements iCloudBackuper*/ {

    public interface iDropboxBackuperCallbacks {
        void onBackupStart();
        void onBackupFinish();
        void onBackupSuccess(BackupItemInfo backupItemInfo);
        void onBackupFail(String errorMsg);
    }
    public interface iCreateDirCallbacks {
        void onCreateDirSuccess(String dirName);
        void onCreateDirFail(String errorMsg);
    }
    public interface iUploadStringCallbacks {
        void onUploadStringSuccess(String fileName, String md5sum);
        void onUploadStringFail(String errorMsg);
    }
    public interface iDownloadStringCallbacks {
        void onStringDownloadSuccess(String text);
        void onStringDownloadError(String errorMsg);
    }

    public final static int MESSAGE_BACKUP_START = 10;
    public final static int MESSAGE_BACKUP_SUCCESS = 20;
    public final static int MESSAGE_BACKUP_FAIL = 30;

    private final static String TAG = "DropboxBackuper";
    private DbxClientV2 client;


    // Конструктор
    public DropboxBackuper(String accessToken) {
        DbxRequestConfig dbxRequestConfig = new DbxRequestConfig("dropbox/java-tutorial", "en_US");
        client = new DbxClientV2(dbxRequestConfig, accessToken);
    }


    // Внешние методы
    public void backupString(String dirName, String fileName, String fileExtension, String textData, iDropboxBackuperCallbacks callbacks) {

        Handler handler = prepareHandler(callbacks);

        BackupItemInfo backupItemInfo = new BackupItemInfo();

        Runnable runnable = new Runnable() {
            @Override
            public void run() {

                handler.sendEmptyMessage(MESSAGE_BACKUP_START);

                createDir(dirName, new iCreateDirCallbacks() {
                    @Override
                    public void onCreateDirSuccess(String dirName) {
                        backupItemInfo.setDirName(dirName);

                        uploadString(dirName, fileName, fileExtension, textData, true, new iUploadStringCallbacks() {
                            @Override
                            public void onUploadStringSuccess(String fileName, String md5sum) {
                                backupItemInfo.setFileName(fileName);
                                backupItemInfo.setMd5hash(md5sum);

                                Message message = handler.obtainMessage(MESSAGE_BACKUP_SUCCESS, backupItemInfo);
                                handler.sendMessage(message);
                            }

                            @Override
                            public void onUploadStringFail(String errorMsg) {
                                Message message = handler.obtainMessage(MESSAGE_BACKUP_FAIL, errorMsg);
                                handler.sendMessage(message);
                            }
                        });
                    }

                    @Override
                    public void onCreateDirFail(String errorMsg) {
                        Message message = handler.obtainMessage(MESSAGE_BACKUP_FAIL, errorMsg);
                        handler.sendMessage(message);
                    }
                });
            }
        };

        new Thread(runnable).start();
    }

    public void backupFile(String fileName, byte[] data) {

    }


    // Внутренние методы
    private void createDir(String dirName, iCreateDirCallbacks callbacks) {
        try {
            dirName = "/" + dirName;

            CreateFolderResult createFolderResult = client.files().createFolderV2(dirName);

            FolderMetadata folderMetadata = createFolderResult.getMetadata();

            String createdFolderName = folderMetadata.getName();

            callbacks.onCreateDirSuccess(createdFolderName);
        }
        catch (Exception e) {
            Log.e(TAG, e.getMessage()); e.printStackTrace();
            callbacks.onCreateDirFail(e.getMessage());
        }
    }

    private void uploadString(String dirName, String fileName, String fileExtension, String stringData, boolean verifyHash, iUploadStringCallbacks callbacks) {
        try {
            String firstHash = MyUtils.md5sum(stringData) + "";
            String remoteFileName = "/" + dirName + "/" + fileName + "." + fileExtension;
            byte[] textBytes = stringData.getBytes();

            try (InputStream byteArrayInputStream = new ByteArrayInputStream(textBytes)) {
                // Отправка на сервер
                FileMetadata uploadMetadata = client.files()
                        .uploadBuilder(remoteFileName)
                        .uploadAndFinish(byteArrayInputStream);


                String uploadedFileName = uploadMetadata.getName();
                Log.d(TAG, "uploadedFileName: "+uploadedFileName);

                downloadString(remoteFileName, new iDownloadStringCallbacks() {
                    @Override
                    public void onStringDownloadSuccess(String text) {
                        String secondHash = MyUtils.md5sum(text);
                        if (firstHash.equals(secondHash))
                            callbacks.onUploadStringSuccess(uploadedFileName, secondHash);
                        else
                            callbacks.onUploadStringFail(
                                    "Hashes of string '"+stringData+"' are mismatch: "+firstHash+" - "+secondHash
                            );
                    }

                    @Override
                    public void onStringDownloadError(String errorMsg) {

                    }
                });
            }
            catch (Exception e) {
                Log.e(TAG, e.getMessage());
                e.printStackTrace();
            }
        }
        catch (Exception e) {
            Log.e(TAG, e.getMessage()); e.printStackTrace();
            callbacks.onUploadStringFail(e.getMessage());
        }
    }

    private void downloadString(String remoteFileName, iDownloadStringCallbacks callbacks) {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

            DbxDownloader<FileMetadata> downloader = client.files().download(remoteFileName);
            downloader.download(byteArrayOutputStream);

            String text = byteArrayOutputStream.toString();
            byteArrayOutputStream.close();

            callbacks.onStringDownloadSuccess(text);
        }
        catch (Exception ex) {
            Log.e(TAG, ex.getMessage());
            ex.printStackTrace();
        }
    }

    private Handler prepareHandler(iDropboxBackuperCallbacks callbacks) {
        return new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case MESSAGE_BACKUP_START:
                        callbacks.onBackupStart();
                        break;
                    case MESSAGE_BACKUP_SUCCESS:
                        BackupItemInfo backupItemInfo = (BackupItemInfo) msg.obj;
                        callbacks.onBackupSuccess(backupItemInfo);
                        break;
                    case MESSAGE_BACKUP_FAIL:
                        String errorMsg = (String) msg.obj;
                        callbacks.onBackupFail(errorMsg);
                        break;
                    default:
                        super.handleMessage(msg);
                }
            }
        };
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

