package item;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "HenGio")
public class HenGio {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private int gio;
    private int phut;
    private int giay;

    public HenGio(int gio, int phut, int giay) {
        this.gio = gio;
        this.phut = phut;
        this.giay = giay;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getGio() { return gio; }
    public int getPhut() { return phut; }
    public int getGiay() { return giay; }

    public String getFormattedTime() {
        return String.format("%dh %dm %ds", gio, phut, giay);
    }
}
