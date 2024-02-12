package View;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.util.TypedValue;
import android.widget.ArrayAdapter;
import android.widget.GridLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import Model.Location;

public class ChooseFromMultiple {
    private List<Location> locationList;
    private LayoutManager layoutManager;
    private Context context;
    private String[] arrayExactNames;
    private int countLocations;

    public ChooseFromMultiple(List<Location> locationList, LayoutManager layoutManager, Context context){
        this.locationList = locationList;
        this.layoutManager = layoutManager;
        this.context = context;
        countLocations = locationList.size();
        this.arrayExactNames = new String[countLocations];
    }

    public List<View> createChooseLoc(){
        List<View> newViews = new ArrayList<>();
        newViews.add(createText());;
        newViews.add(createTable());
        return newViews;
    }

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
            layoutManager.choiceMade(arrayExactNames[position]);
        });

        return table;
    }

    private void parseLocListToString(){
        for (int i=0; i<countLocations; i++){
            arrayExactNames[i] = locationList.get(i).getExactName();
        }
    }

    private TextView createText(){
        TextView text = new TextView(context);
        text.setText("Bitte auswählen");
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
        return text;
    }
}
