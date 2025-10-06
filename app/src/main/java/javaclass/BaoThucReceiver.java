package javaclass;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BaoThucReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        int alarmId = intent.getIntExtra("id", -1);
        if (alarmId == -1) return;

        Intent serviceIntent = new Intent(context, AlarmService.class);
        serviceIntent.putExtra("id", alarmId);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            context.startForegroundService(serviceIntent);
        } else {
            context.startService(serviceIntent);
        }
    }
}
