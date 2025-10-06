package item;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Alarm")
public class BaoThuc {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private int h; // giờ
    private int m; // phút
    private int t2, t3, t4, t5, t6, t7, cn; // ngày lặp
    private int bat; // 0 = tắt, 1 = bật
    private String ringtoneUri;

    // Constructor tạo mới (không có id)
    public BaoThuc(int h, int m,
                   int t2, int t3, int t4, int t5, int t6, int t7, int cn, int bat, String ringtoneUri) {
        this.h = h;
        this.m = m;
        this.t2 = t2;
        this.t3 = t3;
        this.t4 = t4;
        this.t5 = t5;
        this.t6 = t6;
        this.t7 = t7;
        this.cn = cn;
        this.bat = bat;
        this.ringtoneUri = ringtoneUri;
    }

    // --- Getter & Setter ---
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getH() { return h; }
    public void setH(int h) { this.h = h; }

    public int getM() { return m; }
    public void setM(int m) { this.m = m; }

    public int getT2() { return t2; }
    public void setT2(int t2) { this.t2 = t2; }

    public int getT3() { return t3; }
    public void setT3(int t3) { this.t3 = t3; }

    public int getT4() { return t4; }
    public void setT4(int t4) { this.t4 = t4; }

    public int getT5() { return t5; }
    public void setT5(int t5) { this.t5 = t5; }

    public int getT6() { return t6; }
    public void setT6(int t6) { this.t6 = t6; }

    public int getT7() { return t7; }
    public void setT7(int t7) { this.t7 = t7; }

    public int getCn() { return cn; }
    public void setCn(int cn) { this.cn = cn; }

    public int getBat() { return bat; }
    public void setBat(int bat) { this.bat = bat; }

    public String getRingtoneUri() { return ringtoneUri; }
    public void setRingtoneUri(String ringtoneUri) { this.ringtoneUri = ringtoneUri; }

    // --- Hàm tiện ích ---
    public String getTimeString() {
        return String.format("%02d:%02d", h, m);
    }

    public String getRepeatString() {
        StringBuilder sb = new StringBuilder();
        if (t2 == 1) sb.append("T2 ");
        if (t3 == 1) sb.append("T3 ");
        if (t4 == 1) sb.append("T4 ");
        if (t5 == 1) sb.append("T5 ");
        if (t6 == 1) sb.append("T6 ");
        if (t7 == 1) sb.append("T7 ");
        if (cn == 1) sb.append("CN ");
        return sb.length() > 0 ? sb.toString().trim() : "Không lặp";
    }

    public boolean isActive() { return bat == 1; }
}
