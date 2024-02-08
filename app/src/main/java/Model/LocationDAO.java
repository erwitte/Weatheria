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

    @Query("SELECT exactName FROM Location")
    List<String> getAllByExactNames();

    @Query("SELECT exactName FROM Location WHERE exactName like :exactName")
    String getSameExactName(String exactName);

    @Query("DELETE FROM Location WHERE exactName = :exactName")
    void deleteEntry(String exactName);

    @Query("SELECT * FROM location")
    List<Location> getAll();
}
