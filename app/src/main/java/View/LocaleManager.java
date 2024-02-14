package View;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import java.util.Locale;

public class LocaleManager {

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
