
package com.example.apptg;

import android.Manifest;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.Calendar;

import Database.DatabaseHelper;
import item.BaoThuc;
import javaclass.AlarmScheduler;
import javaclass.AlarmService;

public class BaoThucActivity extends AppCompatActivity {

    private TextView tvTimeAlarm;
    private Button btnStop, btnSnooze;
    private Ringtone ringtone;
    private BaoThuc baoThuc;
    private Vibrator vibrator;
    private static final int PERMISSION_REQUEST_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        turnScreenOnAndUnlock();
        setContentView(R.layout.activity_baothuc_activity);

        tvTimeAlarm = findViewById(R.id.tvTimeAlarm);
        btnStop = findViewById(R.id.btnStop);
        btnSnooze = findViewById(R.id.btnSnooze);

        int alarmId = getIntent().getIntExtra("id", -1);
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        baoThuc = dbHelper.getBaoThucById(alarmId);

        if (baoThuc != null) tvTimeAlarm.setText(baoThuc.getTimeString());

        // Yêu cầu quyền thông báo nếu Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        PERMISSION_REQUEST_CODE);
            }
        }

        vibratePhone();

        btnStop.setOnClickListener(v -> stopAlarm());
        btnSnooze.setOnClickListener(v -> snoozeAlarm());
    }

    private void turnScreenOnAndUnlock() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true);
            setTurnScreenOn(true);
            KeyguardManager km = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
            if (km != null) km.requestDismissKeyguard(this, null);
        } else {
            getWindow().addFlags(
                    WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                            WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                            WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                            WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
            );
        }
    }

    private void playAlarmSound() {
        Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        if (alarmUri == null) alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        ringtone = RingtoneManager.getRingtone(this, alarmUri);
        if (ringtone != null) ringtone.play();
    }

    private void vibratePhone() {
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        if (vibrator != null) {
            long[] pattern = {0, 500, 500, 500};
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createWaveform(pattern, 0)); // 0 = lặp vô hạn
            } else {
                vibrator.vibrate(pattern, 0);
            }
        }
    }


    private void stopAlarm() {
        if (ringtone != null && ringtone.isPlaying()) ringtone.stop();
        if (vibrator != null) vibrator.cancel();

        stopService(new Intent(this, AlarmService.class));

        if (baoThuc != null) {
            // Kiểm tra nếu báo thức là "Hôm nay" (không lặp)
            if (baoThuc.getT2() == 0 && baoThuc.getT3() == 0 && baoThuc.getT4() == 0 &&
                    baoThuc.getT5() == 0 && baoThuc.getT6() == 0 && baoThuc.getT7() == 0 &&
                    baoThuc.getCn() == 0) {

                // Set tắt báo thức
                baoThuc.setBat(0);

                // Cập nhật database
                DatabaseHelper dbHelper = new DatabaseHelper(this);
                dbHelper.updateBaoThuc(baoThuc);

                // Hủy báo thức thực tế
                javaclass.AlarmCanceler.huyBaoThuc(this, baoThuc);

                // Gửi thông báo tới fragment để refresh switch
                getSupportFragmentManager().setFragmentResult("refresh_baothuc", new Bundle());
            } else {
                // Báo thức lặp -> chỉ hủy báo thức hôm nay
                javaclass.AlarmCanceler.huyBaoThuc(this, baoThuc);
            }
        }

        finish();
    }




    private void snoozeAlarm() {
        // Dừng chuông & rung
        if (ringtone != null && ringtone.isPlaying()) ringtone.stop();
        if (vibrator != null) vibrator.cancel();

        stopService(new Intent(this, AlarmService.class));

        if (baoThuc != null) {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.MINUTE, 5); // snooze 5 phút

            // Chỉ đặt báo thức lại trong hôm nay nếu không lặp
            if (baoThuc.getT2() == 0 && baoThuc.getT3() == 0 && baoThuc.getT4() == 0 &&
                    baoThuc.getT5() == 0 && baoThuc.getT6() == 0 && baoThuc.getT7() == 0 &&
                    baoThuc.getCn() == 0) {

                // Lưu giờ và phút tạm thời cho snooze
                BaoThuc snoozeBaoThuc = new BaoThuc(
                        baoThuc.getH(), baoThuc.getM(),
                        baoThuc.getT2(), baoThuc.getT3(), baoThuc.getT4(), baoThuc.getT5(),
                        baoThuc.getT6(), baoThuc.getT7(), baoThuc.getCn(),
                        baoThuc.getBat()
                );
                snoozeBaoThuc.setId(baoThuc.getId());
                snoozeBaoThuc.setH(cal.get(Calendar.HOUR_OF_DAY));
                snoozeBaoThuc.setM(cal.get(Calendar.MINUTE));
                snoozeBaoThuc.setRingtoneUri(baoThuc.getRingtoneUri());

                AlarmScheduler.datBaoThuc(this, snoozeBaoThuc);
            } else {
                // Báo thức lặp -> snooze bình thường
                baoThuc.setH(cal.get(Calendar.HOUR_OF_DAY));
                baoThuc.setM(cal.get(Calendar.MINUTE));
                AlarmScheduler.datBaoThuc(this, baoThuc);
            }
        }

        finish();
    }




    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (ringtone != null && ringtone.isPlaying()) ringtone.stop();
        if (vibrator != null) vibrator.cancel(); // <-- Hủy rung
    }
}
