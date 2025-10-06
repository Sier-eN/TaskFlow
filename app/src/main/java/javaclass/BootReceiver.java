package javaclass;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.List;

import Database.DatabaseHelper;
import item.EventItem;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Log.d("BootReceiver", "Thiết bị vừa khởi động lại -> đặt lại tất cả alarm");

            // Lấy toàn bộ sự kiện trong DB
            DatabaseHelper db = new DatabaseHelper(context);
            List<EventItem> events = db.getAllEvents();

            // Đặt lại alarm cho từng event
            for (EventItem e : events) {
                EventAlarmScheduler.scheduleEventAlarm(context, e);
                Log.d("BootReceiver", "Đặt lại alarm cho: " + e.getTitle() + " - " + e.getDateIso());
            }
        }
    }
}
