package View;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.view.View;

import com.example.weatheria.R;

import java.util.ArrayList;
import java.util.List;

import Model.Location;

/**
 * Provides functionality to display a selection window for choosing from multiple locations.
 * This class generates a dynamic view that lists multiple location options retrieved from a search query,
 * allowing the user to select their desired location. It integrates with a {@link LayoutManager} to manage
 * the application's layout updates based on user interaction.
 */
public class ChooseFromMultiple {
    private final List<Location> locationList;
    private final LayoutManager layoutManager;
    private final Context context;
    private final String[] arrayExactNames;
    private final int countLocations;
    private int childCount = 0;

    /**
     * Initializes a new instance of ChooseFromMultiple with a list of locations, a layout manager, and an application context.
     *
     * @param locationList A list of {@link Location} objects representing the search results.
     * @param layoutManager The {@link LayoutManager} responsible for updating the application's layout.
     * @param context The application context used for creating view elements.
     */

    public ChooseFromMultiple(List<Location> locationList, LayoutManager layoutManager, Context context){
        this.locationList = locationList;
        this.layoutManager = layoutManager;
        this.context = context;
        countLocations = locationList.size();
        this.arrayExactNames = new String[countLocations];
    }

    /**
     * Creates and returns a list of views for the location selection window.
     *
     * @return A list of views including a title text, a table (list) of location names, and a back button.
     */
    public List<View> createChooseLoc(){
        List<View> newViews = new ArrayList<>();
        newViews.add(createText());
        newViews.add(createTable());
        newViews.add(createBackBtn());
        return newViews;
    }

    /**
     * Creates a ListView to display the location names for selection.
     *
     * @return A {@link ListView} configured with the names of the locations.
     */
    private View createTable(){
        parseLocListToString();
        ArrayAdapter<String> data = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, arrayExactNames);
        ListView table = new ListView(context);
        table.setAdapter(data);

        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width = 0;
        params.height = 0;
        params.rowSpec = GridLayout.spec(20, 60, 1f);
        params.columnSpec = GridLayout.spec(3, 26, 1f);
        params.setMargins(0,0,0,0);
        table.setLayoutParams(params);

        table.setOnItemClickListener((parent, view, position, id) -> {
            layoutManager.choiceMade(arrayExactNames[position], childCount);
        });

        childCount++;
        return table;
    }

    /**
     * Parses the list of {@link Location} objects into an array of location names.
     * This method populates {@code arrayExactNames} with the exact names of the locations for display.
     */
    private void parseLocListToString(){
        for (int i=0; i<countLocations; i++){
            arrayExactNames[i] = locationList.get(i).getExactName();
        }
    }

    /**
     * Creates a TextView displaying a prompt for the user to select a location.
     *
     * @return A {@link TextView} configured to display the selection prompt.
     */
    private TextView createText(){
        TextView text = new TextView(context);
        text.setText(context.getString(R.string.auswahl));
        // sp skaliert mit bildschirmgröße
        text.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
        text.setTextColor(Color.BLACK);

        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width = GridLayout.LayoutParams.WRAP_CONTENT;
        params.height = GridLayout.LayoutParams.WRAP_CONTENT;
        params.rowSpec = GridLayout.spec(7, 4, 1f);
        params.columnSpec = GridLayout.spec(7, 20, 1f);
        params.setMargins(0,0,0,0);
        text.setLayoutParams(params);

        childCount++;
        return text;
    }

    /**
     * Creates a back button for navigation.
     *
     * @return A {@link Button} configured for returning to the previous screen.
     */
    private Button createBackBtn(){
        Button backBtn = new Button(context);
        backBtn.setText(context.getString(R.string.zurueck));

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
}
