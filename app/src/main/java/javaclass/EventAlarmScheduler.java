package javaclass;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import item.EventItem;

public class EventAlarmScheduler {

    // Bật chế độ test 1 phút
    private static final boolean DEBUG_TEST = false;

    public static void scheduleEventAlarm(Context context, EventItem event) {
        String dateIso = event.getDateIso(); // yyyy-MM-dd
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = sdf.parse(dateIso);
            if (date == null) return;

            Calendar cal = Calendar.getInstance();

            if (DEBUG_TEST) {
                // ✅ Test: đặt alarm sau 1 phút từ bây giờ
                cal.setTimeInMillis(System.currentTimeMillis());
                cal.add(Calendar.MINUTE, 1);
            } else {
                // ✅ Chạy thật: đặt vào 6h sáng ngày sự kiện
                cal.setTime(date);
                cal.set(Calendar.HOUR_OF_DAY, 6);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);

                // nếu đã qua 6h hôm nay thì bỏ qua
                if (cal.getTimeInMillis() < System.currentTimeMillis()) return;
            }

            Intent intent = new Intent(context, EventAlarmReceiver.class);
            intent.putExtra("event_title", event.getTitle());
            intent.putExtra("event_id", event.getId());

            PendingIntent pi = PendingIntent.getBroadcast(
                    context,
                    event.getId(),
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );

            AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            if (am != null) {
                am.setExact(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pi);
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
