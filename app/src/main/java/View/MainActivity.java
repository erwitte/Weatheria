package View;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.example.weatheria.R;

import java.util.List;

import Controller.LocationToCoords;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LocationToCoords test = new LocationToCoords(locations -> {
            for (String as : locations){
                Log.i("Ergebnisse", as);
            }
        });
        test.execute("Bremen");
    }
}