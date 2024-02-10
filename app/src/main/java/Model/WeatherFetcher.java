package Model;

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

//Erik Witte
public class WeatherFetcher {
    private String apiKey;
    public WeatherFetcher(){
        apiKey = "&appid=809847a25e5f68fa0bc19f41354fb5b7";
    }

    // stellt nicht das aktuelle datum
    public JSONObject getCurrentWeather(double latitude, double longitude){
        String queryUrl = "https://api.openweathermap.org/data/2.5/weather?lat=" + Double.toString(latitude)
                + "&lon=" + Double.toString(longitude) + apiKey;
        String apiResponse = getApiData(queryUrl);
        if (apiResponse != null){
            try{
                return new JSONObject(apiResponse);
            } catch (JSONException e){
                Log.e("JSONError", "weatherFetcher, getCurrent", e);
            }
        }
        return null;
    }

    // return 5 days forecast split into 3h parts
    public JSONArray get5Days(double latitude, double longitude){
        String queryUrl = "https://api.openweathermap.org/data/2.5/forecast?lat=" + latitude + "&lon="
                + longitude + apiKey;
        String apiResponse = getApiData(queryUrl);
        if (apiResponse != null) {
            try {
                //API antwort zu JSON
                JSONObject completeJson = new JSONObject(apiResponse);
                return completeJson.getJSONArray("list");
            } catch (JSONException e) {
                Log.e("JSONError", "weatherFetcher, get5Days", e);
            }
        }
        return null;
    }

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

    public JSONArray getToday(){
        JSONArray weatherArray = get5Days(52.2719595, 8.047635);
        JSONArray todaysWeatherArray = new JSONArray();

        // aktuelles Datum beziehen,
        //generiert mit Prompt how to get localdate and only the date
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String formattedDate = currentDate.format(formatter);
        //generiert ende

        // erhöht performance
        try{
            int weatherArrayLength = weatherArray.length();
            for (int forecastSlot=0; forecastSlot < weatherArrayLength; forecastSlot++) {
                JSONObject jsonObject = weatherArray.getJSONObject(forecastSlot);
                String[] dateInJson = jsonObject.getString("dt_txt").split(" ");
                if (formattedDate.equals(dateInJson[0])){
                    todaysWeatherArray.put(weatherArray.getJSONObject(forecastSlot));
                } else
                    forecastSlot += weatherArrayLength;
            }
        } catch(JSONException e){
            Log.e("JSONParseError", "weatherFetcher getToday", e);
        }
        return todaysWeatherArray;
    }
}
