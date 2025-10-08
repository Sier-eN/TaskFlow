package dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import item.BaoThuc;

@Dao
public interface BaoThucDao {

    @Insert
    void insert(BaoThuc baoThuc);

    @Update
    void update(BaoThuc baoThuc);

    @Delete
    void delete(BaoThuc baoThuc);

    @Query("SELECT * FROM Alarm ORDER BY h ASC, m ASC")
    List<BaoThuc> getAll();

    @Query("SELECT * FROM Alarm WHERE id = :id LIMIT 1")
    BaoThuc getById(int id);

    @Query("DELETE FROM Alarm")
    void deleteAll();
}
