package Controller;

import android.content.Context;
import android.util.Log;

import androidx.room.Room;

import java.util.List;
import java.util.concurrent.CountDownLatch;

import Model.AppDatabase;
import Model.Location;
import Model.LocationToCoords;

/**
 * Manages location data, including adding, querying, and deleting location entries in the database.
 * Utilizes {@link LocationToCoords} for fetching geographical coordinates based on location names.
 */
public class LocationHandler {

    final private AppDatabase db;

    /**
     * Initializes a new instance of LocationHandler with a specific application context.
     * Sets up the Room database for location data management.
     *
     * @param context The application's context.
     */
    public LocationHandler(Context context){
        //da nur einmal initialisiert und keine große operation wird auf main thread ausgeführt
        db = Room.databaseBuilder(context.getApplicationContext(),
                AppDatabase.class, "Location").allowMainThreadQueries().build();
    }

    /**
     * Adds a location to the database based on a textual search query. If multiple matches are found,
     * it requires user selection. Automatically adds the location to the database if a single match is found.
     *
     * @param newLocation The name of the location to search for and add.
     * @return A list of {@link Location} objects matching the search query.
     */
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
            // keine Stadt entspricht der Suche
                // genau eine Stadt entspricht der Suche
                if (matchingLocations.size() == 1) {
                    addLocationToDb(matchingLocations.get(0));
                }
                // mehrere Städte passen, eine muss ausgewählt werden
            return matchingLocations;
        } catch (InterruptedException e){
            Log.i("interrupt exception", e.toString());
            return null;
        }
    }

    /**
     * Placeholder for adding a location to the database based on GPS coordinates.
     * Intended for future implementation.
     */
    public void addLocationViaGps(){
        // wäre als nächstes angegangen worden
    }

    /**
     * Inserts a new location into the database if it does not already exist.
     *
     * @param newLocation The {@link Location} object to insert into the database.
     */
    public void addLocationToDb(Location newLocation){
        new Thread(() -> {
            if (db.locationDAO().getSameExactName(newLocation.getExactName()) == null){
                db.locationDAO().insert(newLocation);
            }
        }).start();
    }

    /**
     * Retrieves all unique location names stored in the database.
     *
     * @return A list of strings representing the exact names of all saved locations.
     */
    public List<String> getDbEntries(){
        return db.locationDAO().getAllByExactNames();
    }

    /**
     * Deletes a specific location from the database by its exact name.
     *
     * @param toDelete The exact name of the location to delete.
     */
    public void deleteDbEntry(String toDelete){
        new Thread(() -> {
            db.locationDAO().deleteEntry(toDelete);
        }).start();
    }

    /**
     * Fetches detailed information for a specific location by its exact name.
     *
     * @param exactName The exact name of the location to retrieve.
     * @return A {@link Location} object containing the detailed information of the requested location.
     */
    public Location getEntry(String exactName){
        return db.locationDAO().getEntry(exactName);
    }
}
