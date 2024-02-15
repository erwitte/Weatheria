package View;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PictureDrawable;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
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
import Controller.FileWriterReader;
import Model.Location;
import Controller.WeatherFetcher;

public class Forecast {

    private final WeatherFetcher weatherFetcher;
    private final Location location;
    private Context context;
    private LayoutManager layoutManager;
    private int childCount = 0;
    private boolean isFahrenheit = false;
    private JSONArray todaysWeather;
    private JSONArray tomorrowsWeather;
    private JSONArray threeDaysWeather;
    private boolean isViewed;
    private FileWriterReader fileWriterReader;
    private int forecastViewCounter;

    public Forecast(Context context, LayoutManager layoutManager, Location location){
        this.context = context;
        this.layoutManager = layoutManager;
        this.fileWriterReader = new FileWriterReader(context);
        this.weatherFetcher = new WeatherFetcher(context, fileWriterReader);
        this.location = location;
        this.isViewed = false;
        forecastViewCounter = 0;

        fileWriterReader.setExactName(location.getExactName());

        this.todaysWeather = weatherFetcher.getTodaysWeather(this.location.getLatitude(), this.location.getLongitude());
        this.tomorrowsWeather = weatherFetcher.getTomorrowsWeather(this.location.getLatitude(), this.location.getLongitude());
        this.threeDaysWeather = weatherFetcher.getThreeDaysWeather(this.location.getLatitude(), this.location.getLongitude());
    }

    public List<View> getForecastView(){
        List<View> forecastView = new ArrayList<>();
        forecastView.add(createBackBtn());
        forecastView.add(createFahrenheitBtn());

        for (View v : createCurrentWeather())
            forecastView.add(v);

        for (View v : createChoiceBtns())
            forecastView.add(v);

        //forecastView.add(getDailyForecast());
        return forecastView;
    }

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

        backBtn.setOnClickListener(view -> {
            layoutManager.goBack(childCount);
        });
        childCount++;
        return backBtn;
    }

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
                locationTemp.setText(kelvinToCelsius(kelvin) + " °C");
            else
                locationTemp.setText(kelvingToFahrenheit(kelvin) + " °F");
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
        ImageView weatherIcon = extractId(currentWeather);
        GridLayout.LayoutParams paramsIcon = new GridLayout.LayoutParams();
        paramsIcon.width = 150;
        paramsIcon.height = 150;
        paramsIcon.rowSpec = GridLayout.spec(11, 2, 1f);
        paramsIcon.columnSpec = GridLayout.spec(15, 2, 1f);
        paramsIcon.setMargins(0,0,0,0);
        weatherIcon.setLayoutParams(paramsIcon);

        toReturn.add(weatherIcon);
        childCount++;

        TextView humidityWord = new TextView(context);
        TextView humidityPercentage= new TextView(context);
        try {
            // zeigt zahl der Luftfeuchtigkeit + %
            String humidityText = currentWeather.getJSONObject("main").getString("humidity") + "%";
            humidityPercentage.setText(humidityText);
            GridLayout.LayoutParams paramsHumidityPercentage = new GridLayout.LayoutParams();
            paramsHumidityPercentage.width = 0;
            paramsHumidityPercentage.height = 0;
            paramsHumidityPercentage.rowSpec = GridLayout.spec(8, 4, 1f);
            paramsHumidityPercentage.columnSpec = GridLayout.spec(22, 4, 1f);
            paramsHumidityPercentage.setMargins(0,0,0,0);

            humidityPercentage.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
            humidityPercentage.setTextColor(Color.BLACK);
            humidityPercentage.setLayoutParams(paramsHumidityPercentage);
            toReturn.add(humidityPercentage);
            childCount++;

            // zeigt wort Feuchtigkeit / humidity
            humidityWord.setText(context.getString(R.string.feuchtigkeit));
            GridLayout.LayoutParams paramsHumidityWord = new GridLayout.LayoutParams();
            paramsHumidityWord.width = 0;
            paramsHumidityWord.height = 0;
            paramsHumidityWord.rowSpec = GridLayout.spec(12, 4, 1f);
            paramsHumidityWord.columnSpec = GridLayout.spec(20, 14, 1f);
            paramsHumidityWord.setMargins(0,0,0,0);

            humidityWord.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
            humidityWord.setTextColor(Color.BLACK);
            humidityWord.setLayoutParams(paramsHumidityWord);
            toReturn.add(humidityWord);
            childCount++;
        } catch (JSONException e){
            Log.e("JSONError", "fetching humidity", e);
        }
        return toReturn;
    }

    private ImageView extractId(JSONObject currentWeather){
        try{
            JSONArray weatherArray = currentWeather.getJSONArray("weather");
            JSONObject currentWeatherObject = weatherArray.getJSONObject(0);
            int id = currentWeatherObject.getInt("id");
            return chooseIcon(id);
        } catch(JSONException e){
            Log.e("JSONError", "forecast.chooseIcon", e);
        }
        return null;
    }

    private ImageView chooseIcon(int id){
        ImageView icon = new ImageView(context);
        int resource = 0;
        if (id > 199 && id < 300){
            resource = R.raw.thunderstormsvg;
        } else if (id > 299 && id < 400) {
            resource = R.raw.drizzlesvg;
        } else if (id > 499 && id < 600) {
            resource = R.raw.rainsvg;
        } else if (id > 599 && id < 700) {
            resource = R.raw.snowsvg;
        } else if (id > 699 && id < 800) {
            resource = R.raw.mistsvg;
        } else if (id == 800) {
            resource = R.raw.clearsvg;
        } else if (id > 800 && id < 900) {
            resource = R.raw.cloudysvg;
        }

        icon.setImageDrawable(convertSvgToDrawable(resource));
        return icon;
    }

    private Drawable convertSvgToDrawable(int svgResource) {
        try {
            SVG svg = SVG.getFromResource(context, svgResource);
            return new PictureDrawable(svg.renderToPicture());
        } catch (Exception e) {
            Log.e("SVGError", "Loading SVG resource failed.", e);
            return null;
        }
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

    private List<View> getDailyForecast(){
        DataExtractor extractor = new DataExtractor();
        List<String> forecastData = extractor.extractData(weatherFetcher.getTodaysWeather(location.getLatitude(), location.getLongitude()));
        forecastData = calculateTemp(forecastData);

        return createForecast(forecastData);
    }

    private List<View> getTomorrowsForecast(){
        DataExtractor extractor = new DataExtractor();
        List<String> forecastData = extractor.extractData(weatherFetcher.getTomorrowsWeather(location.getLatitude(), location.getLongitude()));
        forecastData = calculateTemp(forecastData);

        return createForecast(forecastData);
    }

    private List<String> calculateTemp(List<String> temps){
        if (!isFahrenheit) {
            for (int i = 0; i < temps.size(); i = i + 4) {
                temps.set(i, kelvinToCelsius(Double.parseDouble(temps.get(i))) + " °C");
            }
            return temps;
        } else {
            for (int i = 0; i < temps.size(); i = i + 4) {
                temps.set(i, kelvingToFahrenheit(Double.parseDouble(temps.get(i))) + " °F");
            }
            return temps;
        }
    }

    private List<View> createForecast(List<String> input){
        List<View> createdViews = new ArrayList<>();
        int index = 0;
        int row = 27;
        int column = 1;

        while (index < input.size()){
            TextView text1 = new TextView(context);
            text1.setText(input.get(index));
            index++;
            forecastViewCounter++;

            GridLayout.LayoutParams paramsText1 = new GridLayout.LayoutParams();
            paramsText1.width = 0;
            paramsText1.height = 0;
            paramsText1.rowSpec = GridLayout.spec(row, 7, 1f);
            paramsText1.columnSpec = GridLayout.spec(column, 8, 1f);
            paramsText1.setMargins(0,0,0,0);
            text1.setLayoutParams(paramsText1);
            childCount++;
            createdViews.add(text1);
            column = column + 8;

            ImageView icon = chooseIcon(Integer.parseInt(input.get(index)));
            index++;
            forecastViewCounter++;

            GridLayout.LayoutParams image = new GridLayout.LayoutParams();
            image.width = 0;
            image.height = 0;
            image.rowSpec = GridLayout.spec(row, 7, 1f);
            image.columnSpec = GridLayout.spec(column, 8, 1f);
            image.setMargins(0,0,0,0);
            icon.setLayoutParams(image);
            childCount++;
            createdViews.add(icon);
            column = column + 8;

            TextView text2 = new TextView(context);
            text2.setText(input.get(index));
            index++;
            forecastViewCounter++;

            GridLayout.LayoutParams paramsText2 = new GridLayout.LayoutParams();
            paramsText2.width = 0;
            paramsText2.height = 0;
            paramsText2.rowSpec = GridLayout.spec(row, 7, 1f);
            paramsText2.columnSpec = GridLayout.spec(column, 8, 1f);
            paramsText2.setMargins(0,0,0,0);
            text2.setLayoutParams(paramsText2);
            childCount++;
            createdViews.add(text2);

            column = column + 8;

            TextView text3 = new TextView(context);
            text3.setText(input.get(index));
            index++;
            forecastViewCounter++;

            GridLayout.LayoutParams paramsText3 = new GridLayout.LayoutParams();
            paramsText3.width = 0;
            paramsText3.height = 0;
            paramsText3.rowSpec = GridLayout.spec(row, 7, 1f);
            paramsText3.columnSpec = GridLayout.spec(column, 8, 1f);
            paramsText3.setMargins(0,0,0,0);
            text3.setLayoutParams(paramsText3);
            childCount++;
            createdViews.add(text3);

            column = 1;
            row = row + 8;
        }

        return createdViews;
    }
}
