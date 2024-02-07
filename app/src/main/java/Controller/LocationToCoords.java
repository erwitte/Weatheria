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

//Erik Witte
public class LocationToCoords extends AsyncTask<String, Void, String> {

    private List<String> matchingLocations;
    private OnLocationsFetchedListener listener;
    public LocationToCoords(OnLocationsFetchedListener listener){
        matchingLocations = new ArrayList<>();
        this.listener = listener;
    }
    @Override
    protected String doInBackground(String... params) {
        String locationName = params[0];
        try{
            //enkodiert den Ortsnamen so, dass er in URL genutzt werden kann
            locationName = URLEncoder.encode(locationName, "UTF-8");
            String queryUrl = "https://nominatim.openstreetmap.org/search?city=" + locationName + "&format=json";

            URL url = new URL(queryUrl);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            //API verlangt User Agent
            urlConnection.setRequestProperty("User-Agent", "Weatheria");

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()))) {
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                return response.toString();
            } finally {
                urlConnection.disconnect();
            }
        } catch (Exception e){
            //Error fangen
            Log.e("FetchCityCoordinatesError", "Error fetching city coordinates", e);
            return null;
        }
    }

    @Override
    protected void onPostExecute(String response) {
        if (response != null && !response.isEmpty()) {
            try {
                // JSON Antwort parsen
                JSONArray jsonArray = new JSONArray(response);
                if (jsonArray.length() > 0) {
                    for (int arrayEntry=0; arrayEntry<jsonArray.length(); arrayEntry++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(arrayEntry);
                        String locationName = jsonObject.getString("display_name");
                        matchingLocations.add(locationName);
                    }
                }
                listener.onLocationsFetched(matchingLocations);
            } catch (JSONException e) {
                Log.e("JSONParseError", "Error parsing JSON", e);
            }
        }
    }

    public List<String> chooseLocation(String locationName){
        execute(locationName);
        return matchingLocations;
    }
}
