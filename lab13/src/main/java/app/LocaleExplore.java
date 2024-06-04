package app;
 

import com.DisplayLocales;
import com.Info;
import com.SetLocale;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Scanner;

public class LocaleExplore {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ResourceBundle messages = ResourceBundle.getBundle("res.Messages", Locale.getDefault());

        while (true) {
            System.out.print(messages.getString("prompt"));
            String command = scanner.nextLine();

            switch (command.split(" ")[0]) {
                case "locales":
                    DisplayLocales.execute(messages);
                    break;
                case "set":
                    if (command.split(" ").length > 1) {
                        String languageTag = command.split(" ")[1];
                        SetLocale.execute(languageTag, messages);
                        messages = ResourceBundle.getBundle("res.Messages", Locale.getDefault());
                    } else {
                        System.out.println(messages.getString("invalid"));
                    }
                    break;
                case "info":
                    if (command.split(" ").length > 1) {
                        String languageTag = command.split(" ")[1];
                        Info.execute(languageTag, messages);
                    } else {
                        Info.execute(null, messages);
                    }
                    break;
                default:
                    System.out.println(messages.getString("invalid"));
                    break;
            }
        }
    }
}