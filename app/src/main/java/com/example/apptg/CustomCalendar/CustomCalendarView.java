package com.example.apptg.CustomCalendar;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.example.apptg.R;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * CustomCalendarView - phiên bản ổn định:
 * - Luôn render 7 x 6 = 42 ô (đều nhau)
 * - Đợi GridLayout đo xong (post) trước khi tính kích thước ô
 * - Hiển thị số luôn, màu chữ mặc định = @color/mautrangnhathon
 * - Chỉ click mở callback khi ngày nằm trong eventDays
 */
public class CustomCalendarView extends LinearLayout {
    private static final String TAG = "CustomCalendarView";

    private TextView tvThangHienTai;
    private ImageView ivThangTruoc, ivThangSau;
    private GridLayout gridCalendarDays;

    private LocalDate thangHienTai;
    private Set<LocalDate> selectedDate;

    private OnDateSelectedListener dateSelectedListener;

    private final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final DateTimeFormatter monthFormat = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.getDefault());

    // Map lưu màu cho từng ngày
    private final Map<LocalDate, Integer> dayBgColors = new HashMap<>();
    private final Map<LocalDate, Integer> dayTextColors = new HashMap<>();

    // Danh sách ngày có sự kiện
    private final Set<LocalDate> eventDays = new HashSet<>();

    public CustomCalendarView(Context context) {
        super(context);
        init(context);
    }

    public CustomCalendarView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CustomCalendarView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.custom_calendar_view, this, true);

        tvThangHienTai = findViewById(R.id.thangnam);
        ivThangTruoc = findViewById(R.id.thangtruoc);
        ivThangSau = findViewById(R.id.thangsau);
        gridCalendarDays = findViewById(R.id.grid_calendar_day);

        thangHienTai = LocalDate.now().withDayOfMonth(1);

        // mặc định chọn hôm nay
        selectedDate = new HashSet<>();
        selectedDate.add(LocalDate.now());

        ivThangTruoc.setOnClickListener(v -> {
            thangHienTai = thangHienTai.minusMonths(1);
            setupCalendar(); // sẽ tự post nếu cần
        });
        ivThangSau.setOnClickListener(v -> {
            thangHienTai = thangHienTai.plusMonths(1);
            setupCalendar();
        });

        // lần đầu thiết lập
        setupCalendar();
    }

    private void setupCalendar() {
        if (gridCalendarDays.getWidth() == 0) {
            gridCalendarDays.post(this::setupCalendar);
            return;
        }

        gridCalendarDays.removeAllViews();
        tvThangHienTai.setText(thangHienTai.format(monthFormat));
        gridCalendarDays.setColumnCount(7);

        final int totalCells = 42;
        final int firstDayOfWeek = thangHienTai.getDayOfWeek().getValue() % 7;
        final int daysInMonth = thangHienTai.lengthOfMonth();

        int margin = dpToPx(2);
        int totalMargin = margin * 2 * 7; // 2 bên mỗi ô x 7 cột
        int cellWidth = (gridCalendarDays.getWidth() - totalMargin) / 7;
        int cellHeight = dpToPx(48);

        LocalDate today = LocalDate.now();

        for (int i = 0; i < totalCells; i++) {
            TextView tv = new TextView(getContext(), null, 0, R.style.CalendarDate);
            tv.setGravity(Gravity.CENTER);
            tv.setIncludeFontPadding(false);

            GridLayout.LayoutParams lp = new GridLayout.LayoutParams();
            lp.width = cellWidth;
            lp.height = cellHeight;
            lp.rowSpec = GridLayout.spec(i / 7);
            lp.columnSpec = GridLayout.spec(i % 7);
            lp.setMargins(margin, margin, margin, margin);
            tv.setLayoutParams(lp);

            if (i >= firstDayOfWeek && i < firstDayOfWeek + daysInMonth) {
                int day = i - firstDayOfWeek + 1;
                final LocalDate date = thangHienTai.withDayOfMonth(day);
                tv.setText(String.valueOf(day));
                tv.setTag(date);

                int defaultTextColor = ContextCompat.getColor(getContext(), R.color.mautrangnhathon);

                // 1. Ngày có màu custom từ setDayColor()
                if (dayBgColors.containsKey(date)) {
                    tv.setBackgroundColor(dayBgColors.get(date));
                    tv.setTextColor(dayTextColors.getOrDefault(date, defaultTextColor));
                }
                // 2. Ngày hôm nay (nếu chưa có màu custom)
                else if (date.equals(today)) {
                    tv.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.bg_today));
                    tv.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
                }
                // 3. Ngày bình thường
                else {
                    tv.setBackgroundColor(0x00000000);
                    tv.setTextColor(defaultTextColor);
                }

                // Click chỉ cho ngày có sự kiện
                tv.setOnClickListener(v -> {
                    LocalDate clicked = (LocalDate) v.getTag();
                    if (!eventDays.contains(clicked)) return;

                    selectedDate.clear();
                    selectedDate.add(clicked);
                    setupCalendar();

                    if (dateSelectedListener != null) {
                        ArrayList<String> list = new ArrayList<>();
                        list.add(clicked.format(dateFormat));
                        dateSelectedListener.onDateSelected(list);
                    }
                });

            } else {
                // ô trống
                tv.setText("");
                tv.setBackgroundColor(0x00000000);
                tv.setClickable(false);
            }

            gridCalendarDays.addView(tv);
        }
    }


    private int dpToPx(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }

    // ---------- Public API ----------

    public void setSelectedDate(ArrayList<String> dates) {
        selectedDate.clear();
        for (String dateStr : dates) {
            try {
                selectedDate.add(LocalDate.parse(dateStr, dateFormat));
            } catch (Exception e) {
                Log.e(TAG, "setSelectedDate: parse error " + dateStr);
            }
        }
        setupCalendar();
    }

    public ArrayList<String> getSelectedDates() {
        ArrayList<String> dates = new ArrayList<>();
        for (LocalDate date : selectedDate) {
            dates.add(date.format(dateFormat));
        }
        return dates;
    }

    public void setDateSelectedListener(OnDateSelectedListener listener) {
        this.dateSelectedListener = listener;
    }

    public void setDayColor(String dateIso, int bgColor, int textColor) {
        try {
            LocalDate date = LocalDate.parse(dateIso, dateFormat);
            dayBgColors.put(date, bgColor);
            dayTextColors.put(date, textColor);
            setupCalendar();
        } catch (Exception e) {
            Log.e(TAG, "setDayColor error " + dateIso);
        }
    }

    public void clearAllMarks() {
        dayBgColors.clear();
        dayTextColors.clear();
        setupCalendar();
    }

    public void setEventDays(ArrayList<String> events) {
        eventDays.clear();
        for (String dateStr : events) {
            try {
                eventDays.add(LocalDate.parse(dateStr, dateFormat));
            } catch (Exception e) {
                Log.e(TAG, "setEventDays parse error " + dateStr);
            }
        }
        setupCalendar();
    }
}
