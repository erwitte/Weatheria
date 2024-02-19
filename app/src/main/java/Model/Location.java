package Model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Represents a location entity in the database.
 * This entity includes the exact name of the location, which serves as the primary key,
 * a more general name, and the geographical coordinates (latitude and longitude).
 */
@Entity
public class Location {
    @PrimaryKey
    @NonNull
    private final String exactName;
    private final String name;
    private final double latitude;
    private final double longitude;

    /**
     * Constructs a new Location instance.
     *
     * @param exactName The exact name of the location, serving as a unique identifier.
     * @param name The general name of the location.
     * @param latitude The latitude of the location.
     * @param longitude The longitude of the location.
     */

    public Location(@NonNull String exactName, String name, double latitude, double longitude){
        // Lohne, Landkreis Vechta, Niedersachsen, 49393, Deutschland
        this.exactName = exactName;
        // Lohne
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    /**
     * Gets the general name of the location.
     *
     * @return The general name of the location.
     */
    public String getName(){
        return this.name;
    }

    /**
     * Gets the latitude of the location.
     *
     * @return The latitude of the location.
     */
    public double getLatitude(){
        return this.latitude;
    }

    /**
     * Gets the longitude of the location.
     *
     * @return The longitude of the location.
     */
    public double getLongitude() {
        return longitude;
    }

    /**
     * Gets the exact name of the location.
     *
     * @return The exact name of the location.
     */
    @NonNull
    public String getExactName(){
        return exactName;
    }
}
