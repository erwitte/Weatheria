package View;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.GridLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import Controller.LocationHandler;

public class ChooseFromDb {

    private LayoutManager layoutManager;
    private Context context;
    private LocationHandler locationHandler;

    public ChooseFromDb(LayoutManager layoutManager, Context context){
        this.context = context;
        this.layoutManager = layoutManager;
        this.locationHandler = new LocationHandler(context);
    }

    public List<View> getDbView(){
        List<View> dbViewList = new ArrayList<>();
        dbViewList.add(createText());
        dbViewList.add(createTable());
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

        table.setOnItemClickListener((parent, view, position, id) -> {
            Log.i("oijesaf", locationHandler.getDbEntries().get(position));
        });

        return table;
    }

    private TextView createText(){
        TextView text = new TextView(context);
        text.setText("Datenbank");
        // sp skaliert mit bildschirmgröße
        text.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
        text.setTextColor(Color.BLACK);

        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width = GridLayout.LayoutParams.WRAP_CONTENT;
        params.height = GridLayout.LayoutParams.WRAP_CONTENT;
        params.rowSpec = GridLayout.spec(7, 4, 1f);
        params.columnSpec = GridLayout.spec(11, 10, 1f);
        params.setMargins(0,0,0,0);
        text.setLayoutParams(params);
        return text;
    }
}
