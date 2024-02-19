package View;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.View;
import android.widget.GridLayout;
import android.widget.TextView;

import com.example.weatheria.R;

import java.io.File;
import java.util.List;

import Controller.FileWriterReader;
import Controller.Forecast;
import Controller.LocationHandler;
import Model.Location;

/**
 * Manages the layout and dynamic content of an application's main view.
 * This class is responsible for updating the GridLayout based on user interactions,
 * such as searching, selecting locations from a database or list, and displaying weather forecasts.
 * It supports navigating back to the search window, handling initial application start logic,
 * and managing views based on search results.
 */
public class LayoutManager {
    final private GridLayout gridLayout;
    final private LocationHandler locationHandler;
    private List<Location> matchingLocations;
    final private SearchWindow searchWindow;

    /**
     * Constructs a new LayoutManager with the specified GridLayout.
     * Initializes the search window and location handler for the application.
     * @param gridLayout The GridLayout to be managed.
     */
    public LayoutManager(GridLayout gridLayout) {
        this.gridLayout = gridLayout;
        this.searchWindow = new SearchWindow(this, gridLayout.getContext());
        this.locationHandler = new LocationHandler(gridLayout.getContext());
    }

    /**
     * Handles the initial start logic of the application.
     * Checks if the application has been launched previously and loads last search data if available.
     * Otherwise, it displays the search window to the user.
     */
    public void initialStart() {
        File lastSearch = new File(gridLayout.getContext().getFilesDir(), "weatherData.txt");
        initialFillLayout();
        // prüft ob programm schon mal ausgeführt wurde
        // wenn ja werden alte daten geladen
        if (lastSearch.exists()) {
            FileWriterReader fileWriterReader = new FileWriterReader(gridLayout.getContext());
            Location lastLocation = locationHandler.getEntry(fileWriterReader.readExactName());
            Forecast forecast = new Forecast(gridLayout.getContext(), this, lastLocation);
            updateLayout(forecast.getForecastView(), -1);
        } else {

            this.searchWindow.createSearchWindow();
        }
    }

    /**
     * Navigates back to the search window from a current view.
     * Removes the last set of children views added to the layout before showing the search window again.
     * @param childCount The number of child views to remove from the layout.
     */
    public void goBack(int childCount) {
        removeLastChildren(childCount);
        this.searchWindow.createSearchWindow();
    }

    /**
     * Updates the GridLayout with a new set of views.
     * Removes the specified number of child views before adding the new ones.
     * @param newViews A list of new views to be added to the GridLayout.
     * @param childCount The number of child views to remove from the layout before adding the new ones.
     */
    public void updateLayout(List<View> newViews, int childCount) {
        removeLastChildren(childCount);
        for (View v : newViews)
            this.gridLayout.addView(v);
    }

    /**
     * Initiates a search based on the user's input.
     * Determines the appropriate action based on the number of matching locations found.
     * @param toSearch The user's search query.
     * @param childCount The number of child views to remove from the layout.
     * @param context The application context.
     * @return A TextView indicating "No Results" if no locations match the search query, null otherwise.
     */
    public TextView searchInitiated(String toSearch, int childCount, Context context) {
        // liste der orte laden die zum eingegebenen namen passen
        matchingLocations = locationHandler.addLocationViaText(toSearch);
        if (matchingLocations.size() == 1) {
            Forecast forecast = new Forecast(gridLayout.getContext(), this, matchingLocations.get(0));
            updateLayout(forecast.getForecastView(), childCount);
        } else if (matchingLocations.size() > 1) {
            ChooseFromMultiple chooseFromMultiple = new ChooseFromMultiple(matchingLocations, this, gridLayout.getContext());
            List<View> chooseLoc = chooseFromMultiple.createChooseLoc();
            updateLayout(chooseLoc, childCount);
        } else if (matchingLocations.size() == 0) {
            TextView noResult = new TextView(context);
            noResult.setTextColor(Color.parseColor("#dc071b"));
            noResult.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
            noResult.setText(context.getString(R.string.keinergebnis));

            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.rowSpec = GridLayout.spec(10, 4, 1f);
            params.columnSpec = GridLayout.spec(12, 10, 1f);
            noResult.setLayoutParams(params);
            return noResult;
        }
        return null;
    }

    /**
     * Fills the GridLayout with transparent views to maintain structure.
     * This method is called during the initial setup of the application's main layout.
     */
    public void initialFillLayout() {
        for (int row = 0; row < 100; row++) {
            for (int col = 0; col < 34; col++) {
                View view = new View(gridLayout.getContext());
                // layout mit durchsichtigen blöcken füllen
                int color = Color.argb(0, 0, 0, 0);
                view.setBackgroundColor(color);

                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.width = 0;
                params.height = 0;
                params.rowSpec = GridLayout.spec(row, 1f);
                params.columnSpec = GridLayout.spec(col, 1f);
                params.setMargins(0, 0, 0, 0);

                view.setLayoutParams(params);
                gridLayout.addView(view);
            }
        }
    }

    /**
     * Handles the user's choice of location from a list of matching locations.
     * Updates the layout to display the weather forecast for the selected location.
     * @param chosenLocation The user-selected location name.
     * @param childCount The number of child views to remove from the layout.
     */
    public void choiceMade(String chosenLocation, int childCount) {
        for (Location loc : matchingLocations) {
            if (loc.getExactName().equals(chosenLocation)) {
                locationHandler.addLocationToDb(loc);
                Forecast forecast = new Forecast(gridLayout.getContext(), this, loc);
                updateLayout(forecast.getForecastView(), childCount);
                break;
            }
        }
    }

    /**
     * Handles the selection of a location from the database.
     * Updates the layout to display the weather forecast for the selected location.
     * @param exactNamen The exact name of the location as stored in the database.
     * @param childCount The number of child views to remove from the layout.
     */
    public void chosenFromDb(String exactNamen, int childCount) {
        // erzeugt Location Objekt mit Daten aus DB
        Location loc = locationHandler.getEntry(exactNamen);
        Forecast forecast = new Forecast(gridLayout.getContext(), this, loc);
        updateLayout(forecast.getForecastView(), childCount);
    }

    /**
     * Removes the specified number of last-added child views from the GridLayout.
     * This method is used to clear the layout before updating it with new views.
     * @param toRemove The number of child views to remove.
     */
    public void removeLastChildren(int toRemove) {
        // getChildCount ändert sich wenn views entfernt werden
        int currentChildCount = gridLayout.getChildCount();
        for (int lastChild = 0; lastChild < toRemove; lastChild++) {
            gridLayout.removeViewAt(currentChildCount - 1 - lastChild);
        }
    }
}