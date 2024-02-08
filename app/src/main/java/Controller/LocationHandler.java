package Controller;

import android.content.Context;
import android.util.Log;

import androidx.room.Room;

import java.util.List;
import java.util.concurrent.CountDownLatch;

import Model.AppDatabase;
import Model.Location;
import Model.WeatherFetcher;

//Erik Witte
public class LocationHandler {

    private AppDatabase db;
    CountDownLatch latch = new CountDownLatch(1);

    //
    public LocationHandler(Context context){
        //da nur einmal initialisiert und keine große operation wird auf main thread ausgeführt
        db = Room.databaseBuilder(context.getApplicationContext(),
                AppDatabase.class, "Location").allowMainThreadQueries().build();
    }

    public List<Location> addLocationViaText(String newLocation){
        // CountDownLatch ähnlich zu Semaphoren
        List<Location> matchingLocations;
        // für wettervorhersage werden koordinaten benötigt
        LocationToCoords locationToCoords = new LocationToCoords(latch);
        // locationToCoords auf eigenem Thread starten
        locationToCoords.execute(newLocation);
        try {
            latch.await();
            matchingLocations = locationToCoords.getMatchingLocations();
            // keine Stadt entspricht der Suche
            if (matchingLocations.size() == 0)
                return matchingLocations;
            else {
                // genau eine Stadt entspricht der Suche
                if (matchingLocations.size() == 1) {
                    addLocationToDb(matchingLocations.get(0));
                    WeatherFetcher test = new WeatherFetcher();
                    Log.i("est", test.getCurrentWeather(matchingLocations.get(0).getLatitude(), matchingLocations.get(0).getLongitude()));
                    return matchingLocations;
                }
                // mehrere Städte passen, eine muss ausgewählt werden
                else {
                    return matchingLocations;
                }
            }
        } catch (InterruptedException e){
            Log.i("interrupt exception", e.toString());
            return null;
        }
    }

    public void addLocationViaGps(){

    }

    public void addLocationToDb(Location newLocation){
        new Thread(() -> {
            if (db.locationDAO().getSameExactName(newLocation.getExactName()) == null){
                db.locationDAO().insert(newLocation);
            }
        }).start();
    }

    public List<String> getDbEntries(){
        return db.locationDAO().getAllByExactNames();
    }

    public void deleteDbEntry(String toDelete){
        new Thread(() -> {
            db.locationDAO().deleteEntry(toDelete);
        }).start();
    }

    public CountDownLatch getLatch(){
        return latch;
    }
}
