package Controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class DateToWeekday {

    public String getWeekday(String _date){
        // generiert und minimal angepasst (parameter, locale, return anstatt print);
        // Prompt: extract the date and use it in java.time - 2024-02-09 09:00:00
        // Define the formatter that matches the input string
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // Parse the string to a LocalDateTime object
        LocalDateTime dateTime = LocalDateTime.parse(_date, formatter);

        // Extract the LocalDate part
        LocalDate date = dateTime.toLocalDate();

        return date.getDayOfWeek().getDisplayName(java.time.format.TextStyle.FULL, Locale.GERMAN);
    }
}
