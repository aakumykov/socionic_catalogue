package ru.aakumykov.me.mvp.card_edit;

public class CardEdit_Model implements iCardEdit.Model {

    /* Одиночка */
    private static volatile CardEdit_Model ourInstance = new CardEdit_Model();
    private CardEdit_Model() { }
    public static synchronized CardEdit_Model getInstance() {
        synchronized (CardEdit_Model.class) {
            if (null == ourInstance) {
                ourInstance = new CardEdit_Model();
            }
            return ourInstance;
        }
    }
    /* Одиночка */

    private final static String TAG = "CardEdit_Model";


}
