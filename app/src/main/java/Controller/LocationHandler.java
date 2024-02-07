package Controller;

import android.content.Context;
import android.util.Log;

import androidx.room.Room;

import java.util.ArrayList;
import java.util.List;

import Model.AppDatabase;
import Model.Location;

//Erik Witte
public class LocationHandler {

    private String chosenLocation;
    private AppDatabase db;

    //
    public LocationHandler(Context context){
        db = Room.databaseBuilder(context.getApplicationContext(),
                AppDatabase.class, "Location").allowMainThreadQueries().build();
    }

    public void addLocationViaText(String newLocation){
        List<String> asf = new ArrayList<>();
        LocationToCoords test = new LocationToCoords(locations -> {
            for (String as : locations) {
                Log.i("Ergebnisse", as);
                asf.add(as);
            }
        });
        addLocationToDb(new Location("Lohne, Landkreis Vechta, Niedersachsen, 49393, Deutschland", "Lohne", 52, 52));

        List<Location> es = db.locationDAO().getAllLocations();
        new Thread(() -> {
            for (Location a : es) {
                Log.i("Ergebnis", a.getName());
            }
        }).start();
        test.execute(newLocation);
    }

    public void addLocationViaGps(){

    }

    private void addLocationToDb(Location location){
        new Thread(() -> {
            if (db.locationDAO().getSameExactName("Lohne, Landkreis Vechta, Niedersachsen, 49393, Deutschland") == null){
                db.locationDAO().insert(location);
            }
        }).start();
    }
}
