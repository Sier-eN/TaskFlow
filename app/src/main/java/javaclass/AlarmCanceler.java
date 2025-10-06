package javaclass;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.util.List;

import Database.DatabaseHelper;
import item.BaoThuc;

public class AlarmCanceler {

    public static void huyTatCaBaoThuc(Context context) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        List<BaoThuc> danhSach = dbHelper.getAllBaoThuc();

        for (BaoThuc baoThuc : danhSach) {
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
}
