package Model;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;

@Dao
public interface LocationDAO {
    @Insert
    void insert(Location location);

    @Query("SELECT name FROM Location")
    List<Location> getAllLocations();
}
