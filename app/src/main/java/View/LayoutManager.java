package View;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.GridLayout;
import android.widget.TextView;

import com.example.weatheria.R;

import java.util.List;

import Controller.LocationHandler;
import Model.Location;

public class LayoutManager{
    final private GridLayout gridLayout;
    final private LocationHandler locationHandler;
    private List<Location> matchingLocations;
    final private SearchWindow searchWindow;

    public LayoutManager(GridLayout gridLayout){
        this.gridLayout = gridLayout;
        this.searchWindow = new SearchWindow(this, gridLayout.getContext());
        this.locationHandler = new LocationHandler(gridLayout.getContext());
    }

    public void initialStart(){
        initialFillLayout();
        this.searchWindow.createSearchWindow();
    }

    public void goBack(int childCount){
        removeLastChildren(childCount);
        this.searchWindow.createSearchWindow();
    }

    public void updateLayout(List<View> newViews, int childCount) {
        removeLastChildren(childCount);
        for(View v : newViews)
            this.gridLayout.addView(v);
    }

    public TextView searchInitiated(String toSearch, int childCount, Context context) {
        matchingLocations = locationHandler.addLocationViaText(toSearch);
        if (matchingLocations.size() == 1){
            Forecast forecast = new Forecast(gridLayout.getContext(), this, matchingLocations.get(0));
            updateLayout(forecast.getForecastView(), childCount);
        } else if (matchingLocations.size() > 1){
            ChooseFromMultiple chooseFromMultiple = new ChooseFromMultiple(matchingLocations, this, gridLayout.getContext());
            List<View> chooseLoc = chooseFromMultiple.createChooseLoc();
            updateLayout(chooseLoc, childCount);
        } else if (matchingLocations.size() == 0){
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

    public void initialFillLayout(){
        for (int row = 0; row < 100; row++) {
            for (int col = 0; col < 34; col++) {
                View view = new View(gridLayout.getContext());
                // layout mit durchsichtigen blöcken füllen
                int color = Color.argb(0, 0,0,0);
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

    public void choiceMade(String chosenLocation, int childCount){
        for (Location loc : matchingLocations){
            if (loc.getExactName().equals(chosenLocation)){
                locationHandler.addLocationToDb(loc);
                Forecast forecast = new Forecast(gridLayout.getContext(), this, loc);
                updateLayout(forecast.getForecastView(), childCount);
                break;
            }
        }
    }

    public void chosenFromDb(String exactNamen, int childCount){
        Location loc = locationHandler.getEntry(exactNamen);
        Forecast forecast = new Forecast(gridLayout.getContext(), this, loc);
        updateLayout(forecast.getForecastView(), childCount);
    }

    private void removeLastChildren(int toRemove){
        // getChildCount ändert sich wenn views entfernt werden
        int currentChildCount = gridLayout.getChildCount();
        for (int lastChild=0; lastChild<toRemove; lastChild++){
            View t = gridLayout.getChildAt(gridLayout.getChildCount() - 1 - lastChild);
            gridLayout.removeViewAt(currentChildCount - 1 - lastChild);
        }
    }

    public void removeLast(){
        gridLayout.removeViewAt(gridLayout.getChildCount() - 1);
    }
}
