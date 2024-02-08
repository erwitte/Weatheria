package Controller;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import Model.Location;

//Erik Witte
public class LocationToCoords extends AsyncTask<String, Void, String> {

    private List<Location> matchingLocations;
    private final CountDownLatch latch;
    private String stringFromJson;

    public LocationToCoords(CountDownLatch latch) {
        matchingLocations = new ArrayList<>();
        this.latch = latch;
    }

    @Override
    protected String doInBackground(String... params) {
        String desiredLocation = params[0];
        try {
            //enkodiert den Ortsnamen so, dass er in URL genutzt werden kann
            desiredLocation = URLEncoder.encode(desiredLocation, "UTF-8");
            String queryUrl = "https://nominatim.openstreetmap.org/search?city=" + desiredLocation + "&format=json";

            URL url = new URL(queryUrl);
            // baut verbindung zu api auf
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            //API verlangt User Agent
            urlConnection.setRequestProperty("User-Agent", "Weatheria");

            // liest antwort der api zeile für zeile
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()))) {
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                stringFromJson = response.toString();
                if (stringFromJson != null) {
                    try {
                        // JSON Antwort parsen
                        JSONArray jsonArray = new JSONArray(stringFromJson);
                        if (jsonArray.length() > 0) {
                            // Locations aus JSON filtern und jeweils Objekte erzeugen
                            for (int arrayEntry = 0; arrayEntry < jsonArray.length(); arrayEntry++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(arrayEntry);
                                String locationExactName = jsonObject.getString("display_name");
                                String locationName = jsonObject.getString("name");
                                double locationLatitude = jsonObject.getDouble("lat");
                                double locationLongitude = jsonObject.getDouble("lon");
                                matchingLocations.add(new Location(locationExactName, locationName, locationLatitude, locationLongitude));
                            }
                        }
                        latch.countDown();
                    } catch (JSONException e) {
                        Log.e("JSONParseError", "Error parsing JSON", e);
                    }
                }
            } finally {
                urlConnection.disconnect();
                return "";
            }
        } catch (Exception e) {
            //Error fangen
            Log.e("FetchCityCoordinatesError", "Error fetching city coordinates", e);
            return null;
        }
    }

    public List<Location> getMatchingLocations(){
        return matchingLocations;
    }
}
