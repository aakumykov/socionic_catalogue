package io.gitlab.aakumykov.sociocat.utils;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Translator {

    private static Map<String,String> dic = new HashMap<>();

    static {
        dic.put("The email address is badly formatted", "Email написан с ошибкой");
        dic.put("Given String is empty or null", "Пустая строка");
        dic.put("The email address is already in use", "Email уже используется");
        dic.put("A network error", "Ошибка сети");
        dic.put("The given password is invalid. [ Password should be at least 6 characters ]",
                "Пароль должен иметь 6 и более знаков");
//        dic.put("", "");
//        dic.put("", "");
    }

    private Translator() {}

    public static String translate(String translatedString) {

        String lang = Locale.getDefault().getLanguage();
        if (!lang.equals("ru")) return translatedString;

        for (Map.Entry<String, String> entry : dic.entrySet()) {
            if (translatedString.startsWith(entry.getKey())) return entry.getValue();
        }
        return translatedString;
    }

}
