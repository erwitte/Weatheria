package Controller;

import android.content.Context;
import android.util.Log;

import androidx.room.Room;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import Model.AppDatabase;
import Model.Location;

//Erik Witte
public class LocationHandler {

    private String chosenLocation;
    private AppDatabase db;

    //
    public LocationHandler(Context context){
        //da nur einmal initialisiert und keine große operation wird auf main thread ausgeführt
        db = Room.databaseBuilder(context.getApplicationContext(),
                AppDatabase.class, "Location").allowMainThreadQueries().build();
    }

    public List<Location> addLocationViaText(String newLocation){
        // CountDownLatch ähnlich zu Semaphoren
        CountDownLatch latch = new CountDownLatch(1);
        List<Location> matchingLocations;
        // für wettervorhersage werden koordinaten benötigt
        LocationToCoords locationToCoords = new LocationToCoords(latch);
        // locationToCoords auf eigenem Thread starten
        locationToCoords.execute(newLocation);
        try {
            latch.await();
            matchingLocations = locationToCoords.getMatchingLocations();
            if (matchingLocations.size() == 0)
                return null;
            else {
                if (matchingLocations.size() == 1)
                    addLocationToDb(matchingLocations.get(0));
                else {
                    
                }
            }
        } catch (InterruptedException e){
            Log.i("interrupt exception", e.toString());
            return null;
        }
    }

    public void addLocationViaGps(){

    }

    private void addLocationToDb(Location newLocation){
        new Thread(() -> {
            if (db.locationDAO().getSameExactName(newLocation.getExactName()) == null){
                db.locationDAO().insert(newLocation);
            }
        }).start();
    }
}
