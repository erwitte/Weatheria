package Model;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;

/**
 * Data Access Object (DAO) for managing CRUD operations for {@link Location} entities.
 */
@Dao
public interface LocationDAO {
    /**
     * Inserts a new {@link Location} into the database.
     *
     * @param location The location object to insert.
     */
    @Insert
    void insert(Location location);

    /**
     * Retrieves all location names from the database.
     *
     * @return A list of strings representing the exact names of all locations stored in the database.
     */
    @Query("SELECT exactName FROM Location")
    List<String> getAllByExactNames();

    /**
     * Searches for a location with a matching exact name.
     *
     * @param exactName The exact name of the location to find.
     * @return The exact name of the matching location, or null if no match is found.
     */
    @Query("SELECT exactName FROM Location WHERE exactName like :exactName")
    String getSameExactName(String exactName);

    /**
     * Deletes a location from the database by its exact name.
     *
     * @param exactName The exact name of the location to delete.
     */
    @Query("DELETE FROM Location WHERE exactName = :exactName")
    void deleteEntry(String exactName);

    /**
     * Retrieves a {@link Location} object from the database by its exact name.
     *
     * @param exactName The exact name of the location to retrieve.
     * @return The {@link Location} object if found, or null if no match is found.
     */
    @Query("SELECT * FROM location WHERE exactName like :exactName")
    Location getEntry(String exactName);
}
