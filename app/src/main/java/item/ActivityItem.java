package item;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Activities")
public class ActivityItem {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String title;        // Tên hoạt động
    private String dateIso;      // Ngày yyyy-MM-dd
    private String startTime;    // Giờ bắt đầu HH:mm
    private String endTime;      // Giờ kết thúc HH:mm
    private String colorHex;     // Màu hiển thị (#RRGGBB)
    private String description;  // Mô tả hoạt động

    // Constructor khi tạo mới
    public ActivityItem(String title, String dateIso, String startTime, String endTime, String colorHex, String description) {
        this.title = title;
        this.dateIso = dateIso;
        this.startTime = startTime;
        this.endTime = endTime;
        this.colorHex = colorHex;
        this.description = description;
    }

    // --- Getter & Setter ---
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDateIso() { return dateIso; }
    public void setDateIso(String dateIso) { this.dateIso = dateIso; }

    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }

    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }

    public String getColorHex() { return colorHex; }
    public void setColorHex(String colorHex) { this.colorHex = colorHex; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
