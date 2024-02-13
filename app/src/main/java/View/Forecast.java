package View;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.PictureDrawable;
import android.util.Log;
import android.util.TypedValue;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.View;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.caverock.androidsvg.SVG;
import com.example.weatheria.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import Controller.DateToWeekday;
import Controller.LocationHandler;
import Model.Location;
import Controller.WeatherFetcher;

public class Forecast {

    private WeatherFetcher weatherFetcher;
    private DateToWeekday dateToWeekday;
    private Location location;
    private LocationHandler locationHandler;
    private Context context;
    private LayoutManager layoutManager;
    private int childCount = 0;
    private boolean isFahrenheit = false;
    private JSONArray todaysWeather;
    private JSONArray tomorrowsWeather;
    private JSONArray threeDaysWeather;
    private List<ImageView> svgList; //0=clear; 1=cloudy; 2=drizzle; 3=mist; 4=rain; 5=snow; 6=thunderstorm

    public Forecast(Context context, LayoutManager layoutManager, Location location){
        this.context = context;
        this.layoutManager = layoutManager;
        this.weatherFetcher = new WeatherFetcher();
        this.dateToWeekday = new DateToWeekday();
        this.locationHandler = new LocationHandler(context);
        this.location = location;

        this.todaysWeather = weatherFetcher.getTodaysWeather(this.location.getLatitude(), this.location.getLongitude());
        this.tomorrowsWeather = weatherFetcher.getTomorrowsWeather(this.location.getLatitude(), this.location.getLongitude());
        this.threeDaysWeather = weatherFetcher.getThreeDaysWeather(this.location.getLatitude(), this.location.getLongitude());

        svgList = loadSvg();
    }

    public List<View> getForecastView(){
        List<View> forecastView = new ArrayList<>();
        forecastView.add(createBackBtn());
        forecastView.add(createFahrenheitBtn());

        for (View v : createCurrentWeather())
            forecastView.add(v);

        for (View v : createChoiceBtns())
            forecastView.add(v);

        forecastView.add(getDailyForecast());
        return forecastView;
    }

    private Button createBackBtn(){
        Button backBtn = new Button(context);
        backBtn.setText("zurück");

        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width = 0;
        params.height = 0;
        params.rowSpec = GridLayout.spec(0, 7, 1f);
        params.columnSpec = GridLayout.spec(24, 10, 1f);
        params.setMargins(0,0,0,0);
        backBtn.setLayoutParams(params);

        backBtn.setOnClickListener(view -> {
            layoutManager.goBack(childCount);
        });
        childCount++;
        return backBtn;
    }

    private Button createFahrenheitBtn(){
        Button fahrenheitBtn = new Button(context);
        fahrenheitBtn.setText("Fahrenheit");

        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width = 0;
        params.height = 0;
        params.rowSpec = GridLayout.spec(0, 7, 1f);
        params.columnSpec = GridLayout.spec(0, 11, 1f);
        params.setMargins(0,0,0,0);
        fahrenheitBtn.setLayoutParams(params);

        fahrenheitBtn.setOnClickListener(view -> {
            if (!isFahrenheit) {
                fahrenheitBtn.setText("Celsius");
                isFahrenheit = true;
            } else {
                fahrenheitBtn.setText("Fahrenheit");
                isFahrenheit = false;
            }
        });
        childCount++;
        return fahrenheitBtn;
    }

    private double kelvinToCelsius(double kelvin){
        double celsius = kelvin -273.15;
        double modifier = Math.pow(10, 2);
        return Math.floor(celsius * modifier) / modifier;
    }

    private double kelvingToFahrenheit(double kelvin){
        return kelvinToCelsius(kelvin) * (9/5) +32;
    }

    private List<View> createCurrentWeather(){
        // eine methode da für alles andere api genutzt wird
        JSONObject currentWeather = weatherFetcher.getCurrentWeather(location.getLatitude(), location.getLongitude());
        List<View> toReturn = new ArrayList<>();

        // Name des angezeigten Ortes
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
        childCount++;
        toReturn.add(locationName);

        //Temperatur am Ort
        TextView locationTemp = new TextView(context);
        try {
            double kelvin = currentWeather.getJSONObject("main").getDouble("temp");
            if (isFahrenheit == false)
                locationTemp.setText(Double.toString(kelvinToCelsius(kelvin)) + " °C");
            else
                locationTemp.setText(Double.toString(kelvingToFahrenheit(kelvin)) + " °F");
        } catch (JSONException e){
            Log.e("JSONError", "fetching temperature", e);
            locationTemp.setText("----");
        }
        GridLayout.LayoutParams paramsTemp = new GridLayout.LayoutParams();
        paramsTemp.width = 0;
        paramsTemp.height = 0;
        paramsTemp.rowSpec = GridLayout.spec(6, 5, 1f);
        paramsTemp.columnSpec = GridLayout.spec(15, 6, 1f);
        paramsTemp.setMargins(0,0,0,0);
        locationTemp.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        locationTemp.setTextColor(Color.BLACK);
        locationTemp.setLayoutParams(paramsTemp);
        childCount++;
        toReturn.add(locationTemp);

        // passendes icon zum wetter am ort
        ImageView weatherIcon = chooseIcon(currentWeather);
        GridLayout.LayoutParams paramsIcon = new GridLayout.LayoutParams();
        paramsIcon.width = 150;
        paramsIcon.height = 150;
        paramsIcon.rowSpec = GridLayout.spec(11, 2, 1f);
        paramsIcon.columnSpec = GridLayout.spec(15, 2, 1f);
        paramsIcon.setMargins(0,0,0,0);
        weatherIcon.setLayoutParams(paramsIcon);

        toReturn.add(weatherIcon);
        childCount++;
        return toReturn;
    }

    private List<ImageView> loadSvg(){
        List<ImageView> svgList = new ArrayList<>();

        // grafiken als int laden
        int[] svgRessources = new int[]{R.raw.clearsvg, R.raw.cloudysvg,
                R.raw.drizzlesvg, R.raw.mistsvg, R.raw.rainsvg, R.raw.snowsvg, R.raw.thunderstormsvg};
        for (int ressource : svgRessources){
            try{
                // int Grafiken zu Grafiken machen
                SVG svg = SVG.getFromResource(context, ressource);
                ImageView svgView = new ImageView(context);
                svgView.setImageDrawable(new PictureDrawable(svg.renderToPicture()));
                svgList.add(svgView);
            } catch (Exception e){
                Log.e("SVGError", String.valueOf(ressource), e);
            }
        }
        return svgList;
    }

    private ImageView chooseIcon(JSONObject currentWeather){
        try{
            JSONArray weatherArray = currentWeather.getJSONArray("weather");
            JSONObject currentWeatherObject = weatherArray.getJSONObject(0);
            int id = currentWeatherObject.getInt("id");
            if (id > 199 && id < 300){
                return svgList.get(6);
            } else if (id > 299 && id < 400)
                return svgList.get(2);
            else if (id > 499 && id < 600)
                return svgList.get(4);
            else if (id > 599 && id < 700)
                return svgList.get(5);
            else if (id > 699 && id < 800)
                return svgList.get(3);
            else if (id == 800)
                return svgList.get(0);
            else if (id > 800 && id < 900)
                return svgList.get(1);
        } catch(JSONException e){
            Log.e("JSONError", "forecast.chooseIcon", e);
        }
        return null;
    }

    private List<Button> createChoiceBtns(){
        List<Button> btnList = new ArrayList<>();
        btnList.add(createTodayBtn());
        btnList.add(createTomorrowBtn());
        btnList.add(create3DaysBtn());
        return btnList;
    }

    private Button createTodayBtn(){
        Button todayBtn = new Button(context);
        todayBtn.setText("heute");

        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width = 0;
        params.height = 0;
        params.rowSpec = GridLayout.spec(18, 7, 1f);
        params.columnSpec = GridLayout.spec(1, 10, 1f);
        params.setMargins(0,0,0,0);
        todayBtn.setLayoutParams(params);
        childCount++;
        return todayBtn;
    }

    private Button createTomorrowBtn(){
        Button tomorrowBtn = new Button(context);
        tomorrowBtn.setText("morgen");

        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width = 0;
        params.height = 0;
        params.rowSpec = GridLayout.spec(18, 7, 1f);
        params.columnSpec = GridLayout.spec(12, 10, 1f);
        params.setMargins(0,0,0,0);
        tomorrowBtn.setLayoutParams(params);
        childCount++;
        return tomorrowBtn;
    }

    private Button create3DaysBtn(){
        Button threeDaysBtn = new Button(context);
        threeDaysBtn.setText("3 Tage");

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

    private View getDailyForecast(){
        RecyclerView dailyForecast = new RecyclerView(context);
        DataExtractor extractor = new DataExtractor();

        // form der recyclerview definieren und füllen
        int spanCount = 3;
        int spacing = 50;
        dailyForecast.setLayoutManager(new GridLayoutManager(context, spanCount));
        dailyForecast.addItemDecoration(new GridSpacingItemDecoration(context, spanCount, spacing, true));
        MyAdapter adapter = new MyAdapter(extractor.extractData(weatherFetcher.getTodaysWeather(location.getLongitude(), location.getLongitude())));
        dailyForecast.setAdapter(adapter);

        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width = GridLayout.LayoutParams.WRAP_CONTENT; // Or specific size in pixels
        params.height = GridLayout.LayoutParams.WRAP_CONTENT; // Or specific size in pixels

// Assuming the GridLayout has defined column and row counts
        params.rowSpec = GridLayout.spec(40, 30); // Adjust rowIndex and rowSpan as needed
        params.columnSpec = GridLayout.spec(0, 34); // Adjust columnIndex and columnSpan as needed

        dailyForecast.setLayoutParams(params);
        return dailyForecast;
    }
}
