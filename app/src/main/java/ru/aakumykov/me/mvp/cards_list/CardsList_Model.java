package ru.aakumykov.me.mvp.cards_list;

import android.util.Log;

import ru.aakumykov.me.mvp.login.iLogin;

public class CardsList_Model implements iCardsList.Model {

    private final static String TAG = "cards_list";

    /* Одиночка: начало */
    private volatile static CardsList_Model ourInstance;
    static synchronized CardsList_Model getInstance() {
        if (null == ourInstance) {
            synchronized (CardsList_Model.class) {
                ourInstance = new CardsList_Model();
            }
        }
        return ourInstance;
    }
    private CardsList_Model() {}
    /* Одиночка: конецъ */

//    private FirebaseDatabase

    @Override
    public void loadList() {

    }
}
