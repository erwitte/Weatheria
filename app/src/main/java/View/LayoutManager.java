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

    public void searchInitiated(String toSearch, int childCount) {
        matchingLocations = locationHandler.addLocationViaText(toSearch);
        if (matchingLocations.size() == 1){
            Log.i("jaf", matchingLocations.get(0).getExactName());
        } else if (matchingLocations.size() > 1){
            ChooseFromMultiple chooseFromMultiple = new ChooseFromMultiple(matchingLocations, this, gridLayout.getContext());
            List<View> chooseLoc = chooseFromMultiple.createChooseLoc();
            updateLayout(chooseLoc, childCount);
        }
    }

    public void initialFillLayout(){
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

    private void removeLastChildren(int toRemove){
        // getChildCount ändert sich wenn views entfernt werden
        int currentChildCount = gridLayout.getChildCount();
        for (int lastChild=0; lastChild<toRemove; lastChild++){
            View t = gridLayout.getChildAt(gridLayout.getChildCount() - 1 - lastChild);
            gridLayout.removeViewAt(currentChildCount - 1 - lastChild);
        }
    }
}
