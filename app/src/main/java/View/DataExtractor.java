package View;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import Controller.DateToWeekday;

public class DataExtractor {
    List<String> extractData(JSONArray weatherData){
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
}
