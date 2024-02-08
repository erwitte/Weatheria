package Model;

import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.CountDownLatch;

public class WeatherFetcher {
    private String apiKey;

    public WeatherFetcher(){
        apiKey = "&appid=809847a25e5f68fa0bc19f41354fb5b7";
    }

    public String getCurrentWeather(double latitude, double longitude){
        String queryUrl = "https://api.openweathermap.org/data/2.5/weather?lat=" + Double.toString(latitude)
                + "&lon=" + Double.toString(longitude) + apiKey;
        return getApiData(queryUrl);
    }

    public String get5Days(double latitude, double longitude){
        String queryUrl = "https://api.openweathermap.org/data/2.5/forecast?lat=" + Double.toString(latitude) + "&lon="
                + Double.toString(longitude) + apiKey;
        return getApiData(queryUrl);
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
}
