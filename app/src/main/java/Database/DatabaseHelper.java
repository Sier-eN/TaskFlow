package Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import item.ActivityItem;
import item.BaoThuc;
import item.EventItem; // class model mới (mình sẽ cung cấp)

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "QuanLyThoiGian.db";
    private static final int DB_VERSION = 4; // Tăng lên 3 để migration Events

    public static final String TABLE_ALARM = "Alarm";

    // New table for events
    public static final String TABLE_EVENTS = "Events";

    public static final String TABLE_ACTIVITIES = "Activities";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Alarm table (giữ nguyên)
        String createAlarm = "CREATE TABLE " + TABLE_ALARM + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "h INTEGER NOT NULL, " +
                "m INTEGER NOT NULL, " +
                "t2 INTEGER DEFAULT 0, " +
                "t3 INTEGER DEFAULT 0, " +
                "t4 INTEGER DEFAULT 0, " +
                "t5 INTEGER DEFAULT 0, " +
                "t6 INTEGER DEFAULT 0, " +
                "t7 INTEGER DEFAULT 0, " +
                "cn INTEGER DEFAULT 0, " +
                "bat INTEGER DEFAULT 0, " +
                "ringtoneUri TEXT" +
                ")";
        db.execSQL(createAlarm);

        // Events table
        String createEvents = "CREATE TABLE " + TABLE_EVENTS + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "title TEXT NOT NULL, " +
                "dateIso TEXT NOT NULL, " + // store yyyy-MM-dd
                "colorHex TEXT NOT NULL" +   // store color like #RRGGBB or #AARRGGBB
                ")";
        db.execSQL(createEvents);

        // Bảng mới cho hoạt động
        String createActivities = "CREATE TABLE " + TABLE_ACTIVITIES + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "title TEXT NOT NULL, " +
                "dateIso TEXT NOT NULL, " +
                "startTime TEXT, " +
                "endTime TEXT, " +
                "colorHex TEXT NOT NULL" +
                ")";
        db.execSQL(createActivities);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Nếu cũ chưa có cột ringtoneUri thì thêm (v2)
        if (oldVersion < 2) {
            try {
                db.execSQL("ALTER TABLE " + TABLE_ALARM + " ADD COLUMN ringtoneUri TEXT");
            } catch (Exception ignored) { }
        }
        // Nếu cũ chưa có bảng Events thì tạo (v3)
        if (oldVersion < 3) {
            String createEvents = "CREATE TABLE IF NOT EXISTS " + TABLE_EVENTS + " (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "title TEXT NOT NULL, " +
                    "dateIso TEXT NOT NULL, " +
                    "colorHex TEXT NOT NULL" +
                    ")";
            db.execSQL(createEvents);
        }
        if (oldVersion < 5) {
            String createActivities = "CREATE TABLE IF NOT EXISTS " + TABLE_ACTIVITIES + " (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "title TEXT NOT NULL, " +
                    "dateIso TEXT NOT NULL, " +
                    "startTime TEXT, " +
                    "endTime TEXT, " +
                    "colorHex TEXT NOT NULL" +
                    ")";
            db.execSQL(createActivities);
        }
    }

    // ---------------- Alarm methods (giữ nguyên) ----------------
    // Insert
    public long insertBaoThuc(BaoThuc baoThuc) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("h", baoThuc.getH());
        values.put("m", baoThuc.getM());
        values.put("t2", baoThuc.getT2());
        values.put("t3", baoThuc.getT3());
        values.put("t4", baoThuc.getT4());
        values.put("t5", baoThuc.getT5());
        values.put("t6", baoThuc.getT6());
        values.put("t7", baoThuc.getT7());
        values.put("cn", baoThuc.getCn());
        values.put("bat", baoThuc.getBat());
        values.put("ringtoneUri", baoThuc.getRingtoneUri());
        long id = db.insert(TABLE_ALARM, null, values);
        db.close();
        return id;
    }

    // Get all
    public List<BaoThuc> getAllBaoThuc() {
        List<BaoThuc> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_ALARM, null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                int h = cursor.getInt(cursor.getColumnIndexOrThrow("h"));
                int m = cursor.getInt(cursor.getColumnIndexOrThrow("m"));
                int t2 = cursor.getInt(cursor.getColumnIndexOrThrow("t2"));
                int t3 = cursor.getInt(cursor.getColumnIndexOrThrow("t3"));
                int t4 = cursor.getInt(cursor.getColumnIndexOrThrow("t4"));
                int t5 = cursor.getInt(cursor.getColumnIndexOrThrow("t5"));
                int t6 = cursor.getInt(cursor.getColumnIndexOrThrow("t6"));
                int t7 = cursor.getInt(cursor.getColumnIndexOrThrow("t7"));
                int cn = cursor.getInt(cursor.getColumnIndexOrThrow("cn"));
                int bat = cursor.getInt(cursor.getColumnIndexOrThrow("bat"));
                String ringtoneUri = null;
                int idx = cursor.getColumnIndex("ringtoneUri");
                if (idx != -1) ringtoneUri = cursor.getString(idx);

                BaoThuc bt = new BaoThuc(id, h, m, t2, t3, t4, t5, t6, t7, cn, bat);
                bt.setRingtoneUri(ringtoneUri);
                list.add(bt);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return list;
    }

    // Update
    public void updateBaoThuc(BaoThuc b) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("h", b.getH());
        values.put("m", b.getM());
        values.put("t2", b.getT2());
        values.put("t3", b.getT3());
        values.put("t4", b.getT4());
        values.put("t5", b.getT5());
        values.put("t6", b.getT6());
        values.put("t7", b.getT7());
        values.put("cn", b.getCn());
        values.put("bat", b.getBat());
        values.put("ringtoneUri", b.getRingtoneUri());
        db.update(TABLE_ALARM, values, "id=?", new String[]{String.valueOf(b.getId())});
        db.close();
    }

    // Delete
    public void deleteBaoThuc(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ALARM, "id=?", new String[]{String.valueOf(id)});
        db.close();
    }

    // Get by id
    public BaoThuc getBaoThucById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_ALARM + " WHERE id = ?", new String[]{String.valueOf(id)});
        BaoThuc baoThuc = null;
        if (cursor.moveToFirst()) {
            int h = cursor.getInt(cursor.getColumnIndexOrThrow("h"));
            int m = cursor.getInt(cursor.getColumnIndexOrThrow("m"));
            int t2 = cursor.getInt(cursor.getColumnIndexOrThrow("t2"));
            int t3 = cursor.getInt(cursor.getColumnIndexOrThrow("t3"));
            int t4 = cursor.getInt(cursor.getColumnIndexOrThrow("t4"));
            int t5 = cursor.getInt(cursor.getColumnIndexOrThrow("t5"));
            int t6 = cursor.getInt(cursor.getColumnIndexOrThrow("t6"));
            int t7 = cursor.getInt(cursor.getColumnIndexOrThrow("t7"));
            int cn = cursor.getInt(cursor.getColumnIndexOrThrow("cn"));
            int bat = cursor.getInt(cursor.getColumnIndexOrThrow("bat"));
            String ringtoneUri = null;
            int idx = cursor.getColumnIndex("ringtoneUri");
            if (idx != -1) ringtoneUri = cursor.getString(idx);

            baoThuc = new BaoThuc(id, h, m, t2, t3, t4, t5, t6, t7, cn, bat);
            baoThuc.setRingtoneUri(ringtoneUri);
        }
        cursor.close();
        db.close();
        return baoThuc;
    }

    // ---------------- Events methods (mới) ----------------

    // Insert event
    public long insertEvent(EventItem event) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title", event.getTitle());
        values.put("dateIso", event.getDateIso());
        values.put("colorHex", event.getColorHex());
        long id = db.insert(TABLE_EVENTS, null, values);
        db.close();
        return id;
    }

    // Get all events
    public List<EventItem> getAllEvents() {
        List<EventItem> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_EVENTS + " ORDER BY dateIso", null);
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
                String dateIso = cursor.getString(cursor.getColumnIndexOrThrow("dateIso"));
                String colorHex = cursor.getString(cursor.getColumnIndexOrThrow("colorHex"));
                EventItem e = new EventItem(id, title, dateIso, colorHex);
                list.add(e);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }

    // Get events by dateIso (yyyy-MM-dd)
    public List<EventItem> getEventsByDate(String dateIso) {
        List<EventItem> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_EVENTS + " WHERE dateIso = ?", new String[]{dateIso});
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
                String colorHex = cursor.getString(cursor.getColumnIndexOrThrow("colorHex"));
                EventItem e = new EventItem(id, title, dateIso, colorHex);
                list.add(e);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }

    // Update event
    public void updateEvent(EventItem event) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title", event.getTitle());
        values.put("dateIso", event.getDateIso());
        values.put("colorHex", event.getColorHex());
        db.update(TABLE_EVENTS, values, "id=?", new String[]{String.valueOf(event.getId())});
        db.close();
    }

    // Delete event
    public void deleteEvent(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_EVENTS, "id=?", new String[]{String.valueOf(id)});
        db.close();
    }


    // ---------------- Activities ----------------
    public long insertActivity(ActivityItem activity) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title", activity.getTitle());
        values.put("dateIso", activity.getDateIso());
        values.put("startTime", activity.getStartTime());
        values.put("endTime", activity.getEndTime());
        values.put("colorHex", activity.getColorHex());
        long id = db.insert(TABLE_ACTIVITIES, null, values);
        db.close();
        return id;
    }

    public List<ActivityItem> getAllActivities() {
        List<ActivityItem> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_ACTIVITIES + " ORDER BY dateIso", null);
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
                String dateIso = cursor.getString(cursor.getColumnIndexOrThrow("dateIso"));
                String startTime = cursor.getString(cursor.getColumnIndexOrThrow("startTime"));
                String endTime = cursor.getString(cursor.getColumnIndexOrThrow("endTime"));
                String colorHex = cursor.getString(cursor.getColumnIndexOrThrow("colorHex"));
                ActivityItem a = new ActivityItem(id, title, dateIso, colorHex);
                a.setStartTime(startTime);
                a.setEndTime(endTime);
                list.add(a);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }

    public void updateActivity(ActivityItem activity) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title", activity.getTitle());
        values.put("dateIso", activity.getDateIso());
        values.put("startTime", activity.getStartTime());
        values.put("endTime", activity.getEndTime());
        values.put("colorHex", activity.getColorHex());
        db.update(TABLE_ACTIVITIES, values, "id=?", new String[]{String.valueOf(activity.getId())});
        db.close();
    }

    public void deleteActivity(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ACTIVITIES, "id=?", new String[]{String.valueOf(id)});
        db.close();
    }
}
