package View;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import java.util.Locale;

public class LocaleHelper {

    public void setLocale(Context context) {
        String language;
        Locale lo = Locale.getDefault();
        String l = lo.getLanguage();
        if (l.equals("de"))
            language = "en";
        else
            language = "de";
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Resources resources = context.getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        resources.updateConfiguration(config, resources.getDisplayMetrics());
    }
}
