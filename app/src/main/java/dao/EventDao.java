package dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import item.EventItem;

@Dao
public interface EventDao {

    @Insert
    void insert(EventItem event);

    @Update
    void update(EventItem event);

    @Delete
    void delete(EventItem event);

    @Query("SELECT * FROM Events ORDER BY dateIso ASC")
    List<EventItem> getAll();

    @Query("SELECT * FROM Events WHERE id = :id LIMIT 1")
    EventItem getById(int id);

    // 🔹 Đổi tên cho đúng với chỗ gọi trong HomeFragment
    @Query("SELECT * FROM Events WHERE dateIso = :dateIso ORDER BY id ASC")
    List<EventItem> getEventsByDate(String dateIso);

    @Query("DELETE FROM Events")
    void deleteAll();
}
