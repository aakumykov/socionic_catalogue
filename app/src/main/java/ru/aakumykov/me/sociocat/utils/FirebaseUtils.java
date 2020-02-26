package ru.aakumykov.me.sociocat.utils;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class FirebaseUtils {
    private FirebaseUtils() {}

    public static void extractObjectsList(QuerySnapshot queryDocumentSnapshots, Class objectClass, ExtractObjectsListCallbacks callbacks) {
        List<Object> objectsList = new ArrayList<>();
        List<String> errorsList = new ArrayList<>();

        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
            try {
                objectsList.add( documentSnapshot.toObject(objectClass) );
            } catch (Exception e) {
                errorsList.add(e.getMessage());
            }
        }

        if (errorsList.size() > 0  && objectsList.size() == 0)
            callbacks.onObjectsListExtractFailed("Errors extracting list", errorsList);
        else
            callbacks.onObjectsListExtractSuccess(objectsList);
    }

    public interface ExtractObjectsListCallbacks {
        void onObjectsListExtractSuccess(List<Object> objectList);
        void onObjectsListExtractFailed(String errorMsg, List<String> errorsList);
    }

}
