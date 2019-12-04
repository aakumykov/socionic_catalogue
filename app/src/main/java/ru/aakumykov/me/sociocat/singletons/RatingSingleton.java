package ru.aakumykov.me.sociocat.singletons;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;

import java.util.HashMap;

import ru.aakumykov.me.sociocat.Config;
import ru.aakumykov.me.sociocat.utils.MyUtils;

public class RatingSingleton implements iRatingSingleton {

    private final static String TAG = "RatingSingleton";
    private static final String SHARDS_NAME = "shards";
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

    // Начало одиночества
    private static volatile RatingSingleton ourInstance;
    public synchronized static RatingSingleton getInstance() {
        synchronized (CommentsSingleton.class) {
            if (null == ourInstance) ourInstance = new RatingSingleton();
            return ourInstance;
        }
    }
    private RatingSingleton() { }
    // Конец одиночества

    @Override
    public void initRatingCounter(String counterName, String counterOwnerKey, iCreateDistributedCounterCallbacks callbacks) {

        CollectionReference counterCollection = firebaseFirestore.collection(counterName);
        DocumentReference counterDocument = counterCollection.document(counterOwnerKey);
        CollectionReference shardsCollection = counterDocument.collection(SHARDS_NAME);

        HashMap<String,Object> documentMap = new HashMap<>();
        documentMap.put("name", "Rating Document"); // TODO: переделать в CounterHolder

        WriteBatch writeBatch = firebaseFirestore.batch();
        writeBatch.set(counterDocument, documentMap, SetOptions.mergeFields("name"));

        for (int i = 0; i< Config.CARDS_RATING_COUNTERS_NUMBER; i++) {
            writeBatch.set(shardsCollection.document(String.valueOf(i)), new CounterObject(), SetOptions.mergeFields("name"));
        }

        writeBatch.commit()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        callbacks.onDistributedCounterCreateSuccess();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        callbacks.onDistributedCounterCreateError(e.getMessage());
                        MyUtils.printError(TAG, e);
                    }
                });
    }

    @Override
    public void getRating(String counterName, String counterOwnerKey, iGetRatingCallbacks callbacks) {

        firebaseFirestore.collection(counterName)
                .document(counterOwnerKey)
                .collection(SHARDS_NAME)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        int count = 0;
                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            CounterObject counterObject = documentSnapshot.toObject(CounterObject.class);
                            if (null != counterObject)
                                count += counterObject.getCount();
                        }
                        callbacks.onGetRatingComplete(count, null);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        callbacks.onGetRatingComplete(0, e.getMessage());
                        MyUtils.printError(TAG, e);
                    }
                });
    }


    // Внутренние классы
    private static class CounterHolder {
        public String name = "CounterHolder";
    }

    private static class CounterObject {
        public String name = "CounterObject";

        private int count = 0;

        public int getCount() {
            return this.count;
        }

        // TODO: попробовать убрать
        public void setCount(int count) {
            this.count = count;
        }
    }
}
