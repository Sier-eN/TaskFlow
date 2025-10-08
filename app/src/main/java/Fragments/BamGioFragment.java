package Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.apptg.R;

import java.util.ArrayList;
import java.util.List;

public class BamGioFragment extends Fragment {

    private static final String PREFS_NAME = "stopwatch_prefs";

    private TextView tvTimer;
    private Button btnStartPause, btnLap, btnReset;
    private LinearLayout layoutLapTimes;

    private boolean isRunning = false;
    private long startTime = 0L;
    private long elapsedTime = 0L;
    private int lapCount = 0;
    private Handler handler = new Handler();

    private final List<Long> lapTimes = new ArrayList<>();

    private Runnable updateTimer = new Runnable() {
        @Override
        public void run() {
            long now = System.currentTimeMillis();
            long diff = now - startTime;

            int minutes = (int) (diff / 1000) / 60;
            int seconds = (int) (diff / 1000) % 60;
            int millis = (int) (diff % 1000) / 10;

            tvTimer.setText(String.format("%02d:%02d.%02d", minutes, seconds, millis));
            handler.postDelayed(this, 10);
        }
    };

    public BamGioFragment() {
        // Bắt buộc constructor rỗng
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_bamgio, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvTimer = view.findViewById(R.id.tvTimer);
        btnStartPause = view.findViewById(R.id.btnStartPause);
        btnLap = view.findViewById(R.id.btnLap);
        btnReset = view.findViewById(R.id.btnReset);
        layoutLapTimes = view.findViewById(R.id.layoutLapTimes);

        // Load dữ liệu từ SharedPreferences
        loadData();

        btnStartPause.setOnClickListener(v -> {
            if (!isRunning) {
                startTime = System.currentTimeMillis() - elapsedTime;
                handler.post(updateTimer);
                isRunning = true;
                btnStartPause.setText("Tạm dừng");
                btnLap.setVisibility(View.VISIBLE);
            } else {
                handler.removeCallbacks(updateTimer);
                elapsedTime = System.currentTimeMillis() - startTime;
                isRunning = false;
                btnStartPause.setText("Tiếp tục");
                btnLap.setVisibility(View.GONE);
                saveData(); // Lưu khi tạm dừng
            }
        });

        btnLap.setOnClickListener(v -> addLapTime());

        btnReset.setOnClickListener(v -> resetAll());
    }

    private void addLapTime() {
        long now = System.currentTimeMillis();
        long diff = now - startTime;

        lapCount++;
        lapTimes.add(diff);

        int minutes = (int) (diff / 1000) / 60;
        int seconds = (int) (diff / 1000) % 60;
        int millis = (int) (diff % 1000) / 10;

        TextView tvLap = new TextView(getContext());
        tvLap.setText(String.format("Lần %d: %02d:%02d.%02d", lapCount, minutes, seconds, millis));
        tvLap.setTextColor(ContextCompat.getColor(requireContext(), R.color.mautrangnhathon));
        tvLap.setTextSize(18);
        tvLap.setPadding(0, 8, 0, 8);

        TranslateAnimation slide = new TranslateAnimation(300, 0, 0, 0);
        slide.setDuration(300);
        AlphaAnimation fade = new AlphaAnimation(0f, 1f);
        fade.setDuration(300);
        tvLap.startAnimation(slide);
        tvLap.startAnimation(fade);

        layoutLapTimes.addView(tvLap, 0);

        highlightExtremes();

        saveData(); // Lưu dữ liệu sau mỗi lap
    }

    private void highlightExtremes() {
        if (lapTimes.isEmpty()) return;

        long min = lapTimes.get(0);
        long max = lapTimes.get(0);
        int minIndex = 0, maxIndex = 0;

        for (int i = 1; i < lapTimes.size(); i++) {
            if (lapTimes.get(i) < min) {
                min = lapTimes.get(i);
                minIndex = i;
            }
            if (lapTimes.get(i) > max) {
                max = lapTimes.get(i);
                maxIndex = i;
            }
        }

        for (int i = 0; i < layoutLapTimes.getChildCount(); i++) {
            TextView tv = (TextView) layoutLapTimes.getChildAt(i);
            tv.setTextColor(ContextCompat.getColor(requireContext(), R.color.mautrangnhathon));
        }

        int visibleMin = layoutLapTimes.getChildCount() - 1 - minIndex;
        int visibleMax = layoutLapTimes.getChildCount() - 1 - maxIndex;

        if (visibleMin >= 0 && visibleMin < layoutLapTimes.getChildCount()) {
            TextView tvMin = (TextView) layoutLapTimes.getChildAt(visibleMin);
            tvMin.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.holo_green_light));
        }

        if (visibleMax >= 0 && visibleMax < layoutLapTimes.getChildCount()) {
            TextView tvMax = (TextView) layoutLapTimes.getChildAt(visibleMax);
            tvMax.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.holo_red_light));
        }
    }

    private void resetAll() {
        handler.removeCallbacks(updateTimer);
        isRunning = false;
        elapsedTime = 0;
        lapCount = 0;
        lapTimes.clear();
        tvTimer.setText("00:00.00");
        layoutLapTimes.removeAllViews();
        btnStartPause.setText("Bắt đầu");
        btnLap.setVisibility(View.GONE);
        saveData(); // Xóa dữ liệu trong SharedPreferences
    }

    // ===== LƯU DỮ LIỆU VÀ LOAD DỮ LIỆU =====

    private void saveData() {
        SharedPreferences prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong("elapsedTime", isRunning ? System.currentTimeMillis() - startTime : elapsedTime);
        editor.putInt("lapCount", lapCount);

        // Lưu danh sách lap dưới dạng CSV
        StringBuilder sb = new StringBuilder();
        for (Long lap : lapTimes) {
            sb.append(lap).append(",");
        }
        editor.putString("lapTimes", sb.toString());
        editor.apply();
    }

    private void loadData() {
        SharedPreferences prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        elapsedTime = prefs.getLong("elapsedTime", 0L);
        lapCount = prefs.getInt("lapCount", 0);
        String lapStr = prefs.getString("lapTimes", "");

        lapTimes.clear();
        layoutLapTimes.removeAllViews();

        if (!lapStr.isEmpty()) {
            String[] parts = lapStr.split(",");
            for (String s : parts) {
                if (!s.isEmpty()) {
                    lapTimes.add(Long.parseLong(s));
                }
            }
        }

        // Hiển thị các lap cũ
        for (int i = 0; i < lapTimes.size(); i++) {
            long diff = lapTimes.get(i);
            int minutes = (int) (diff / 1000) / 60;
            int seconds = (int) (diff / 1000) % 60;
            int millis = (int) (diff % 1000) / 10;

            TextView tvLap = new TextView(getContext());
            tvLap.setText(String.format("Lần %d: %02d:%02d.%02d", i + 1, minutes, seconds, millis));
            tvLap.setTextColor(ContextCompat.getColor(requireContext(), R.color.mautrangnhathon));
            tvLap.setTextSize(18);
            tvLap.setPadding(0, 8, 0, 8);

            layoutLapTimes.addView(tvLap, 0);
        }

        highlightExtremes();

        // Hiển thị timer hiện tại
        int minutes = (int) (elapsedTime / 1000) / 60;
        int seconds = (int) (elapsedTime / 1000) % 60;
        int millis = (int) (elapsedTime % 1000) / 10;
        tvTimer.setText(String.format("%02d:%02d.%02d", minutes, seconds, millis));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacks(updateTimer);
        saveData(); // lưu khi thoát fragment
    }
}
