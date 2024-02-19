package Controller;

import android.content.Context;
import android.widget.Button;
import android.widget.GridLayout;
import android.view.View;

import com.example.weatheria.R;

import java.util.ArrayList;
import java.util.List;

import Model.ForecastDataWindow;
import View.LayoutManager;

import Model.Location;
import Model.CurrentWeatherWindow;

/**
 * Handles the creation and display of weather forecast information within the application.
 * This class manages the retrieval of weather data, the transformation of temperature units,
 * and the dynamic generation of views to present the forecast data.
 */
public class Forecast {

    private final WeatherFetcher weatherFetcher;
    private final TempTransformer tempTransformer;
    private final Location location;
    private final Context context;
    private final LayoutManager layoutManager;
    // zählt die erzeugten Views dieses Fenster
    // layoutManager.updateLayout löscht diese wieder
    private int childCount = 0;
    private final DataExtractor extractor;
    private boolean isViewed;
    private int forecastViewCounter;
    private final CurrentWeatherWindow currentWeatherWindow;
    /**
     * Initializes a new instance of Forecast with the specified context, layout manager, and location.
     * @param context The application context.
     * @param layoutManager The layout manager responsible for updating the UI.
     * @param location The location for which the weather forecast will be displayed.
     */

    public Forecast(Context context, LayoutManager layoutManager, Location location){
        this.context = context;
        this.layoutManager = layoutManager;
        FileWriterReader fileWriterReader = new FileWriterReader(context);
        this.weatherFetcher = new WeatherFetcher(context, fileWriterReader);
        this.location = location;
        this.isViewed = false;
        this.extractor = new DataExtractor();
        this.tempTransformer = new TempTransformer();
        this.currentWeatherWindow = new CurrentWeatherWindow(context, location);
        forecastViewCounter = 0;

        // speichert den genauen namen der aktuellen vorhersage für den nächsten start
        fileWriterReader.setExactName(location.getExactName());
    }

    /**
     * Retrieves and constructs the views necessary for displaying the current weather forecast.
     * @return A list of views representing the current weather forecast.
     */
    public List<View> getForecastView(){
        List<View> forecastView = new ArrayList<>();
        forecastView.add(createBackBtn());
        forecastView.add(createFahrenheitBtn());

        for (View v : currentWeatherWindow.createCurrentWeatherWindow()) {
            forecastView.add(v);
            childCount++;
        }

        forecastView.addAll(createChoiceBtns());
        return forecastView;
    }

    /**
     * Creates a back button for navigation.
     * @return A Button configured for returning to the previous screen.
     */
    private Button createBackBtn(){
        Button backBtn = new Button(context);
        backBtn.setText(context.getString(R.string.zurueck));
        backBtn.setBackgroundResource(R.drawable.roundec_corners);

        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width = 0;
        params.height = 0;
        params.rowSpec = GridLayout.spec(0, 7, 1f);
        params.columnSpec = GridLayout.spec(24, 10, 1f);
        params.setMargins(0,0,0,0);
        backBtn.setLayoutParams(params);

        backBtn.setOnClickListener(v -> {
            layoutManager.goBack(childCount);
        });
        childCount++;
        return backBtn;
    }

    /**
     * Creates a button for toggling the temperature display between Fahrenheit and Celsius.
     * @return A Button configured for toggling the temperature unit.
     */
    private Button createFahrenheitBtn(){
        Button fahrenheitBtn = new Button(context);
        fahrenheitBtn.setText("Fahrenheit");
        fahrenheitBtn.setBackgroundResource(R.drawable.roundec_corners);

        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width = 0;
        params.height = 0;
        params.rowSpec = GridLayout.spec(0, 7, 1f);
        params.columnSpec = GridLayout.spec(0, 11, 1f);
        params.setMargins(0,0,0,0);
        fahrenheitBtn.setLayoutParams(params);

        fahrenheitBtn.setOnClickListener(view -> {
            if(isViewed) {
                layoutManager.removeLastChildren(forecastViewCounter);
                forecastViewCounter = 0;
                isViewed = false;
            }
            if (!tempTransformer.getIsFahrenheit()) {
                fahrenheitBtn.setText("Celsius");
            } else {
                fahrenheitBtn.setText("Fahrenheit");
            }
            tempTransformer.changeIsFahrenheit();
        });
        childCount++;
        return fahrenheitBtn;
    }

    /**
     * Creates buttons for selecting different forecast views (e.g., today, tomorrow, 3 days).
     * @return A list of Buttons for forecast selection.
     */
    private List<Button> createChoiceBtns(){
        List<Button> btnList = new ArrayList<>();
        btnList.add(createTodayBtn());
        btnList.add(createTomorrowBtn());
        btnList.add(create3DaysBtn());
        return btnList;
    }

    /**
     * Creates a button for displaying today's forecast.
     * @return A Button configured to display today's weather forecast.
     */
    private Button createTodayBtn(){
        Button todayBtn = new Button(context);
        todayBtn.setText(context.getString(R.string.heute));
        todayBtn.setBackgroundResource(R.drawable.roundec_corners);

        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width = 0;
        params.height = 0;
        params.rowSpec = GridLayout.spec(18, 7, 1f);
        params.columnSpec = GridLayout.spec(1, 10, 1f);
        params.setMargins(0,0,0,0);
        todayBtn.setLayoutParams(params);
        childCount++;

        todayBtn.setOnClickListener(v -> {
            if (isViewed)
                layoutManager.removeLastChildren(forecastViewCounter);
            forecastViewCounter = 0;
            layoutManager.updateLayout(getDailyForecast(), -1);
            isViewed = true;
        });
        return todayBtn;
    }

    /**
     * Creates a button for displaying tomorrow's forecast.
     * @return A Button configured to display tomorrow's weather forecast.
     */
    private Button createTomorrowBtn(){
        Button tomorrowBtn = new Button(context);
        tomorrowBtn.setText(context.getString(R.string.morgen));
        tomorrowBtn.setBackgroundResource(R.drawable.roundec_corners);

        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width = 0;
        params.height = 0;
        params.rowSpec = GridLayout.spec(18, 7, 1f);
        params.columnSpec = GridLayout.spec(12, 10, 1f);
        params.setMargins(0,0,0,0);
        tomorrowBtn.setLayoutParams(params);
        childCount++;

        tomorrowBtn.setOnClickListener(v -> {
            if (isViewed)
                layoutManager.removeLastChildren(forecastViewCounter);
            forecastViewCounter = 0;
            layoutManager.updateLayout(getTomorrowsForecast(), -1);
            isViewed = true;
        });
        return tomorrowBtn;
    }

    /**
     * Creates a button for displaying the forecast for the next three days.
     * @return A Button configured to display the forecast for the next three days.
     */
    private Button create3DaysBtn(){
        Button threeDaysBtn = new Button(context);
        threeDaysBtn.setText(context.getString(R.string.dreitage));
        threeDaysBtn.setBackgroundResource(R.drawable.roundec_corners);

        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width = 0;
        params.height = 0;
        params.rowSpec = GridLayout.spec(18, 7, 1f);
        params.columnSpec = GridLayout.spec(23, 10, 1f);
        params.setMargins(0,0,0,0);
        threeDaysBtn.setLayoutParams(params);
        childCount++;
        return threeDaysBtn;
    }

    /**
     * Retrieves and prepares the daily forecast data for presentation.
     * @return A list of views representing the daily forecast.
     */
    private List<View> getDailyForecast(){
        List<String> forecastData = extractor.extractData(weatherFetcher.getTodaysWeather(location.getLatitude(), location.getLongitude()));
        forecastData = tempTransformer.calculateTemp(forecastData);

        return  getForecastWindow(forecastData);
    }

    /**
     * Retrieves and prepares tomorrow's forecast data for presentation.
     * @return A list of views representing tomorrow's forecast.
     */
    private List<View> getTomorrowsForecast(){
        List<String> forecastData = extractor.extractData(weatherFetcher.getTomorrowsWeather(location.getLatitude(), location.getLongitude()));
        forecastData = tempTransformer.calculateTemp(forecastData);

        return  getForecastWindow(forecastData);
    }

    /**
     * Generates the forecast data window for a given set of forecast data.
     * @param forecastData A list of strings containing the forecast data.
     * @return A list of views constructed to display the forecast data.
     */
    private List<View> getForecastWindow(List<String> forecastData){
        ForecastDataWindow forecastDataWindow = new ForecastDataWindow(forecastData, context);
        List<View> forecastWindow = forecastDataWindow.createDataWindow();
        for (View v : forecastWindow){
            childCount++;
            forecastViewCounter++;
        }
        return forecastWindow;
    }
}
