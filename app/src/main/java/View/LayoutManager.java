package View;

import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.GridLayout;

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
        locationHandler = new LocationHandler(gridLayout.getContext());
    }

    public GridLayout getGridLayout(){
        return this.gridLayout;
    }

    public void initialStart(){
        clearLayout();
        this.searchWindow.createSearchWindow();
    }
    public void updateLayout(List<View> newViews) {
        for(View v : newViews)
            this.gridLayout.addView(v);
    }

    public void searchInitiated(String toSearch) {
        matchingLocations = locationHandler.addLocationViaText(toSearch);
        if (matchingLocations.size() == 1){
            Log.i("jaf", matchingLocations.get(0).getExactName());
        } else if (matchingLocations.size() > 1){
            gridLayout.removeAllViews();
            clearLayout();
            ChooseFromMultiple chooseFromMultiple = new ChooseFromMultiple(matchingLocations, this, gridLayout.getContext());
            List<View> chooseLoc = chooseFromMultiple.createChooseLoc();
            for (View v : chooseLoc)
                gridLayout.addView(v);
        }
    }

    public void clearLayout(){
        for (int row = 0; row < 100; row++) {
            for (int col = 0; col < 34; col++) {
                View view = new View(gridLayout.getContext());
                // Generate a random color
                int color = Color.argb(0, 0,0,0);
                view.setBackgroundColor(color);

                // Set layout parameters for the view
                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.width = 0; // Use GridLayout.LayoutParams.MATCH_PARENT if you want fixed sizes instead of weights
                params.height = 0; // Use GridLayout.LayoutParams.MATCH_PARENT if you want fixed sizes instead of weights
                params.rowSpec = GridLayout.spec(row, 1f);
                params.columnSpec = GridLayout.spec(col, 1f);
                params.setMargins(0, 0, 0, 0); // Set margins to create gaps between cells

                view.setLayoutParams(params);
                 // Add the view to the GridLayout
                gridLayout.addView(view);
            }
        }
    }

    public void choiceMade(String chosenLocation){
        for (Location loc : matchingLocations){
            if (loc.getExactName().equals(chosenLocation)){
                locationHandler.addLocationToDb(loc);
                // todo wetter ding hier
                break;
            }
        }
    }
}
