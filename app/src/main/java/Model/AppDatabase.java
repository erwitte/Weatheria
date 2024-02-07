package Model;

import androidx.room.Database;
import androidx.room.RoomDatabase;

//Erik Witte
@Database(entities = {Location.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase{
    public abstract LocationDAO locationDAO();
}
