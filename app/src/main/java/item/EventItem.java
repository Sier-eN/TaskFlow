package item;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.Ignore;

@Entity(tableName = "Events")
public class EventItem {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String title;     // Tên sự kiện
    private String dateIso;   // Ngày (yyyy-MM-dd)
    private String colorHex;  // Màu (#RRGGBB)

    // Không lưu colorInt vào DB, chỉ dùng tạm trong app
    @Ignore
    private int colorInt = -1;

    // Constructor chỉ dùng khi tạo mới trong code, Room sẽ bỏ qua
    @Ignore
    public EventItem(String title, String dateIso, String colorHex) {
        this.title = title;
        this.dateIso = dateIso;
        this.colorHex = colorHex;
        parseColor();
    }

    // Constructor chính Room dùng để tạo object từ DB
    public EventItem(int id, String title, String dateIso, String colorHex) {
        this.id = id;
        this.title = title;
        this.dateIso = dateIso;
        this.colorHex = colorHex;
        parseColor();
    }

    // --- Getter & Setter ---
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getDateIso() { return dateIso; }
    public void setDateIso(String dateIso) {
        this.dateIso = dateIso;
    }

    public String getColorHex() { return colorHex; }
    public void setColorHex(String colorHex) {
        this.colorHex = colorHex;
        parseColor();
    }

    // Trả về mã màu đã parse
    public int getColorInt() {
        if (colorInt == -1) parseColor();
        return colorInt;
    }

    private void parseColor() {
        try {
            colorInt = android.graphics.Color.parseColor(colorHex);
        } catch (Exception e) {
            colorInt = android.graphics.Color.GRAY;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof EventItem)) return false;
        EventItem e = (EventItem) o;
        return id == e.id &&
                title.equals(e.title) &&
                dateIso.equals(e.dateIso) &&
                colorHex.equals(e.colorHex);
    }
}
