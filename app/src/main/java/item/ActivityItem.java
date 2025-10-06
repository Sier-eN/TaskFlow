package item;

public class ActivityItem {
    private int id;
    private String title;
    private String dateIso;
    private String startTime;
    private String endTime;
    private String colorHex;

    public ActivityItem(int id, String title, String dateIso, String colorHex) {
        this.id = id;
        this.title = title;
        this.dateIso = dateIso;
        this.colorHex = colorHex;
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getDateIso() { return dateIso; }
    public String getStartTime() { return startTime; }
    public String getEndTime() { return endTime; }
    public String getColorHex() { return colorHex; }

    public void setTitle(String title) { this.title = title; }
    public void setDateIso(String dateIso) { this.dateIso = dateIso; }
    public void setStartTime(String startTime) { this.startTime = startTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }
    public void setColorHex(String colorHex) { this.colorHex = colorHex; }
}
