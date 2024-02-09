package View;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.example.weatheria.R;

import Controller.LocationHandler;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EditText initialInput = findViewById(R.id.etCity);
        Button btnSubmit = findViewById(R.id.btnGet);
        LocationHandler locationHandler = new LocationHandler(this);

        btnSubmit.setOnClickListener(view -> {
            String location = initialInput.getText().toString();
            locationHandler.addLocationViaText(location);
        });
    }
}