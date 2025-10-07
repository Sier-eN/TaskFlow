package Fragments;

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
        // Bắt buộc để Android không báo lỗi
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

        btnStartPause.setOnClickListener(v -> {
            if (!isRunning) {
                // Bắt đầu hoặc tiếp tục
                startTime = System.currentTimeMillis() - elapsedTime;
                handler.post(updateTimer);
                isRunning = true;
                btnStartPause.setText("Tạm dừng");
                btnLap.setVisibility(View.VISIBLE);
            } else {
                // Tạm dừng
                handler.removeCallbacks(updateTimer);
                elapsedTime = System.currentTimeMillis() - startTime;
                isRunning = false;
                btnStartPause.setText("Tiếp tục");
                btnLap.setVisibility(View.GONE);
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

        // Hiệu ứng trượt + mờ dần khi thêm
        TranslateAnimation slide = new TranslateAnimation(300, 0, 0, 0);
        slide.setDuration(300);
        AlphaAnimation fade = new AlphaAnimation(0f, 1f);
        fade.setDuration(300);
        tvLap.startAnimation(slide);
        tvLap.startAnimation(fade);

        layoutLapTimes.addView(tvLap, 0);

        // Cập nhật highlight nhanh nhất / chậm nhất
        highlightExtremes();
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

        // Reset màu tất cả
        for (int i = 0; i < layoutLapTimes.getChildCount(); i++) {
            TextView tv = (TextView) layoutLapTimes.getChildAt(i);
            tv.setTextColor(ContextCompat.getColor(requireContext(), R.color.mautrangnhathon));
        }

        // Vì lap mới nhất thêm ở vị trí 0 -> cần đảo index lại
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
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacks(updateTimer);
    }
}
