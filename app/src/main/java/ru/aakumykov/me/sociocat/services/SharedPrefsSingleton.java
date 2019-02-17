package ru.aakumykov.me.sociocat.services;


import android.content.SharedPreferences;

import ru.aakumykov.me.sociocat.interfaces.iSharedPrefsSingleton;

public class SharedPrefsSingleton implements iSharedPrefsSingleton {

    private static SharedPreferences sharedPreferences;

    /* Одиночка */
    private static volatile SharedPrefsSingleton ourInstance;
    public synchronized static SharedPrefsSingleton getInstance(String name) {
        synchronized (SharedPrefsSingleton.class) {
            if (null == ourInstance) {
                ourInstance = new SharedPrefsSingleton();
                sharedPreferences =
            }
            return ourInstance;
        }
    }
    private SharedPrefsSingleton() {}
    /* Одиночка */


}
