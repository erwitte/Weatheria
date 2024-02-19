package Model;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.util.TypedValue;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.View;

import com.example.weatheria.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import Controller.DataExtractor;
import Controller.FileWriterReader;
import Controller.TempTransformer;
import Controller.WeatherFetcher;

/**
 * Constructs and displays the current weather information for a specified location.
 * This class manages the creation of views for displaying location name, temperature, weather icon,
 * and humidity information based on the current weather data.
 */
public class CurrentWeatherWindow {
    private final Context context;
    private final Location location;
    private final JSONObject currentWeather;
    private final TempTransformer tempTransformer;
    private final DataExtractor extractor;
    private final IconChooser iconChooser;

    /**
     * Initializes a new instance of CurrentWeatherWindow with the specified context and location.
     * Fetches the current weather data for the given location and prepares the view components.
     *
     * @param context The application context used for creating view elements.
     * @param location The location object containing the latitude and longitude for the weather data request.
     */
    public CurrentWeatherWindow(Context context, Location location){
        // todo fileReaderWriter nicht erzeugen sondern als Parameter übergeben
        this.context = context;
        this.location = location;
        FileWriterReader fileWriterReader = new FileWriterReader(context);
        WeatherFetcher weatherFetcher = new WeatherFetcher(context, fileWriterReader);
        this.tempTransformer = new TempTransformer();
        this.extractor = new DataExtractor();
        this.iconChooser = new IconChooser(context);
        this.currentWeather = weatherFetcher.getCurrentWeather(location.getLatitude(), location.getLongitude());
    }

    /**
     * Creates and returns a list of views containing the current weather information.
     *
     * @return A list of views representing the current weather conditions.
     */
    public List<View> createCurrentWeatherWindow(){
        List<View> currentWeaterWindow = new ArrayList<>();
        currentWeaterWindow.add(createLocationName());
        currentWeaterWindow.add(createLocationTemperature());
        currentWeaterWindow.add(createWeatherIcon());
        currentWeaterWindow.add(createHumidityWord());
        currentWeaterWindow.add(createHumidityPercentage());

        return currentWeaterWindow;
    }

    /**
     * Creates a TextView displaying the name of the location.
     *
     * @return A TextView configured to display the location name.
     */
    private View createLocationName(){
        TextView locationName = new TextView(context);
        locationName.setText(location.getName());

        GridLayout.LayoutParams paramsName = new GridLayout.LayoutParams();
        paramsName.width = 0;
        paramsName.height = 0;
        paramsName.rowSpec = GridLayout.spec(2, 4, 1f);
        paramsName.columnSpec = GridLayout.spec(15, 10, 1f);
        paramsName.setMargins(0,0,0,0);

        locationName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        locationName.setTextColor(Color.BLACK);
        locationName.setLayoutParams(paramsName);

        return locationName;
    }

    /**
     * Creates a TextView displaying the current temperature at the location.
     *
     * @return A TextView configured to display the temperature.
     */
    private View createLocationTemperature(){
        TextView locationTemperature = new TextView(context);
        try {
            double kelvin = currentWeather.getJSONObject("main").getDouble("temp");
            if (!tempTransformer.getIsFahrenheit())
                locationTemperature.setText(tempTransformer.kelvinToCelsius(kelvin) + " °C");
            else
                locationTemperature.setText(tempTransformer.kelvingToFahrenheit(kelvin) + " °F");
        } catch (JSONException e){
            Log.e("JSONError", "fetching temperature", e);
            locationTemperature.setText("----");
        }
        GridLayout.LayoutParams paramsTemp = new GridLayout.LayoutParams();
        paramsTemp.width = 0;
        paramsTemp.height = 0;
        paramsTemp.rowSpec = GridLayout.spec(6, 5, 1f);
        paramsTemp.columnSpec = GridLayout.spec(15, 6, 1f);
        paramsTemp.setMargins(0,0,0,0);

        // text in sp skaliert mit screen size
        locationTemperature.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        locationTemperature.setTextColor(Color.BLACK);
        locationTemperature.setLayoutParams(paramsTemp);

        return locationTemperature;
    }

    /**
     * Creates an ImageView displaying the icon representing the current weather condition.
     *
     * @return An ImageView configured to display the weather icon.
     */
    private View createWeatherIcon(){
        int id = extractor.extractId(currentWeather);
        ImageView weatherIcon = iconChooser.chooseIcon(id);

        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width = 150;
        params.height = 150;
        params.rowSpec = GridLayout.spec(11, 2, 1f);
        params.columnSpec = GridLayout.spec(15, 2, 1f);
        params.setMargins(0,0,0,0);
        weatherIcon.setLayoutParams(params);

        return weatherIcon;
    }

    /**
     * Creates a TextView displaying the word "Humidity".
     *
     * @return A TextView configured to display the word "Humidity".
     */
    private View createHumidityWord(){
        TextView humidityWord = new TextView(context);
        humidityWord.setText(context.getString(R.string.feuchtigkeit));

        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width = 0;
        params.height = 0;
        params.rowSpec = GridLayout.spec(12, 4, 1f);
        params.columnSpec = GridLayout.spec(20, 14, 1f);
        params.setMargins(0,0,0,0);

        humidityWord.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        humidityWord.setTextColor(Color.BLACK);
        humidityWord.setLayoutParams(params);

        return humidityWord;
    }

    /**
     * Creates a TextView displaying the current humidity percentage.
     *
     * @return A TextView configured to display the humidity percentage.
     */
    private View createHumidityPercentage(){
        TextView humidityPercentage = new TextView(context);
        String humidityText;
        try {
            humidityText = currentWeather.getJSONObject("main").getString("humidity") + "%";
        } catch (JSONException e){
            humidityText= "----";
        }
        humidityPercentage.setText(humidityText);

        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width = 0;
        params.height = 0;
        params.rowSpec = GridLayout.spec(8, 4, 1f);
        params.columnSpec = GridLayout.spec(22, 4, 1f);
        params.setMargins(0,0,0,0);

        humidityPercentage.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        humidityPercentage.setTextColor(Color.BLACK);
        humidityPercentage.setLayoutParams(params);

        return humidityPercentage;
    }
}
