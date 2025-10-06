package com.example.apptg;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.TimePicker;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ThemBaoThuc extends AppCompatActivity {

    TimePicker timePicker;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thembaothuc);
        anhxa();
        timePicker.setIs24HourView(true); // đặt chế độ 24h ở đây

    }
    private void anhxa(){
        timePicker.findViewById(R.id.timePicker);
    }
}