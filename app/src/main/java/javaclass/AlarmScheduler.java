package javaclass;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import java.util.Calendar;

import item.BaoThuc;

public class AlarmScheduler {

    public static void datBaoThuc(Context context, BaoThuc baoThuc) {
        if (baoThuc.getBat() == 0) return;

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Calendar now = Calendar.getInstance();

        int[] days = {baoThuc.getT2(), baoThuc.getT3(), baoThuc.getT4(),
                baoThuc.getT5(), baoThuc.getT6(), baoThuc.getT7(), baoThuc.getCn()};

        boolean anyDay = false;
        for (int d : days) if (d == 1) { anyDay = true; break; }

        if (!anyDay) {
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.HOUR_OF_DAY, baoThuc.getH());
            cal.set(Calendar.MINUTE, baoThuc.getM());
            cal.set(Calendar.SECOND, 0);
            if (cal.getTimeInMillis() <= now.getTimeInMillis()) cal.add(Calendar.DAY_OF_MONTH, 1);
            setAlarm(context, alarmManager, cal, baoThuc, 0);
            return;
        }

        for (int i = 0; i < 7; i++) {
            if (days[i] == 0) continue;
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.HOUR_OF_DAY, baoThuc.getH());
            cal.set(Calendar.MINUTE, baoThuc.getM());
            cal.set(Calendar.SECOND, 0);

            int dayOfWeek = (i == 6) ? Calendar.SUNDAY : i + 2;
            int delta = dayOfWeek - cal.get(Calendar.DAY_OF_WEEK);
            if (delta < 0 || (delta == 0 && cal.getTimeInMillis() <= now.getTimeInMillis())) delta += 7;
            cal.add(Calendar.DAY_OF_MONTH, delta);

            setAlarm(context, alarmManager, cal, baoThuc, i);
        }
    }

    private static void setAlarm(Context context, AlarmManager alarmManager, Calendar calendar, BaoThuc baoThuc, int dayIndex) {
        Intent intent = new Intent(context, BaoThucReceiver.class);
        intent.putExtra("id", baoThuc.getId());

        PendingIntent pi = PendingIntent.getBroadcast(
                context,
                baoThuc.getId() * 10 + dayIndex,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        if (alarmManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pi);
            else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pi);
            else
                alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pi);
        }
    }

    public static void huyBaoThuc(Context context, BaoThuc baoThuc) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        for (int i = 0; i < 7; i++) {
            Intent intent = new Intent(context, BaoThucReceiver.class);
            PendingIntent pi = PendingIntent.getBroadcast(
                    context,
                    baoThuc.getId() * 10 + i,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );
            if (alarmManager != null) alarmManager.cancel(pi);
        }
    }
}
