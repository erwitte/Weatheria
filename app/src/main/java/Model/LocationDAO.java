package Model;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;

//Erik Witte
@Dao
public interface LocationDAO {
    @Insert
    void insert(Location location);

    @Query("SELECT * FROM Location")
    List<Location> getAllLocations();

    @Query("SELECT exactName FROM Location WHERE exactName like :exactName")
    String getSameExactName(String exactName);
}
