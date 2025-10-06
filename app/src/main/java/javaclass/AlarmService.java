package javaclass;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.os.VibrationEffect;
import android.os.Vibrator;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.apptg.BaoThucActivity;
import com.example.apptg.R;

public class AlarmService extends Service {

    private Ringtone ringtone;
    private static final String CHANNEL_ID = "alarm_channel";

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
    }

    @SuppressLint("ForegroundServiceType")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int alarmId = intent.getIntExtra("id", 0);
        String ringtoneUriString = intent.getStringExtra("ringtoneUri");

        Uri alarmUri = null;
        if (ringtoneUriString != null && !ringtoneUriString.isEmpty()) {
            alarmUri = Uri.parse(ringtoneUriString);
        }
        if (alarmUri == null) {
            alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            if (alarmUri == null) alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        }

        try {
            ringtone = RingtoneManager.getRingtone(this, alarmUri);
            if (ringtone != null) ringtone.play();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        if (vibrator != null) {
            long[] pattern = {0, 500, 500, 500};
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createWaveform(pattern, 0));
            } else {
                vibrator.vibrate(pattern, 0);
            }
        }

        Intent activityIntent = new Intent(this, BaoThucActivity.class);
        activityIntent.putExtra("id", alarmId);
        activityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, alarmId, activityIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.alarm)
                .setContentTitle("Báo Thức")
                .setContentText("Đến giờ dậy!")
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL);

        startForeground(alarmId, builder.build());

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (ringtone != null && ringtone.isPlaying()) {
            ringtone.stop();
        }
        ringtone = null;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Alarm Channel",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Kênh báo thức");
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) manager.createNotificationChannel(channel);
        }
    }
}