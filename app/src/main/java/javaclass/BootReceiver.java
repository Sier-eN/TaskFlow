package javaclass;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.List;

import Database.AppDatabase;
import item.EventItem;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Log.d("BootReceiver", "Thiết bị vừa khởi động lại -> đặt lại tất cả alarm");

            // Lấy toàn bộ sự kiện từ Room database
            AppDatabase db = AppDatabase.getInstance(context);
            List<EventItem> events = db.eventDao().getAll();

            // Đặt lại alarm cho từng sự kiện
            for (EventItem e : events) {
                EventAlarmScheduler.scheduleEventAlarm(context, e);
                Log.d("BootReceiver", "Đặt lại alarm cho: " + e.getTitle() + " - " + e.getDateIso());
            }
        }
    }
}
