package Controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Converts a date string to the name of the corresponding weekday.
 */
public class DateToWeekday {

    /**
     * Converts a date string in the format "yyyy-MM-dd HH:mm:ss" to the full name of the weekday in German.
     *
     * @param _date The date string to be converted.
     * @return The full name of the weekday in German.
     */
    public String getWeekday(String _date){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime dateTime = LocalDateTime.parse(_date, formatter);
        LocalDate date = dateTime.toLocalDate();

        return date.getDayOfWeek().getDisplayName(java.time.format.TextStyle.FULL, Locale.GERMAN);
    }
}
