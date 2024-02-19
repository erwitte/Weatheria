package Controller;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Extracts weather forecast data from JSON responses.
 * This class is responsible for parsing weather data received from a weather API
 * and extracting relevant information such as temperature, weather condition ID, weekday, and date-time.
 */
public class DataExtractor {
    /**
     * Extracts forecast data from a JSONArray of weather data.
     * Each entry in the returned list represents a piece of weather information in the order of
     * temperature, weather condition ID, abbreviated weekday, and date-time for each forecast period.
     *
     * @param weatherData A JSONArray containing weather forecast information.
     * @return A list of strings with extracted weather data for each forecast period.
     */
    public List<String> extractData(JSONArray weatherData){
        DateToWeekday dateToWeekday = new DateToWeekday();
        // 4 indizes pro tag; temperatur, wetter(id), wochentag, datum mit uhrzeit
        List<String> extractedData = new ArrayList<>();

        try {
            for (int day = 0; day < weatherData.length(); day++) {
                JSONObject jsonObject = weatherData.getJSONObject(day);
                JSONArray idArray = new JSONArray(jsonObject.getString("weather"));
                JSONObject tempObject = new JSONObject(jsonObject.getString("main"));

                extractedData.add(tempObject.getString("temp"));
                extractedData.add(idArray.getJSONObject(0).getString("id"));
                String dateTime = jsonObject.getString("dt_txt");
                extractedData.add(dateToWeekday.getWeekday(dateTime).substring(0, 3));
                extractedData.add(dateTime);

            }
        } catch (JSONException e){
            Log.e("JSONerror", "dataExtractor", e);
        }
        return extractedData;
    }

    /**
     * Extracts the weather condition ID from a JSONObject representing current weather.
     * This ID is used to determine the appropriate weather icon.
     *
     * @param currentWeather A JSONObject containing the current weather information.
     * @return The weather condition ID as an integer.
     */
    public int extractId(JSONObject currentWeather){
        try{
            JSONArray weatherArray = currentWeather.getJSONArray("weather");
            JSONObject currentWeatherObject = weatherArray.getJSONObject(0);
            return currentWeatherObject.getInt("id");
        } catch(JSONException e){
            Log.e("JSONError", "forecast.chooseIcon", e);
            return 0;
        }
    }
}
