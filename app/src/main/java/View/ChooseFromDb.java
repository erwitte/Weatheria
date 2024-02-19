package View;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.example.weatheria.R;

import java.util.ArrayList;
import java.util.List;

import Controller.LocationHandler;

/**
 * Provides functionality to display and interact with a list of locations stored in the database.
 * This class generates a dynamic view listing the saved locations, allowing the user to select a location for more details
 * or to delete a location from the database via long press.
 */
public class ChooseFromDb {

    private final LayoutManager layoutManager;
    private final Context context;
    private final LocationHandler locationHandler;
    private int childCount = 0;

    /**
     * Initializes a new instance of ChooseFromDb with a layout manager and application context.
     *
     * @param layoutManager The {@link LayoutManager} responsible for updating the application's layout.
     * @param context The application context used for creating view elements.
     */
    public ChooseFromDb(LayoutManager layoutManager, Context context){
        this.context = context;
        this.layoutManager = layoutManager;
        this.locationHandler = new LocationHandler(context);
    }

    /**
     * Creates and returns a list of views for displaying the database locations window.
     *
     * @return A list of views including a title text, a database locations list, an info text, and a back button.
     */
    public List<View> getDbView(){
        List<View> dbViewList = new ArrayList<>();
        dbViewList.add(createText());
        dbViewList.add(createBackBtn());
        dbViewList.add(createInfoText());
        dbViewList.add(createTable());
        return dbViewList;
    }

    /**
     * Creates a ListView to display the names of the locations stored in the database.
     *
     * @return A {@link ListView} configured with the names of the saved locations.
     */
    private View createTable(){
        ArrayAdapter<String> data = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1,
                locationHandler.getDbEntries());
        ListView table = new ListView(context);
        table.setAdapter(data);

        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width = 0;
        params.height = 0;
        params.rowSpec = GridLayout.spec(20, 60, 1f);
        params.columnSpec = GridLayout.spec(3, 26, 1f);
        params.setMargins(0,0,0,0);
        table.setLayoutParams(params);

        childCount++;
        createListeners(table);
        return table;
    }

    /**
     * Creates a TextView displaying a prompt or title for the database view.
     *
     * @return A {@link TextView} configured to display the database title.
     */
    private TextView createText(){
        TextView text = new TextView(context);
        text.setText(context.getString(R.string.datenbank));
        // sp skaliert mit bildschirmgröße
        text.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
        text.setTextColor(Color.BLACK);

        // festlegen der parameter im grid layout
        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width = GridLayout.LayoutParams.WRAP_CONTENT;
        params.height = GridLayout.LayoutParams.WRAP_CONTENT;
        params.rowSpec = GridLayout.spec(7, 4, 1f);
        params.columnSpec = GridLayout.spec(11, 10, 1f);
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
     * Creates a TextView providing additional information or instructions to the user.
     *
     * @return A {@link TextView} displaying additional info or instructions.
     */
    private TextView createInfoText(){
        TextView infoText = new TextView(context);
        infoText.setText(context.getString(R.string.loeschen));
        // sp skaliert mit bildschirmgröße
        infoText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        infoText.setTextColor(Color.BLACK);

        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width = 0;
        params.height = 0;
        params.rowSpec = GridLayout.spec(85, 10, 1f);
        params.columnSpec = GridLayout.spec(5, 22, 1f);
        params.setMargins(0,0,0,0);
        infoText.setLayoutParams(params);

        childCount++;
        return infoText;
    }

    /**
     * Attaches item click listeners to the table for selecting and deleting database entries.
     *
     * @param table The {@link ListView} to which listeners are attached.
     */
    private void createListeners(ListView table){
        table.setOnItemClickListener((parent, view, position, id) -> {
            layoutManager.chosenFromDb(locationHandler.getDbEntries().get(position), childCount);
        });

        table.setOnItemLongClickListener((parent, view, position, id) -> {
            new AlertDialog.Builder(context)
                    .setTitle(context.getString(R.string.nurloeschen))
                    .setMessage(locationHandler.getDbEntries().get(position) + " "
                            + context.getString(R.string.sicherloeschen))

                    .setPositiveButton(android.R.string.yes, (dialog, which) ->
                            locationHandler.deleteDbEntry(locationHandler.getDbEntries().get(position)))
                    .setNegativeButton(android.R.string.no, null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
            return true;
        });
    }
}
