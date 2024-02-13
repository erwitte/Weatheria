package View;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.example.weatheria.R;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import Controller.LocationHandler;

public class ChooseFromDb {

    private LayoutManager layoutManager;
    private Context context;
    private LocationHandler locationHandler;
    private int childCount = 0;

    public ChooseFromDb(LayoutManager layoutManager, Context context){
        this.context = context;
        this.layoutManager = layoutManager;
        this.locationHandler = new LocationHandler(context);
    }

    public List<View> getDbView(){
        List<View> dbViewList = new ArrayList<>();
        dbViewList.add(createText());
        dbViewList.add(createTable());
        dbViewList.add(createBackBtn());
        dbViewList.add(createInfoText());
        return dbViewList;
    }

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

        backBtn.setOnClickListener(view -> {
            layoutManager.goBack(childCount);
        });
        childCount++;
        return backBtn;
    }

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

    private void createListeners(ListView table){
        table.setOnItemClickListener((parent, view, position, id) -> {
            layoutManager.chosenFromDb(locationHandler.getDbEntries().get(position), childCount);
        });

        table.setOnItemLongClickListener((parent, view, position, id) -> {
            new AlertDialog.Builder(context)
                    .setTitle(context.getString(R.string.nurloeschen))
                    .setMessage(locationHandler.getDbEntries().get(position) + " " + context.getString(R.string.sicherloeschen))
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            locationHandler.deleteDbEntry(locationHandler.getDbEntries().get(position));
                        }
                    })
                    .setNegativeButton(android.R.string.no, null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
            return true;
        });
    }
}
