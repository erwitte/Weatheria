package Model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

//Erik Witte
@Entity
public class Location {
    @PrimaryKey
    @NonNull
    private String exactName;
    private String name;
    private double latitude;
    private double longitude;

    public Location(String exactName, String name, double latitude, double longitude){
        this.exactName = exactName;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getName(){
        return this.name;
    }

    public double getLatitude(){
        return this.latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getExactName(){
        return exactName;
    }
}
