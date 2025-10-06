package com.example.apptg;

import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import javaclass.AlarmScheduler;
import item.BaoThuc;
import dao.BaoThucDao;
import Database.AppDatabase;
import javaclass.AlarmService;

import java.util.Calendar;

public class BaoThucActivity extends AppCompatActivity {

    private TextView tvTimeAlarm;
    private Button btnStop, btnSnooze;
    private Ringtone ringtone;
    private Vibrator vibrator;
    private BaoThuc baoThuc;
    private BaoThucDao baoThucDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        turnScreenOnAndUnlock();
        setContentView(R.layout.activity_baothuc_activity);

        tvTimeAlarm = findViewById(R.id.tvTimeAlarm);
        btnStop = findViewById(R.id.btnStop);
        btnSnooze = findViewById(R.id.btnSnooze);

        baoThucDao = AppDatabase.getInstance(this).baoThucDao();

        int alarmId = getIntent().getIntExtra("id",-1);
        if(alarmId != -1) {
            new Thread(() -> {
                baoThuc = baoThucDao.getById(alarmId);
                runOnUiThread(() -> {
                    if (baoThuc != null) {
                        tvTimeAlarm.setText(baoThuc.getTimeString());
                        // Nhạc + rung sẽ do Service phát, Activity chỉ hiển thị
                    }
                });
            }).start();
        }

        btnStop.setOnClickListener(v -> stopAlarm());
        btnSnooze.setOnClickListener(v -> snoozeAlarm());
    }

    private void turnScreenOnAndUnlock() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true);
            setTurnScreenOn(true);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        } else {
            getWindow().addFlags(
                    WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                            WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                            WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
            );
        }
    }

    private void stopAlarm() {
        // Dừng Service để nhạc và rung tắt
        stopService(new Intent(this, AlarmService.class));

        if(baoThuc != null){
            // Nếu báo thức hôm nay, tắt hoàn toàn
            if(baoThuc.isOneTimeToday()){
                baoThuc.setBat(0);
            }

            new Thread(() -> {
                baoThucDao.update(baoThuc);
                AlarmScheduler.huyBaoThuc(this, baoThuc);
            }).start();
        }
        finish();
    }


    private void snoozeAlarm() {
        // Dừng Service
        Intent serviceIntent = new Intent(this, AlarmService.class);
        stopService(serviceIntent);

        if (baoThuc != null) {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.MINUTE, 5);
            baoThuc.setH(cal.get(Calendar.HOUR_OF_DAY));
            baoThuc.setM(cal.get(Calendar.MINUTE));
            AlarmScheduler.datBaoThuc(this, baoThuc);
        }
        finish();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (ringtone != null && ringtone.isPlaying()) ringtone.stop();
        if (vibrator != null) vibrator.cancel();
    }
}
