package item;

public class EventItem {
    private int id;
    private String title;
    private String dateIso;
    private String colorHex;

    // cache màu dạng int
    private int colorInt = -1;

    // khi chưa có id (event mới thêm, chưa lưu db)
    public EventItem(String title, String dateIso, String colorHex) {
        this.id = -1;
        this.title = title;
        this.dateIso = dateIso;
        this.colorHex = colorHex;
        parseColor();
    }

    // khi đã có id (event lấy từ db)
    public EventItem(int id, String title, String dateIso, String colorHex) {
        this.id = id;   // ✅ giữ nguyên id thật
        this.title = title;
        this.dateIso = dateIso;
        this.colorHex = colorHex;
        parseColor();
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDateIso() { return dateIso; }
    public void setDateIso(String dateIso) { this.dateIso = dateIso; }

    public String getColorHex() { return colorHex; }
    public void setColorHex(String colorHex) {
        this.colorHex = colorHex;
        parseColor();
    }

    // trả về màu đã parse, tránh parse lại nhiều lần
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
