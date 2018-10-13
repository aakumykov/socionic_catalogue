package ru.aakumykov.me.mvp.template_mvvm;

public class TemplateModel implements Interfaces.Model {

    /* Одиночка */
    private static volatile TemplateModel ourInstance = new TemplateModel();
    private TemplateModel() { }
    public static synchronized TemplateModel getInstance() {
        synchronized (TemplateModel.class) {
            if (null == ourInstance) {
                ourInstance = new TemplateModel();
            }
            return ourInstance;
        }
    }
    /* Одиночка */

    private final static String TAG = "TemplateModel";
}
