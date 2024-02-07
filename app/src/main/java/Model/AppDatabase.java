package Model;

import androidx.room.Database;
import androidx.room.RoomDatabase;

public abstract class AppDatabase extends RoomDatabase{
    public abstract LocationDAO locationDAO();
}
