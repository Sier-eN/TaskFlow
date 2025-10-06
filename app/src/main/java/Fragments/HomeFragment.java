package Fragments;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.apptg.CustomCalendar.CustomCalendarView;
import com.example.apptg.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import Adapter.EventAdapter;
import Database.DatabaseHelper;
import javaclass.EventAlarmReceiver;
import javaclass.EventAlarmScheduler;
import item.EventItem;
import javaclass.AddEditEventBottomSheet;

public class HomeFragment extends Fragment {

    private RecyclerView rvEvents;
    private EventAdapter adapter;
    private DatabaseHelper db;
    private List<EventItem> eventList = new ArrayList<>();
    private View imgAdd;
    private CustomCalendarView calendarView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);

        db = new DatabaseHelper(requireContext());
        rvEvents = v.findViewById(R.id.rv_events);
        imgAdd = v.findViewById(R.id.img_add);
        calendarView = v.findViewById(R.id.custom_calendar_view);

        // Adapter với click listener
        adapter = new EventAdapter(item -> {
            AddEditEventBottomSheet b = new AddEditEventBottomSheet(this::refreshEventsAndCalendar);
            b.setEditingEvent(item);
            b.show(getParentFragmentManager(), "edit_event");
        });

        rvEvents.setLayoutManager(
                new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        );
        rvEvents.setAdapter(adapter);

        // Nút thêm mới
        imgAdd.setOnClickListener(xx -> {
            AddEditEventBottomSheet b = new AddEditEventBottomSheet(this::refreshEventsAndCalendar);
            b.show(getParentFragmentManager(), "add_event");
        });

        // Click vào ngày trên calendar
        calendarView.setDateSelectedListener(selectedDates -> {
            if (selectedDates == null || selectedDates.isEmpty()) return;
            String dateIso = selectedDates.get(0);
            List<EventItem> events = db.getEventsByDate(dateIso);
            if (events == null || events.isEmpty()) return;

            AddEditEventBottomSheet b = new AddEditEventBottomSheet(this::refreshEventsAndCalendar);
            b.setEditingEvent(events.get(0));
            b.show(getParentFragmentManager(), "event_sheet");
        });

        refreshEventsAndCalendar();
    }

    /**
     * Hàm load event từ DB và cập nhật cả RecyclerView + Calendar
     */
    private void refreshEventsAndCalendar() {
        eventList = db.getAllEvents();
        adapter.submitList(new ArrayList<>(eventList)); // copy để DiffUtil so sánh được

        // Clear calendar cũ
        calendarView.clearAllMarks();
        ArrayList<String> eventDates = new ArrayList<>();
        for (EventItem e : eventList) {
            eventDates.add(e.getDateIso());

            int bgColor = safeParseColor(e.getColorHex());
            int textColor = isColorDark(bgColor)
                    ? requireContext().getColor(R.color.mautrangnhathon)
                    : requireContext().getColor(R.color.maunensanghon);

            calendarView.setDayColor(e.getDateIso(), bgColor, textColor);
        }
        calendarView.setEventDays(eventDates);

        // Đặt lại alarm
        for (EventItem e : eventList) {
            EventAlarmScheduler.scheduleEventAlarm(requireContext(), e);
        }
    }

    private int safeParseColor(String hex) {
        try {
            return Color.parseColor(hex);
        } catch (Exception e) {
            return Color.GRAY;
        }
    }

    private boolean isColorDark(int color) {
        double r = Color.red(color) / 255.0;
        double g = Color.green(color) / 255.0;
        double b = Color.blue(color) / 255.0;
        double luminance = 0.2126 * r + 0.7152 * g + 0.0722 * b;
        return luminance < 0.5;
    }

}
