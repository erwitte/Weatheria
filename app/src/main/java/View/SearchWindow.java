package View;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import com.example.weatheria.R;

import java.util.ArrayList;
import java.util.List;

// Erik Witte
public class SearchWindow extends AppCompatActivity {
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
        Button searchBtn = createSearchBtn();
        Button dbBtn = createDbBtn();
        Button gpsBtn = createGpsBtn();
        Button languageBtn = createLanguageBtn();

        viewList.add(createSearchField());
        viewList.add(searchBtn);
        viewList.add(dbBtn);
        viewList.add(gpsBtn);
        viewList.add(languageBtn);

        createClickListeners(gpsBtn, searchBtn, languageBtn, dbBtn);
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

    private void createClickListeners(Button gps, Button search, Button language, Button db){
        db.setOnClickListener(v -> {
            layoutManager.updateLayout(chooseFromDb.getDbView(), childCount);
            viewList.clear();
            childCount = 0;
        });

        search.setOnClickListener(v -> {
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

        language.setOnClickListener(v -> {
            LocaleManager localeManager = new LocaleManager();
            localeManager.setLocale(context);
            ((Activity) context).recreate();
        });

        gps.setOnClickListener(v -> {
            int LOCATION_PERMISSION_REQUEST_CODE = 1;
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            }
        });
    }
}
