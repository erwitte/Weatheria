package Controller;

import android.content.Context;
import android.util.Log;

import androidx.room.Room;

import com.example.weatheria.R;

import Model.AppDatabase;

public class LocationHandler {

    private String chosenLocation;
    private AppDatabase db;

    //
    public LocationHandler(Context context){
        new Thread(() -> db = Room.databaseBuilder(context.getApplicationContext(),
                AppDatabase.class, "Location").build());
    }

    public void addLocationViaText(String newLocation){
        LocationToCoords test = new LocationToCoords(locations -> {
            for (String as : locations) {
                Log.i("Ergebnisse", as);
            }
        });
        test.execute(newLocation);
    }

    public void addLocationViaGps(){

    }

    private void chooseLocationFromMultiple(){

    }
}
