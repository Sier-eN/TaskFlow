package item;

public class BaoThuc {
    private int id;
    private int h; // giờ
    private int m; // phút
    private int t2, t3, t4, t5, t6, t7, cn; // ngày lặp
    private int bat; // 0 = tắt, 1 = bật
    private String ringtoneUri;

    // Constructor khi lấy từ database (có id)
    public BaoThuc(int id, int h, int m,
                   int t2, int t3, int t4, int t5, int t6, int t7, int cn, int bat) {
        this.id = id;
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
    }

    // Constructor khi tạo mới trước khi insert (không có id)
    public BaoThuc(int h, int m,
                   int t2, int t3, int t4, int t5, int t6, int t7, int cn, int bat) {
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
    }

    // Getter
    public int getId() { return id; }
    public int getH() { return h; }
    public int getM() { return m; }
    public int getT2() { return t2; }
    public int getT3() { return t3; }
    public int getT4() { return t4; }
    public int getT5() { return t5; }
    public int getT6() { return t6; }
    public int getT7() { return t7; }
    public int getCn() { return cn; }
    public int getBat() { return bat; }

    public String getRingtoneUri() { return ringtoneUri; }
    public boolean isActive() { return bat == 1; }

    // Setter
    public void setH(int h) { this.h = h; }
    public void setM(int m) { this.m = m; }
    public void setT2(int t2) { this.t2 = t2; }
    public void setT3(int t3) { this.t3 = t3; }
    public void setT4(int t4) { this.t4 = t4; }
    public void setT5(int t5) { this.t5 = t5; }
    public void setT6(int t6) { this.t6 = t6; }
    public void setT7(int t7) { this.t7 = t7; }
    public void setCn(int cn) { this.cn = cn; }
    public void setBat(int bat) { this.bat = bat; }
    public void setRingtoneUri(String ringtoneUri) { this.ringtoneUri = ringtoneUri; }

    // Hiển thị giờ: "HH:mm"
    public String getTimeString() {
        return String.format("%02d:%02d", h, m);
    }

    // Hiển thị ngày lặp
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

    public void setId(int id) {this.id = id;}
}
