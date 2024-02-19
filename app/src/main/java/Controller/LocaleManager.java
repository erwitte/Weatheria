package Controller;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import java.util.Locale;

/**
 * Manages the application's locale settings, allowing for dynamic changes in language preferences.
 * This class provides a method to switch the application's current language between German and English.
 */
public class LocaleManager {

    /**
     * Changes the application's current locale to German if it is currently set to English, and vice versa.
     * After changing the locale, it updates the application's resources to reflect the change in language.
     * This method is used to dynamically adjust the application's language setting based on user interaction
     * or specific application requirements.
     *
     * @param context The current context of the application, used to access and modify its resources.
     */
    public void setLocale(Context context) {
        String language;
        Locale locale = Locale.getDefault();
        // fragt aktuelle locale ab und vergleicht diese mit de
        if (locale.getLanguage().equals("de"))
            language = "en";
        else
            language = "de";
        locale = new Locale(language);
        // setzt gerade erzeugte locale als system standard
        Locale.setDefault(locale);
        Resources resources = context.getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        // lädt darstellung neu
        resources.updateConfiguration(config, resources.getDisplayMetrics());
    }
}
