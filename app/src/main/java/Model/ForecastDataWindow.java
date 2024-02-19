package Model;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Constructs and organizes a series of views to display forecast data in a window-like layout.
 * This class creates a dynamic layout for displaying weather forecasts, including temperature texts,
 * weather icons, weekdays, and date-time information, based on a list of input strings.
 */
public class ForecastDataWindow {
    private final List<String> input;
    private final Context context;
    private final IconChooser iconChooser;
    private int index;
    private int row;
    private int column;
    private final int spacing;

    /**
     * Initializes a new instance of ForecastDataWindow with the specified input and context.
     * @param input A list of strings containing the forecast data to be displayed.
     * @param context The application context used for creating views.
     */
    public ForecastDataWindow(List<String> input, Context context){
        this.context = context;
        this.iconChooser = new IconChooser(context);
        this.input = input;
        this.index = 0;
        this.row = 27;
        this.column = 1;
        this.spacing = 8;
    }

    /**
     * Creates a collection of views based on the input data for display in the forecast window.
     * @return A list of views containing the forecast data arranged for display.
     */
    public List<View> createDataWindow(){
        List<View> createdViews = new ArrayList<>();
        int inputSize = input.size();
        while(index < inputSize) {
            createdViews.add(createTemperatureText());
            createdViews.add(createWeatherIcon());
            createdViews.add(createWeekday());
            createdViews.add(createDateTime());
        }

        return createdViews;
    }

    /**
     * Creates a TextView for displaying a temperature value.
     * @return A TextView configured with layout parameters and styled for displaying temperature.
     */
    private TextView createTemperatureText(){
        TextView temperatureText = new TextView(context);
        temperatureText.setText(input.get(index));
        index++;

        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width = 0;
        params.height = 0;
        params.rowSpec = GridLayout.spec(row, 7, 1f);
        params.columnSpec = GridLayout.spec(column, 8, 1f);
        params.setMargins(0,0,0,0);
        temperatureText.setLayoutParams(params);

        temperatureText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        temperatureText.setTextColor(Color.BLACK);

        column = column + spacing;
        return temperatureText;
    }

    /**
     * Creates an ImageView for displaying a weather icon.
     * @return An ImageView configured with layout parameters for displaying a weather icon.
     */
    private ImageView createWeatherIcon(){
        ImageView weatherIcon = iconChooser.chooseIcon(Integer.parseInt(input.get(index)));
        index++;

        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width = 0;
        params.height = 0;
        params.rowSpec = GridLayout.spec(row, 7, 1f);
        params.columnSpec = GridLayout.spec(column, 8, 1f);
        params.setMargins(0,0,0,0);
        weatherIcon.setLayoutParams(params);

        column = column + spacing;
        return weatherIcon;
    }

    /**
     * Creates a TextView for displaying the weekday.
     * @return A TextView configured with layout parameters and styled for displaying the weekday.
     */
    private TextView createWeekday(){
        TextView weekday = new TextView(context);
        weekday.setText(input.get(index));
        index++;

        GridLayout.LayoutParams params= new GridLayout.LayoutParams();
        params.width = 0;
        params.height = 0;
        params.rowSpec = GridLayout.spec(row, 7, 1f);
        params.columnSpec = GridLayout.spec(column, 8, 1f);
        params.setMargins(0,0,0,0);
        weekday.setLayoutParams(params);

        weekday.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        weekday.setTextColor(Color.BLACK);

        column = column + spacing;
        return weekday;
    }

    /**
     * Creates a TextView for displaying the date and time.
     * @return A TextView configured with layout parameters and styled for displaying the date and time.
     */
    private TextView createDateTime(){
        TextView dateTime = new TextView(context);
        dateTime.setText(input.get(index));
        index++;

        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width = 0;
        params.height = 0;
        params.rowSpec = GridLayout.spec(row, 7, 1f);
        params.columnSpec = GridLayout.spec(column, 8, 1f);
        params.setMargins(0,0,0,0);
        dateTime.setLayoutParams(params);

        dateTime.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        dateTime.setTextColor(Color.BLACK);

        column = 1;
        row = row + spacing;
        return  dateTime;
    }
}
