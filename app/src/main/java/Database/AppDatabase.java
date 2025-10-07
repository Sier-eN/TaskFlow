package Database;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import android.content.Context;

import dao.BaoThucDao;
import dao.EventDao;
import dao.ActivityItemDao;
import dao.HenGioDao;
import item.BaoThuc;
import item.EventItem;
import item.ActivityItem;
import item.HenGio;

@Database(
        entities = {BaoThuc.class, EventItem.class, ActivityItem.class, HenGio.class},
        version = 3,
        exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase instance;

    public abstract BaoThucDao baoThucDao();
    public abstract EventDao eventDao();
    public abstract ActivityItemDao activityItemDao();
    public abstract HenGioDao henGioDao(); // ✅ thêm dòng này

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            "QuanLyThoiGian.db"
                    )
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}
