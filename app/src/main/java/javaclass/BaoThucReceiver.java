package javaclass;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import Database.DatabaseHelper;
import item.BaoThuc;

public class BaoThucReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        int alarmId = intent.getIntExtra("id", -1);
        if (alarmId == -1) return;

        DatabaseHelper dbHelper = new DatabaseHelper(context);
        BaoThuc baoThuc = dbHelper.getBaoThucById(alarmId);
        if (baoThuc == null || baoThuc.getBat() == 0) return;

        // Lấy URI nhạc chuông từ báo thức
        String ringtoneUri = baoThuc.getRingtoneUri();

        // Bật AlarmService để chơi nhạc + rung + notification
        Intent serviceIntent = new Intent(context, AlarmService.class);
        serviceIntent.putExtra("id", alarmId);
        serviceIntent.putExtra("ringtoneUri", ringtoneUri); // thêm URI nhạc chuông

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            context.startForegroundService(serviceIntent);
        } else {
            context.startService(serviceIntent);
        }
    }
}