package View;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.TextView;

import com.example.weatheria.R;

import java.util.ArrayList;
import java.util.List;


public class SearchWindow {
    private final LayoutManager layoutManager;
    private final List<View>  viewList;
    private final Context context;
    private final ChooseFromDb chooseFromDb;
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
        viewList.add(createLanguageBtn());
        layoutManager.updateLayout(viewList, -1);
    }

    private EditText createSearchField(){
        EditText searchField = new EditText(context);
        searchField.setBackgroundResource(R.drawable.roundec_corners);

        GridLayout.LayoutParams paramsEditText = new GridLayout.LayoutParams();
        paramsEditText.width = 0;
        paramsEditText.height = 0;
        paramsEditText.rowSpec = GridLayout.spec(20, 5, 1f);
        paramsEditText.columnSpec = GridLayout.spec(6, 20, 1f);
        paramsEditText.setMargins(0, 0, 0, 0);

        searchField.setLayoutParams(paramsEditText);

        childCount++;
        return searchField;
    }

    private Button createLanguageBtn(){
        Button languageBtn = new Button(context);
        languageBtn.setText(context.getString(R.string.sprache));
        languageBtn.setBackgroundResource(R.drawable.roundec_corners);

        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width = 0;
        params.height = 0;
        params.rowSpec = GridLayout.spec(0, 7, 1f);
        params.columnSpec = GridLayout.spec(0, 11, 1f);
        params.setMargins(0,0,0,0);
        languageBtn.setLayoutParams(params);

        languageBtn.setOnClickListener(view -> {
            LocaleHelper localeHelper = new LocaleHelper();
            localeHelper.setLocale(context);
            ((Activity) context).recreate();
        });
        childCount++;
        return languageBtn;
    }

    private Button createSearchBtn(){
        Button searchBtn = new Button(context);
        searchBtn.setText(context.getString(R.string.suchen));
        searchBtn.setBackgroundResource(R.drawable.roundec_corners);

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

            TextView noResult = layoutManager.searchInitiated(input, childCount, context);
            if (noResult == null) {
                childCount = 0;
                viewList.clear();
            }
            else{
                List<View> necessaryList = new ArrayList<>();
                necessaryList.add(noResult);
                childCount++;
                layoutManager.updateLayout(necessaryList, -1);
            }
        });

        childCount++;
        return searchBtn;
    }

    private Button createDbBtn(){
        Button dbBtn = new Button(context);
        dbBtn.setText(context.getString(R.string.datenbank));
        dbBtn.setBackgroundResource(R.drawable.roundec_corners);

        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width = 0;
        params.height = 0;
        params.rowSpec = GridLayout.spec(70, 7, 1f);
        params.columnSpec = GridLayout.spec(3, 10, 1f);
        params.setMargins(0,0,0,0);
        dbBtn.setLayoutParams(params);

        dbBtn.setOnClickListener(view -> {
            layoutManager.updateLayout(chooseFromDb.getDbView(), childCount);
            viewList.clear();
            childCount = 0;
        });

        childCount++;
        return dbBtn;
    }

    private Button createGpsBtn(){
        Button gpsBtn = new Button(context);
        gpsBtn.setText("GPS");
        gpsBtn.setBackgroundResource(R.drawable.roundec_corners);

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
