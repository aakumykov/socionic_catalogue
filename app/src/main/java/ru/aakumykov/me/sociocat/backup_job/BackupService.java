package ru.aakumykov.me.sociocat.backup_job;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import ru.aakumykov.me.sociocat.AppConfig;
import ru.aakumykov.me.sociocat.Constants;
import ru.aakumykov.me.sociocat.R;
import ru.aakumykov.me.sociocat.models.Card;
import ru.aakumykov.me.sociocat.models.Comment;
import ru.aakumykov.me.sociocat.models.Tag;
import ru.aakumykov.me.sociocat.models.User;
import ru.aakumykov.me.sociocat.singletons.CardsSingleton;
import ru.aakumykov.me.sociocat.singletons.StorageSingleton;
import ru.aakumykov.me.sociocat.singletons.iCardsSingleton;
import ru.aakumykov.me.sociocat.singletons.iStorageSingleton;
import ru.aakumykov.me.sociocat.utils.DropboxBackuper;
import ru.aakumykov.me.sociocat.utils.MyUtils;
import ru.aakumykov.me.sociocat.utils.NotificationsHelper;

public class BackupService extends Service {


    // ======== НАСТРОЙКА ОБЪЕКТОВ РЕЗЕРВНОГО КОПИРОВАНИЯ ========
    private final static String TAG = "BackupService";
    private static final String SHARED_PREFERENCES_BACKUP_SERVICE = "BACKUP_SERVICE";
    private static final String KEY_CARDS_LIST = "CARDS_LIST";

    private CollectionPool collectionsPool = new CollectionPool(
            new CollectionPair(Constants.CARDS_PATH, Card.class),
            new CollectionPair(Constants.TAGS_PATH, Tag.class),
            new CollectionPair(Constants.COMMENTS_PATH, Comment.class),
            new CollectionPair(Constants.USERS_PATH, User.class),
            new CollectionPair(Constants.ADMINS_PATH, User.class)
    );

    private static boolean isRunning = false;
    private static boolean backupImpossible = true;
    private String targetDirName;
    private String imagesDirName;

    private List<BackupElement> backupPool = new ArrayList<>();
    private List<Card> backuppingCardsList = new ArrayList<>();
    private Map<String, Card> previousCollection = new HashMap<>();


    // ======================== УПРАВЛЕНИЕ СЛУЖБОЙ ========================
    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannel(this);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        final String KEY_NAME = "dropbox_access_token";

        String dropboxAccessToken = (sharedPreferences.contains(KEY_NAME)) ?
                sharedPreferences.getString(KEY_NAME, null) : null;

        if (null != dropboxAccessToken)
            dropboxAccessToken = dropboxAccessToken.trim();

        String dropboxAccessTokenStub = getString(R.string.PREFERENCE_dropbox_access_token_stub);

        // Если dropboxAccessToken содержит заглушку
        if (dropboxAccessTokenStub.equals(dropboxAccessToken)) {

            showCustomNotification(
                    R.string.BACKUP_SERVICE_backup_impossible,
                    R.string.BACKUP_SERVICE_dropbox_access_token_missing
            );

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("perform_database_backup", false);
            editor.commit();
        }
        else {
            backupImpossible = false;
            dropboxBackuper = new DropboxBackuper(dropboxAccessToken);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        //Log.d(TAG, "onStartCommand()");

        if (backupImpossible)
            return START_NOT_STICKY;

        sendServiceBroadcast(SERVICE_STATUS_START);

        startBackup();

        isRunning = true;

        return START_NOT_STICKY;
    }

    @Nullable @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public static boolean isRunning() {
        return isRunning;
    }


    // ======================== РЕЗЕРВНОЕ КОПИРОВАНИЕ ========================
    private DropboxBackuper dropboxBackuper;
    private List<String> backupSuccessList = new ArrayList<>();
    private List<String> backupErrorsList = new ArrayList<>();

    private void startBackup() {
        notifyAboutBackupProgress(MyUtils.getString(this, R.string.BACKUP_SERVICE_backup_started));

        restoreBackupedCards();

        createBackupDirs();
    }

    private void createBackupDirs() {
        String dirName = date2string("yyyy-MM-dd_HH.mm.ss");

        notifyAboutBackupProgress(MyUtils.getString(
                BackupService.this,
                R.string.BACKUP_SERVICE_creating_dir,
                dirName
        ));

        dropboxBackuper.createDir(dirName, true, new DropboxBackuper.iCreateDirCallbacks() {
            @Override
            public void onCreateDirSuccess(String createdDirName) {

                targetDirName = createdDirName;
                storeSuccessMessage(R.string.BACKUP_SERVICE_directory_created, createdDirName);
                imagesDirName = createdDirName +"/images";

                dropboxBackuper.createDir(imagesDirName, false, new DropboxBackuper.iCreateDirCallbacks() {
                    @Override
                    public void onCreateDirSuccess(String createdDirName) {
                        imagesDirName = targetDirName+"/"+createdDirName;
                        storeSuccessMessage(R.string.BACKUP_SERVICE_directory_created, createdDirName);
                        step1FillBackupPool();
                    }

                    @Override
                    public void onCreateDirFail(String errorMsg) {
                        backupErrorsList.add(errorMsg);
                        finishBackupWithError(errorMsg);
                    }
                });
            }

            @Override
            public void onCreateDirFail(String errorMsg) {
                backupErrorsList.add(errorMsg);
                finishBackupWithError(errorMsg);
            }
        });
    }

    private void step1FillBackupPool() {

        CollectionPair collectionPair = collectionsPool.pop();

        if (null == collectionPair) {
            step2ProcessBackupPool();
            return;
        }

        String collectionName = collectionPair.getName();

        switch (collectionName) {
            case Constants.CARDS_PATH:
                loadCards();
                return;
            default:
                loadCollection(collectionName);
        }
    }

    private void step2ProcessBackupPool() {

        if (backupPool.size() == 0) {
            finishBackupWithSuccess();
            return;
        }

        BackupElement backupElement = backupPool.get(0);
        backupPool.remove(0);

        // TODO: неужели есть прямой доступ к свойству класса?
        ElementType elementType = backupElement.getElementType();
        switch (elementType) {
            case JSON:
                backupJSON(backupElement);
                break;
            case IMAGE:
                backupImage(backupElement);
                break;
            default:
                storeErrorMessage(R.string.BACKUP_SERVICE_error_unknown_element_type, elementType.name());
                break;
        }
    }


    private void loadCollection(String collectionName) {
        notifyAboutBackupProgress(MyUtils.getString(
                this,
                R.string.BACKUP_SERVICE_loading_collection,
                collectionName)
        );

        List<String> errorsList = new ArrayList<>();
        List<Object> objectsList = new ArrayList<>();

        // TODO: запаковать в Singleton
        FirebaseFirestore.getInstance().collection(collectionName).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            try {
                                Object object = documentSnapshot.toObject(Object.class);
                                objectsList.add(object);
                            }
                            catch (Exception e) {
                                errorsList.add(e.getMessage());
                                MyUtils.printError(TAG, e);
                            }
                        }

                        if (objectsList.size() > 0) {
                            BackupElement backupElement = new BackupElement(BackupService.ElementType.JSON, collectionName);
                            String json = listOfObjects2JSON(objectsList);
                            backupElement.addJson(json);
                            backupPool.add(backupElement);
                        }

                        if (errorsList.size() > 0) {
                            // TODO: уведомлять?
                            Log.e(TAG, "Errors during loading collection '"+collectionName+"': "+errorsList);
                        }

                        step1FillBackupPool();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        step1FillBackupPool();
                    }
                });
    }

    private void loadCards() {
        String collectionName = Constants.CARDS_PATH;

        notifyAboutBackupProgress(MyUtils.getString(
                this,
                R.string.BACKUP_SERVICE_loading_collection,
                collectionName)
        );

        CardsSingleton.getInstance().loadAllCards(new iCardsSingleton.ListCallbacks() {
            @Override
            public void onListLoadSuccess(List<Card> list) {
                for (Card card : list) {
                    if (card.isImageCard() && cardHasNewImage(card)) {
                        BackupElement backupElement = new BackupElement(BackupService.ElementType.IMAGE, Constants.IMAGES_PATH);
                        String imageFileName = card.getFileName();

                        if (null != imageFileName && imageFileName.startsWith("null."))
                            Log.w(TAG, "null-image detected! "+card);

                        backupElement.setImageFileName(imageFileName);
                        backupElement.setCardKey(card.getKey());
                        backupPool.add(backupElement);
                    }
                }

                backuppingCardsList.addAll(list);

                BackupElement backupElement = new BackupElement(BackupService.ElementType.JSON, collectionName);
                backupElement.addJson(listOfObjects2JSON(list));
                backupPool.add(backupElement);

                step1FillBackupPool();
            }

            @Override
            public void onListLoadFail(String errorMessage) {
                showCustomNotification(R.string.BACKUP_SERVICE_error_loading_collection, collectionName);
                step1FillBackupPool();
            }
        });
    }

    private void backupJSON(BackupElement backupElement) {
        String collectionName = backupElement.getCollectionName();

        notifyAboutBackupProgress(MyUtils.getString(
                BackupService.this,
                R.string.BACKUP_SERVICE_saving_collection,
                collectionName
        ));

        dropboxBackuper.backupString(
                targetDirName,
                collectionName,
                "json",
                backupElement.getJSON(),
                true,
                new DropboxBackuper.iBackupStringCallbacks() {
                    @Override
                    public void onBackupStringSuccess(DropboxBackuper.BackupItemInfo backupItemInfo) {
                        storeSuccessMessage(R.string.BACKUP_SERVICE_collection_is_saved, collectionName);

                        if (Constants.CARDS_PATH.equals(collectionName))
                            storeBackupedCards();

                        step2ProcessBackupPool();
                    }

                    @Override
                    public void onBackupStringFail(String errorMsg) {
                        storeErrorMessage(R.string.BACKUP_SERVICE_error_saving_collection, collectionName, errorMsg);
                        step2ProcessBackupPool();
                    }
                }
        );
    }

    private void backupImage(BackupElement backupElement) {

        String imageFileName = backupElement.getImageFileName();
        String cardKey = backupElement.getCardKey();

        notifyAboutBackupProgress(MyUtils.getString(
                BackupService.this,
                R.string.BACKUP_SERVICE_saving_image,
                imageFileName
        ));

        StorageSingleton.getInstance().getImage(imageFileName, new iStorageSingleton.GetFileCallbacks() {
            @Override
            public void onFileDownloadSuccess(byte[] imageBytes) {
                dropboxBackuper.backupFile(imagesDirName, imageFileName, imageBytes, new DropboxBackuper.iBackupFileCallbacks() {
                    @Override
                    public void onBackupFileSuccess(String uploadedFileName) {
                        storeSuccessMessage(R.string.BACKUP_SERVICE_image_has_been_saved, imageFileName);
                        step2ProcessBackupPool();
                    }

                    @Override
                    public void onBackupFileError(String errorMsg) {
                        storeErrorMessage(R.string.BACKUP_SERVICE_error_saving_image, imageFileName, cardKey, errorMsg);
                        step2ProcessBackupPool();
                    }
                });
            }

            @Override
            public void onFileDownloadError(String errorMsg) {
                storeErrorMessage(R.string.BACKUP_SERVICE_error_loading_image, imageFileName, cardKey, errorMsg);
                step2ProcessBackupPool();
            }
        });
    }

    private <T> String listOfObjects2JSON(List<T> objectsList) {
        List<String> jsonList = listOfObjects2ListOfJSON(objectsList);
        return "[\n" + TextUtils.join(",\n", jsonList) + "\n]";
    }

    private <T> List<String> listOfObjects2ListOfJSON(List<T> objectsList) {
        List<String> jsonList = new ArrayList<>();
        List<String> errorsList = new ArrayList<>();

        Gson gson = new Gson();

        for (Object item : objectsList) {
            try {
                jsonList.add(gson.toJson(item));
            }
            catch (Exception e) {
                errorsList.add(Arrays.toString(e.getStackTrace()));
            }
        }

        if (errorsList.size() > 0) {
            Log.e(TAG, errorsList.toString());
        }

        return jsonList;
    }

    private void finishBackupWithError(String errorMsg) {
        finishBackup(BACKUP_STATUS_ERROR, errorMsg);
        Log.e(TAG, errorMsg);
    }

    private void finishBackupWithSuccess() {
        String message = MyUtils.getString(
                this,
                R.string.BACKUP_SERVICE_all_collections_are_processed,
                date2string()
        );
        finishBackup(BACKUP_STATUS_SUCCESS, message);
        Log.d(TAG, message);
    }

    private void finishBackup(String backupStatus, String userMessage) {
        removeProgressNotification();

        displayResultNotification(userMessage, backupStatus);

        sendBackupResultBroadcast(userMessage, backupStatus);

        isRunning = false;

        stopSelf();

        sendServiceBroadcast(SERVICE_STATUS_FINISH);

        BackupService.saveLastBackupTime(this);
    }

    // Локальное сохранение, восстановление, проверка-по списка карточек.
    private void storeBackupedCards() {
        List<String> jsonList = listOfObjects2ListOfJSON(backuppingCardsList);

        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_BACKUP_SERVICE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        String listAsString = new Gson().toJson(jsonList);

        editor.putString(KEY_CARDS_LIST, listAsString);
        editor.apply();
    }

    private void restoreBackupedCards() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_BACKUP_SERVICE, Context.MODE_PRIVATE);

        if (sharedPreferences.contains(KEY_CARDS_LIST)) {
            String listAsString = sharedPreferences.getString(KEY_CARDS_LIST, "[]");

            Gson gson = new Gson();
            String[] jsonList = gson.fromJson(listAsString, String[].class);

            String cardJson = "";

            for (String s : jsonList) {
                try {
                    cardJson = s;
                    Card card = gson.fromJson(cardJson, Card.class);
                    previousCollection.put(card.getKey(), card);
                }
                catch (Exception e) {
                    MyUtils.printError(TAG, e);
                }
            }
        }
    }

    private boolean cardHasNewImage(Card newCard) {
        // Нет сведений о предыдущем резервном копировании - считаем, что есть новая картинка.
        if (0 == previousCollection.size())
            return true;

        String newCardKey = newCard.getKey();

        // Карточка не найдена, значит, есть новая картинка.
        if (!previousCollection.containsKey(newCardKey)) {
            return true;
        }
        else {
            try {
                Card oldCard = previousCollection.get(newCardKey);
                if (null == oldCard)
                    return true;

                String oldImageURL = oldCard.getImageURL();

                // Если нет сведений о старом imageURL
                if (null == oldImageURL)
                    return true;

                String newImageURL = newCard.getImageURL();

                return ! oldImageURL.equals(newImageURL);
            }
            catch (Exception e) {
                // При ошибке восстановления из JSON выполняем рез. копирование
                MyUtils.printError(TAG, e);
                return true;
            }
        }
    }



    // ======================== ШИРОКОВЕЩАТЕЛЬНЫЕ СООБЩЕНИЯ ========================
    public static final String BROADCAST_SERVICE_STATUS = "ru.aakumykov.me.sociocat.BROADCAST_SERVICE_STATUS";
    public static final String BROADCAST_BACKUP_PROGRESS = "ru.aakumykov.me.sociocat.BROADCAST_BACKUP_PROGRESS";
    public static final String BROADCAST_BACKUP_RESULT = "ru.aakumykov.me.sociocat.BROADCAST_BACKUP_RESULT";

    public final static String SERVICE_STATUS_START =   "SERVICE_STATUS_START";
    public final static String SERVICE_STATUS_RUNNING = "SERVICE_STATUS_RUNNING";
    public final static String SERVICE_STATUS_FINISH =  "SERVICE_STATUS_FINISH";

    public final static String BACKUP_STATUS_START = "BACKUP_STATUS_START";
    public final static String BACKUP_STATUS_RUNNING = "BACKUP_STATUS_START";
    public final static String BACKUP_STATUS_SUCCESS = "BACKUP_STATUS_SUCCESS";
    public final static String BACKUP_STATUS_ERROR = "BACKUP_STATUS_ERROR";

    public static final String EXTRA_SERVICE_STATUS = "EXTRA_SERVICE_STATUS";
    public static final String EXTRA_BACKUP_STATUS = "EXTRA_BACKUP_STATUS";
    public static final String EXTRA_MESSAGE = "EXTRA_MESSAGE";
    public static final String EXTRA_RESULT_NOTIFICATION_ID = "EXTRA_RESULT_NOTIFICATION_ID";

    private void sendServiceBroadcast(String serviceStatus) {
        Intent intent = new Intent(BROADCAST_SERVICE_STATUS);
        intent.putExtra(EXTRA_SERVICE_STATUS, serviceStatus);
        sendBroadcast(intent);
    }

    private void sendBackupProgressBroadcast(String message, String backupStatus) {
        Intent intent = new Intent(BROADCAST_BACKUP_PROGRESS);
        intent.putExtra(EXTRA_MESSAGE, message);
        intent.putExtra(EXTRA_BACKUP_STATUS, backupStatus);
        sendBroadcast(intent);
    }

    private void sendBackupResultBroadcast(String message, String backupStatus) {
        Intent intent = new Intent(BROADCAST_BACKUP_RESULT);
        intent.putExtra(EXTRA_BACKUP_STATUS, backupStatus);
        intent.putExtra(EXTRA_MESSAGE, message);
        intent.putExtra(EXTRA_RESULT_NOTIFICATION_ID, resultNotificationId);
        sendBroadcast(intent);
    }



    // ======================== УВЕДОМЛЕНИЯ ========================
    public static final String BACKUP_JOB_NOTIFICATION_CHANNEL = "BACKUP_JOB_NOTIFICATION_CHANNEL";

    public static final int PENDING_INTENT_ACTION_BACKUP_PROGRESS = 10;
    public static final int PENDING_INTENT_ACTION_BACKUP_RESULT = 20;

    public static final String ACTION_BACKUP_PROGRESS = "ACTION_BACKUP_PROGRESS";
    public static final String ACTION_BACKUP_RESULT = "ACTION_BACKUP_RESULT";

    private int progressNotificationId = 10;
    private static int resultNotificationId = 20;

    public static void createNotificationChannel(Context context) {
        NotificationsHelper.createNotificationChannel(
                context,
                BACKUP_JOB_NOTIFICATION_CHANNEL,
                R.string.BACKUP_SERVICE_channel_title,
                R.string.BACKUP_SERVICE_channel_description,
                new NotificationsHelper.NotificationChannelCreationCallbacks() {
                    @Override
                    public void onNotificationChannelCreateSuccess() {

                    }

                    @Override
                    public void onNotificationChannelCreateError(String errorMsg) {
                        Log.e(TAG, errorMsg);
                    }
                }
        );
    }

    public static void removeResultNotification(Context context, int resultNotificationId) {
        NotificationManagerCompat.from(context).cancel(resultNotificationId);
    }

    private void displayProgressNotification(String message, String status) {

        // Сведения для страницы сведений о РК
        Intent intent = new Intent(this, BackupStatus_Activity.class);
        intent.setAction(ACTION_BACKUP_PROGRESS);
        intent.putExtra(EXTRA_BACKUP_STATUS, status);
        intent.putExtra(EXTRA_MESSAGE, message);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                PENDING_INTENT_ACTION_BACKUP_PROGRESS,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        // Для уведомления
        String notificationTitle = getResources().getString(R.string.BACKUP_SERVICE_backup);

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, BACKUP_JOB_NOTIFICATION_CHANNEL)
                        .setSmallIcon(R.drawable.ic_backup_job_colored)
                        .setContentTitle(notificationTitle)
                        .setContentText(message)
                        .setUsesChronometer(true)
                        .setOngoing(true)
                        .setProgress(0,0,true)
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true);

        Notification notification = notificationBuilder.build();

        startForeground(progressNotificationId, notification);
    }

    private void removeProgressNotification() {
        stopForeground(true);
    }

    private void displayResultNotification(String message, String status) {

        // Сведения для страницы сведений о РК
        Intent intent = new Intent(this, BackupStatus_Activity.class);
        intent.setAction(ACTION_BACKUP_RESULT);
        intent.putExtra(EXTRA_BACKUP_STATUS, status);
        intent.putExtra(EXTRA_MESSAGE, message);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                PENDING_INTENT_ACTION_BACKUP_RESULT,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        // Для уведомления
        String notificationTitle = getResources().getString(R.string.BACKUP_SERVICE_backup);

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, BACKUP_JOB_NOTIFICATION_CHANNEL)
                        .setSmallIcon(R.drawable.ic_backup_job_colored)
                        .setContentTitle(notificationTitle)
                        .setContentText(message)
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true);

        Notification notification = notificationBuilder.build();

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(resultNotificationId, notification);
    }

    private void showCustomNotification(int titleId, int messageId) {
        String message = getResources().getString(messageId);
        showCustomNotification(titleId, message);
    }

    private void showCustomNotification(int titleID, String message) {
        String title = getResources().getString(titleID);

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, BACKUP_JOB_NOTIFICATION_CHANNEL)
                        .setSmallIcon(R.drawable.ic_backup_job_colored)
                        .setContentTitle(title)
                        .setContentText(message)
                        .setAutoCancel(true);

        Notification notification = notificationBuilder.build();

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(resultNotificationId, notification);
    }


    // ======================== СЛУЖЕБНЫЕ МЕТОДЫ ========================
    public static boolean isTimeToDoBackup(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.SHARED_PREFERENCES_ADMIN, MODE_PRIVATE);

        long lastBackupTime = sharedPreferences.getLong(Constants.PREFERENCE_KEY_LAST_BACKUP_TIME, -1);
        long currentTimeInSeconds = MyUtils.getCurrentTimeInSeconds();
        long elapsedTime = currentTimeInSeconds - lastBackupTime;

        return elapsedTime > AppConfig.BACKUP_INTERVAL_SECONDS;
    }

    public static void saveLastBackupTime(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.SHARED_PREFERENCES_ADMIN, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        long currentTimeInSeconds = MyUtils.getCurrentTimeInSeconds();
        editor.putLong(Constants.PREFERENCE_KEY_LAST_BACKUP_TIME, currentTimeInSeconds);
        editor.apply();
    }

    private void notifyAboutBackupProgress(String message) {
        displayProgressNotification(message, BACKUP_STATUS_RUNNING);
        sendBackupProgressBroadcast(message, BACKUP_STATUS_RUNNING);
    }

    public static String date2string() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss", Locale.CANADA);
        return format.format(new Date());
    }

    public static String date2string(String format) {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format, Locale.CANADA);
            return simpleDateFormat.format(new Date());
        }
        catch (Exception e) {
            return null;
        }
    }

    private void storeSuccessMessage(int messageId, @Nullable String insertedText) {
        String message = (null == insertedText) ?
                MyUtils.getString(this, messageId) :
                MyUtils.getString(this, messageId, insertedText);
        backupSuccessList.add(message);
        Log.d(TAG, message);
    }

    private void storeErrorMessage(int messageId, String... insertedTextPieces) {
        String message;
        switch (insertedTextPieces.length) {
            case 0:
                message = MyUtils.getString(this, messageId);
                break;
            case 1:
                message = MyUtils.getString(this, messageId, insertedTextPieces[0]);
                break;
            default:
                message = MyUtils.getString(this, messageId, insertedTextPieces);
        }
        backupErrorsList.add(message);
        Log.e(TAG, message);
    }


    // Внутренние классы
    private enum ElementType {
        JSON,
        IMAGE
    }

    private static class CollectionPair {
        private String name;
        private Class itemClass;

        public CollectionPair(String name, Class itemClass) {
            this.name = name;
            this.itemClass = itemClass;
        }

        public String getName() {
            return name;
        }

        public Class getItemClass() {
            return itemClass;
        }
    }

    private static class CollectionPool {

        public CollectionPool(CollectionPair... pairs) {
            for (CollectionPair pair : pairs)
                this.push(pair);
        }

        private List<CollectionPair> list = new ArrayList<>();

        public CollectionPair pop() {
            int index = list.size()-1;
            if (index >= 0) {
                CollectionPair collectionPair = list.get(index);
                list.remove(index);
                return collectionPair;
            }
            else {
                return null;
            }
        }

        public void push(CollectionPair collectionPair) {
            list.add(collectionPair);
        }

        public int size() {
            return  list.size();
        }
    }

    private static class BackupElement {
        private BackupService.ElementType elementType;
        private String collectionName;
        private String json;
        private String imageFileName;
        private String cardKey;

        public BackupElement(BackupService.ElementType elementType, String collectionName) {
            this.elementType = elementType;
            this.collectionName = collectionName;
        }

        public void addJson(String json) {
            this.json = json;
        }

        public void setImageFileName(String imageFileName) {
            this.imageFileName = imageFileName;
        }

        public void setCardKey(String cardKey) {
            this.cardKey = cardKey;
        }

        public BackupService.ElementType getElementType() {
            return elementType;
        }

        public String getCollectionName() {
            return collectionName;
        }

        public String getJSON() {
            return json;
        }

        public String getImageFileName() {
            return imageFileName;
        }

        public String getCardKey() {
            return cardKey;
        }
    }
}
