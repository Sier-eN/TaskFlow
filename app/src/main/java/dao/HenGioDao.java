package dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;
import item.HenGio;

@Dao
public interface HenGioDao {
    @Insert
    void insert(HenGio henGio);
    @Delete
    void delete(HenGio henGio);
    @Query("SELECT * FROM HenGio")
    List<HenGio> getAll();

}
