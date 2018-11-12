package ru.aakumykov.me.mvp.services;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import ru.aakumykov.me.mvp.Constants;
import ru.aakumykov.me.mvp.interfaces.iCommentsSingleton;


public class CommentsSingleton implements iCommentsSingleton {

    /* Одиночка */
    private static volatile CommentsSingleton ourInstance;
    public synchronized static CommentsSingleton getInstance() {
        synchronized (CommentsSingleton.class) {
            if (null == ourInstance) ourInstance = new CommentsSingleton();
            return ourInstance;
        }
    }
    private CommentsSingleton() {}
    /* Одиночка */

    // Свойства
    private final static String TAG = "CommentsSingleton";

    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

    private DatabaseReference commentsRef = firebaseDatabase.getReference().child(Constants.COMMENTS_PATH);
//    private DatabaseReference cardsRef = firebaseDatabase.getReference().child(Constants.CARDS_PATH);
//    private StorageReference imagesRef = firebaseStorage.getReference().child(Constants.IMAGES_PATH);
}
