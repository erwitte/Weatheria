package View;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;

import java.util.ArrayList;
import java.util.List;


public class SearchWindow {
    private LayoutManager layoutManager;
    //opacity of .8
    private String hexColor = "#99FFFFFF";
    private List<View>  viewList;
    private Context context;
    private ChooseFromDb chooseFromDb;
    private int childCount = 0;
    public SearchWindow(LayoutManager layoutManager, Context context){
        this.layoutManager = layoutManager;
        this.viewList = new ArrayList<>();
        this.context = context;
        this.chooseFromDb = new ChooseFromDb(layoutManager, context);
    }

    public void createSearchWindow(){
        viewList.add(createSearchField());
        viewList.add(createSearchBtn());
        viewList.add(createDbBtn());
        viewList.add(createGpsBtn());
        layoutManager.updateLayout(viewList, -1);
    }

    private EditText createSearchField(){
        EditText searchField = new EditText(context);
        searchField.setBackgroundColor(Color.parseColor(hexColor));
        searchField.setId(View.generateViewId());

        GridLayout.LayoutParams paramsEditText = new GridLayout.LayoutParams();
        paramsEditText.width = 0; // Use GridLayout.LayoutParams.MATCH_PARENT if you want fixed sizes instead of weights
        paramsEditText.height = 0;
        paramsEditText.rowSpec = GridLayout.spec(20, 5, 1f);
        paramsEditText.columnSpec = GridLayout.spec(6, 20, 1f);
        paramsEditText.setMargins(0, 0, 0, 0);

        searchField.setLayoutParams(paramsEditText);

        childCount++;
        return searchField;
    }

    private Button createSearchBtn(){
        Button searchBtn = new Button(context);
        searchBtn.setText("suchen");

        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width = 0;
        params.height = 0;
        params.rowSpec = GridLayout.spec(27, 7, 1f);
        params.columnSpec = GridLayout.spec(12, 8, 1f);
        params.setMargins(0,0,0,0);
        searchBtn.setLayoutParams(params);

        searchBtn.setOnClickListener(view -> {
            EditText edit = (EditText) viewList.get(0);
            String input = edit.getText().toString();
            edit.getText().clear();
            Log.i("edit", input);
            viewList.clear();
            layoutManager.searchInitiated(input, childCount);
            childCount = 0;
        });

        childCount++;
        return searchBtn;
    }

    private Button createDbBtn(){
        Button dbBtn = new Button(context);
        dbBtn.setText("Datenbank");

        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width = 0;
        params.height = 0;
        params.rowSpec = GridLayout.spec(70, 7, 1f);
        params.columnSpec = GridLayout.spec(3, 10, 1f);
        params.setMargins(0,0,0,0);
        dbBtn.setLayoutParams(params);

        dbBtn.setOnClickListener(view -> {
            layoutManager.updateLayout(chooseFromDb.getDbView(), childCount);
        });

        childCount++;
        return dbBtn;
    }

    private Button createGpsBtn(){
        Button gpsBtn = new Button(context);
        gpsBtn.setText("GPS");

        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width = 0;
        params.height = 0;
        params.rowSpec = GridLayout.spec(70, 7, 1f);
        params.columnSpec = GridLayout.spec(20, 10, 1f);
        params.setMargins(0,0,0,0);
        gpsBtn.setLayoutParams(params);

        childCount++;
        return gpsBtn;
    }
}
