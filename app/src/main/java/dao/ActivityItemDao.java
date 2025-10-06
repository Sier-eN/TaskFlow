package dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import item.ActivityItem;

@Dao
public interface ActivityItemDao {

    @Insert
    void insert(ActivityItem activity);

    @Update
    void update(ActivityItem activity);

    @Delete
    void delete(ActivityItem activity);

    @Query("SELECT * FROM Activities ORDER BY dateIso ASC")
    List<ActivityItem> getAll();

    @Query("SELECT * FROM Activities WHERE id = :id LIMIT 1")
    ActivityItem getById(int id);

    @Query("SELECT * FROM Activities WHERE dateIso = :date")
    List<ActivityItem> getByDate(String date);

    @Query("DELETE FROM Activities")
    void deleteAll();
}
