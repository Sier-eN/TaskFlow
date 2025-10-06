package javaclass;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import java.util.Calendar;

import Database.DatabaseHelper;
import item.BaoThuc;

public class AlarmScheduler {

    public static void datBaoThuc(Context context, BaoThuc baoThuc) {
        if (baoThuc.getBat() == 0) return;

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Calendar now = Calendar.getInstance();

        int[] ngayTrongTuan = {baoThuc.getT2(), baoThuc.getT3(), baoThuc.getT4(),
                baoThuc.getT5(), baoThuc.getT6(), baoThuc.getT7(), baoThuc.getCn()};

        // Kiểm tra xem có ngày nào được chọn không
        boolean isAnyDaySelected = false;
        for (int val : ngayTrongTuan) {
            if (val == 1) {
                isAnyDaySelected = true;
                break;
            }
        }

        // Nếu không chọn ngày nào, đặt báo thức hôm nay
        if (!isAnyDaySelected) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, baoThuc.getH());
            calendar.set(Calendar.MINUTE, baoThuc.getM());
            calendar.set(Calendar.SECOND, 0);

            // Nếu giờ phút đã qua, đặt cho ngày mai
            if (calendar.getTimeInMillis() <= now.getTimeInMillis()) {
                calendar.add(Calendar.DAY_OF_MONTH, 1);
            }

            setAlarm(context, alarmManager, calendar, baoThuc, 0);
            return;
        }

        // Nếu có chọn ngày, đặt báo thức theo từng ngày
        for (int i = 0; i < 7; i++) {
            if (ngayTrongTuan[i] == 0) continue;

            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, baoThuc.getH());
            calendar.set(Calendar.MINUTE, baoThuc.getM());
            calendar.set(Calendar.SECOND, 0);

            int thu = (i == 6) ? 1 : i + 2; // CN = 1
            int currentThu = calendar.get(Calendar.DAY_OF_WEEK);
            int deltaDays = thu - currentThu;
            if (deltaDays < 0 || (deltaDays == 0 && calendar.getTimeInMillis() <= now.getTimeInMillis())) {
                deltaDays += 7;
            }
            calendar.add(Calendar.DAY_OF_MONTH, deltaDays);

            setAlarm(context, alarmManager, calendar, baoThuc, i);
        }
    }

    private static void setAlarm(Context context, AlarmManager alarmManager, Calendar calendar, BaoThuc baoThuc, int index) {
        Intent intent = new Intent(context, BaoThucReceiver.class);
        intent.putExtra("id", baoThuc.getId());
        intent.putExtra("ringtoneUri", baoThuc.getRingtoneUri());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                baoThuc.getId() * 10 + index,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        if (alarmManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        calendar.getTimeInMillis(),
                        pendingIntent
                );
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                alarmManager.setExact(
                        AlarmManager.RTC_WAKEUP,
                        calendar.getTimeInMillis(),
                        pendingIntent
                );
            } else {
                alarmManager.set(
                        AlarmManager.RTC_WAKEUP,
                        calendar.getTimeInMillis(),
                        pendingIntent
                );
            }
        }
    }

    public static void huyBaoThuc(Context context, BaoThuc baoThuc) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        for (int i = 0; i < 7; i++) {
            Intent intent = new Intent(context, BaoThucReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    context,
                    baoThuc.getId() * 10 + i,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );

            if (alarmManager != null) alarmManager.cancel(pendingIntent);
        }

        // Dừng nhạc nếu đang phát
        Intent serviceIntent = new Intent(context, AlarmService.class);
        serviceIntent.putExtra("id", baoThuc.getId());
        context.stopService(serviceIntent);
    }

    public static void huyTatCaBaoThuc(Context context) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        for (BaoThuc baoThuc : dbHelper.getAllBaoThuc()) {
            huyBaoThuc(context, baoThuc);
        }
    }
}
