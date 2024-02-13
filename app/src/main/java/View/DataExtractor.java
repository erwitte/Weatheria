package View;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DataExtractor {
    List<String> extractData(JSONArray weatherData){
        // 3 indizes pro tag, temperatur, wetter(id), datum mit uhrzeit
        List<String> extractedData = new ArrayList<>();

        try {
            for (int day = 0; day < weatherData.length(); day++) {
                JSONObject jsonObject = weatherData.getJSONObject(day);
                JSONArray idArray = new JSONArray(jsonObject.getString("weather"));
                JSONObject tempObject = new JSONObject(jsonObject.getString("main"));

                extractedData.add(tempObject.getString("temp"));
                extractedData.add(idArray.getJSONObject(0).getString("id"));
                extractedData.add(jsonObject.getString("dt_txt"));
            }
        } catch (JSONException e){
            Log.e("JSONerror", "dataExtractor", e);
        }
        return extractedData;
    }
}
