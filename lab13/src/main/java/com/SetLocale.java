package com;

import java.util.Locale;
import java.util.ResourceBundle;

public class SetLocale {
    public static void execute(String languageTag, ResourceBundle messages) {
        Locale locale = Locale.forLanguageTag(languageTag);
        Locale.setDefault(locale);
        System.out.println(messages.getString("locale.set").replace("{0}", locale.toString()));
    }
}