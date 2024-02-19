package Controller;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.CountDownLatch;

/**
 * Fetches weather data for a specified location using the OpenWeatherMap API.
 * Handles both current and forecast weather data retrieval based on the device's internet connectivity.
 */
public class WeatherFetcher {
    private final FileWriterReader fileWriterReader;
    private final String apiKey;
    private final InternetChecker internetChecker;

    /**
     * Initializes a new instance of WeatherFetcher with context and file writer/reader utilities.
     *
     * @param context The application's context.
     * @param fileWriterReader Utility for reading from and writing to local storage.
     */
    public WeatherFetcher(Context context, FileWriterReader fileWriterReader){
        this.apiKey = "&appid=809847a25e5f68fa0bc19f41354fb5b7";
        this.fileWriterReader = fileWriterReader;
        this.internetChecker = new InternetChecker(context);
    }

    /**
     * Retrieves current weather data for the given coordinates.
     * If there's no internet connection, attempts to read cached data from local storage.
     *
     * @param latitude The latitude of the location.
     * @param longitude The longitude of the location.
     * @return A JSONObject representing the current weather, or null if unable to fetch or read cached data.
     */
    public JSONObject getCurrentWeather(double latitude, double longitude){
        if(internetChecker.hasInternet()) {
            String queryUrl = "https://api.openweathermap.org/data/2.5/weather?lat=" + latitude
                    + "&lon=" + longitude + apiKey;
            String apiResponse = getApiData(queryUrl);
            if (apiResponse != null) {
                try {
                    JSONObject currenWeather = new JSONObject(apiResponse);
                    fileWriterReader.setCurrentWeather(currenWeather.toString());
                    return currenWeather;
                } catch (JSONException e) {
                    Log.e("JSONError", "weatherFetcher, getCurrent", e);
                }
            }

        } else {
            try {
                return new JSONObject(fileWriterReader.readCurrentWeather());
            } catch (JSONException e){
                Log.e("JSONError", "retrieving current weather", e);
            }
        }
        return null;
    }

    /**
     * Retrieves a 5-day weather forecast from the OpenWeatherMap API for the given coordinates.
     * Caches the data locally for offline access.
     *
     * @param latitude The latitude of the location.
     * @param longitude The longitude of the location.
     * @return A JSONArray containing the forecast data, or null if unable to fetch or read cached data.
     */
    private JSONArray get5DaysJson(double latitude, double longitude){
        if (internetChecker.hasInternet()) {
            String queryUrl = "https://api.openweathermap.org/data/2.5/forecast?lat=" + latitude + "&lon="
                    + longitude + apiKey;
            String apiResponse = getApiData(queryUrl);
            if (apiResponse != null) {
                try {
                    //API antwort zu JSON
                    JSONObject completeJson = new JSONObject(apiResponse);
                    JSONArray fiveDaysJson = completeJson.getJSONArray("list");
                    fileWriterReader.setFiveDaysForecast(fiveDaysJson.toString());
                    return fiveDaysJson;
                } catch (JSONException e) {
                    Log.e("JSONError", "weatherFetcher, get5Days", e);
                }
            }

        } else {
            try{
                return new JSONArray(fileWriterReader.readFiveDaysForecast());
            } catch (JSONException e){
                Log.e("JSONError", "retrieving 5DaysWeather", e);
            }
        }
        return null;
    }

    /**
     * Helper method to make API requests and retrieve the response as a string.
     *
     * @param queryUrl The full URL for the API request.
     * @return The API response as a String, or null if the request fails.
     */
    private String getApiData(String queryUrl){
        CountDownLatch latch = new CountDownLatch(1);
        final StringBuilder bobTheBuilder = new StringBuilder();
        try {
            // link zu current weather
            URL url = new URL(queryUrl);
            // baut verbindung zu api auf
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            // liest antwort der api zeile für zeile
            new Thread(() -> {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()))) {
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    bobTheBuilder.append(response);
                    latch.countDown();
                } catch (Exception e) {
                    Log.e("Exception", "WeatherFetcher, reader", e);
                    latch.countDown();
                }
            }).start();
            latch.await();
            return bobTheBuilder.toString();
        } catch (Exception e){
            Log.e("Exception", "WeatherFetcher, first block", e);
            return null;
        }
    }

    /**
     * Retrieves weather forecast for today based on the device's current date.
     *
     * @param latitude The latitude of the location.
     * @param longitude The longitude of the location.
     * @return A JSONArray containing today's forecast data.
     */
    public JSONArray getTodaysWeather(double latitude, double longitude){
        // aktuelles Datum beziehen,
        //generiert mit Prompt how to get localdate and only the date
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String formattedDate = currentDate.format(formatter);
        //generiert ende

        JSONArray completeWeatherArray = get5DaysJson(latitude, longitude);
        JSONArray weatherArray = new JSONArray();

        try{
            int upToDateInt = parseDateToInt(formattedDate);

            int completeWeatherArrayLength = completeWeatherArray.length();
            for (int forecastSlot=0; forecastSlot < completeWeatherArrayLength; forecastSlot++) {
                JSONObject jsonObject = completeWeatherArray.getJSONObject(forecastSlot);
                String[] dateInJson = jsonObject.getString("dt_txt").split(" ");
                if (parseDateToInt(dateInJson[0]) <= upToDateInt){
                    weatherArray.put(completeWeatherArray.getJSONObject(forecastSlot));
                } else
                    forecastSlot += completeWeatherArrayLength;
            }
        } catch(JSONException e){
            Log.e("JSONParseError", "weatherFetcher getToday", e);
        }
        return weatherArray;

    }

    /**
     * Retrieves weather forecast for tomorrow.
     *
     * @param latitude The latitude of the location.
     * @param longitude The longitude of the location.
     * @return A JSONArray containing tomorrow's forecast data.
     */
    public JSONArray getTomorrowsWeather(double latitude, double longitude){
        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String formattedDate = tomorrow.format(formatter);

        JSONArray completeWeatherArray = get5DaysJson(latitude, longitude);
        JSONArray weatherArray = new JSONArray();

        try{
            int dateToCompareTo = parseDateToInt(formattedDate);
            int completeWeatherArrayLength = completeWeatherArray.length();
            for (int forecastSlot=0; forecastSlot < completeWeatherArrayLength; forecastSlot++) {
                JSONObject jsonObject = completeWeatherArray.getJSONObject(forecastSlot);
                String[] dateInJson = jsonObject.getString("dt_txt").split(" ");
                if (parseDateToInt(dateInJson[0]) == dateToCompareTo){
                    weatherArray.put(completeWeatherArray.getJSONObject(forecastSlot));
                } else if (parseDateToInt(dateInJson[0]) > dateToCompareTo){
                    forecastSlot += completeWeatherArrayLength;
                }
            }
        } catch(JSONException e){
            Log.e("JSONParseError", "weatherFetcher getToday", e);
        }
        return weatherArray;
    }

    // passt nicht ins layout, wäre die methode für 3 Tage Button im Forecast
    /**
     * Retrieves weather forecast for the next three days.
     *
     * @param latitude The latitude of the location.
     * @param longitude The longitude of the location.
     * @return A JSONArray containing the three-day forecast data.
     */
    public JSONArray getThreeDaysWeather(double latitude, double longitude){
        LocalDate today = LocalDate.now();
        LocalDate dayAfterTomorrow = today.plusDays(2);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String formattedDate = dayAfterTomorrow.format(formatter);

        JSONArray completeWeatherArray = get5DaysJson(latitude, longitude);
        JSONArray weatherArray = new JSONArray();

        try{
            int dateToCompareTo = parseDateToInt(formattedDate);
            int completeWeatherArrayLength = completeWeatherArray.length();
            for (int forecastSlot=0; forecastSlot < completeWeatherArrayLength; forecastSlot++) {
                JSONObject jsonObject = completeWeatherArray.getJSONObject(forecastSlot);
                String[] dateInJson = jsonObject.getString("dt_txt").split(" ");
                if (parseDateToInt(dateInJson[0]) <= dateToCompareTo){
                    weatherArray.put(completeWeatherArray.getJSONObject(forecastSlot));
                } else {
                    forecastSlot += completeWeatherArrayLength;
                }
            }
        } catch(JSONException e){
            Log.e("JSONParseError", "weatherFetcher getToday", e);
        }
        return weatherArray;
    }

    // macht alle Daten einschließlich Jahreswechsel vergleichbar
    /**
     * Converts a date string into an integer representation for comparison.
     *
     * @param date The date string in "yyyy-MM-dd" format.
     * @return An integer representation of the date for easy comparison.
     */
    private int parseDateToInt(String date){
        String[] splitDate = date.split("-");
        int day = Integer.parseInt(splitDate[2]);
        int month = Integer.parseInt(splitDate[1]);
        int year = Integer.parseInt(splitDate[0]);

        return year * 1000 + month * 100 + day;
    }
}
