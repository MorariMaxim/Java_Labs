package com;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.util.Currency;
import java.util.Locale;
import java.util.ResourceBundle;

public class Info {
    public static void execute(String languageTag, ResourceBundle messages) {
        Locale locale;
        if (languageTag == null || languageTag.isEmpty()) {
            locale = Locale.getDefault();
        } else {
            locale = Locale.forLanguageTag(languageTag);
        }

        DateFormatSymbols symbols = new DateFormatSymbols(locale);
        Currency currency = Currency.getInstance(locale);
        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.FULL, locale);

        System.out.println(messages.getString("info").replace("{0}", locale.toString()));
        System.out.println("Country: " + locale.getDisplayCountry(locale) + " (" + locale.getDisplayCountry() + ")");
        System.out.println("Language: " + locale.getDisplayLanguage(locale) + " (" + locale.getDisplayLanguage() + ")");
        System.out.println("Currency: " + currency.getCurrencyCode() + " (" + currency.getDisplayName(locale) + ")");
        System.out.println("Week Days: " + String.join(", ", symbols.getWeekdays()));
        System.out.println("Months: " + String.join(", ", symbols.getMonths()));
        System.out.println("Today: " + dateFormat.format(System.currentTimeMillis()));
    }
}
