package Model;

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

/**
 * Asynchronously fetches geographical coordinates for a given location name using the OpenStreetMap API.
 * This class is designed to make network requests in the background, parse the JSON response,
 * and populate a list of {@link Location} objects with the matching locations found.
 */
public class LocationToCoords extends AsyncTask<String, Void, String> {

    private final List<Location> matchingLocations;
    private final CountDownLatch latch;

    /**
     * Constructs a new {@code LocationToCoords} instance with a {@link CountDownLatch} to signal completion.
     *
     * @param latch A {@code CountDownLatch} used to await the completion of the asynchronous task.
     */
    public LocationToCoords(CountDownLatch latch) {
        matchingLocations = new ArrayList<>();
        this.latch = latch;
    }

    /**
     * The background operation to fetch coordinates for a location name.
     *
     * @param params The location name to search for.
     * @return A {@code String} indicating the result of the operation, typically unused in this context.
     */
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
                String stringFromJson = response.toString();
                if (stringFromJson != null) {
                    try {
                        // API Antwort zu JSON umwandeln und einen index pro datenblock zuteilen
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
            }
            urlConnection.disconnect();
            return "";
        } catch (Exception e) {
            //Error fangen
            Log.e("FetchCityCoordinatesError", "Error fetching city coordinates", e);
            return null;
        }
    }

    /**
     * Returns the list of {@link Location} objects matching the search query.
     *
     * @return A {@code List<Location>} populated with matching locations.
     */
    public List<Location> getMatchingLocations(){
        return matchingLocations;
    }
}
