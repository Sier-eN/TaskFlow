package javaclass;

import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.view.*;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.apptg.R;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import Adapter.ColorAdapter;
import Database.DatabaseHelper;
import javaclass.EventAlarmScheduler;
import item.EventItem;

public class AddEditEventBottomSheet extends BottomSheetDialogFragment {

    private EditText etTitle;
    private TextView tvDate;
    private ImageView imgSave, imgDelete;
    private RecyclerView rvColors;
    private ColorAdapter colorAdapter;
    private List<String> colorList;
    private String selectedColor = "#FF0000";
    private String selectedDateIso = "";
    private DatabaseHelper db;
    private EventItem editingEvent; // nếu null => insert

    public interface OnChangeListener {
        void onChanged(); // gọi để HomeFragment reload dữ liệu
    }
    private OnChangeListener changeListener;

    public AddEditEventBottomSheet(OnChangeListener l) { this.changeListener = l; }
    public void setEditingEvent(EventItem e) { this.editingEvent = e; }

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_bottom_sheet_event, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        View view = getView();
        if (view == null) return;
        View parent = (View) view.getParent();
        parent.getLayoutParams().height = (int) (getScreenHeight() * 0.80);
        BottomSheetBehavior.from(parent).setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    private int getScreenHeight() {
        WindowManager wm = requireActivity().getWindowManager();
        Point size = new Point();
        wm.getDefaultDisplay().getSize(size);
        return size.y;
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);
        db = new DatabaseHelper(requireContext());
        etTitle = v.findViewById(R.id.et_title);
        tvDate = v.findViewById(R.id.tv_date);
        imgSave = v.findViewById(R.id.img_save);
        imgDelete = v.findViewById(R.id.img_delete);
        rvColors = v.findViewById(R.id.rv_colors);

        // Prepare color list
        colorList = new ArrayList<>();
        colorList.add("#F44336"); // red
        colorList.add("#E91E63"); // pink
        colorList.add("#9C27B0"); // purple
        colorList.add("#3F51B5"); // indigo
        colorList.add("#2196F3"); // blue
        colorList.add("#03A9F4"); // light blue
        colorList.add("#009688"); // teal
        colorList.add("#4CAF50"); // green
        colorList.add("#FF9800"); // orange
        colorList.add("#FFC107"); // amber
        colorList.add("#795548"); // brown
        colorList.add("#607D8B"); // blue grey

        colorAdapter = new ColorAdapter(requireContext(), colorList, (hex, pos) -> selectedColor = hex);
        rvColors.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        rvColors.setAdapter(colorAdapter);

        // Date picker
        tvDate.setOnClickListener(vv -> showDatePicker());
        v.findViewById(R.id.btn_pick_date).setOnClickListener(vv -> showDatePicker());

        // Load event data nếu edit
        if (editingEvent != null) {
            etTitle.setText(editingEvent.getTitle());
            selectedDateIso = editingEvent.getDateIso();
            tvDate.setText(selectedDateIso);
            selectedColor = editingEvent.getColorHex();
            int pos = colorList.indexOf(selectedColor);
            if (pos >= 0) colorAdapter.setSelectedPos(pos);
            imgDelete.setVisibility(View.VISIBLE);
        } else {
            imgDelete.setVisibility(View.GONE);
        }

        imgSave.setOnClickListener(vv -> saveEvent());
        imgDelete.setOnClickListener(vv -> deleteEvent());
    }

    private void showDatePicker() {
        final Calendar c = Calendar.getInstance();
        int y = c.get(Calendar.YEAR);
        int m = c.get(Calendar.MONTH);
        int d = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog dp = new DatePickerDialog(requireContext(), (view, year, month, dayOfMonth) -> {
            String mm = (month + 1) < 10 ? "0" + (month + 1) : String.valueOf(month + 1);
            String dd = dayOfMonth < 10 ? "0" + dayOfMonth : String.valueOf(dayOfMonth);
            selectedDateIso = String.format("%04d-%s-%s", year, mm, dd);
            tvDate.setText(selectedDateIso);
        }, y, m, d);
        dp.show();
    }

    private void saveEvent() {
        String title = etTitle.getText().toString().trim();
        if (title.isEmpty()) { etTitle.setError("Nhập tên"); return; }
        if (selectedDateIso.isEmpty()) { tvDate.setError("Chọn ngày"); return; }

        if (editingEvent == null) {
            // Insert mới
            EventItem e = new EventItem(title, selectedDateIso, selectedColor);
            long id = db.insertEvent(e);
            e.setId((int) id); // gán id
            scheduleEventNotification(e);
        } else {
            // Update
            editingEvent.setTitle(title);
            editingEvent.setDateIso(selectedDateIso);
            editingEvent.setColorHex(selectedColor);
            db.updateEvent(editingEvent);
            scheduleEventNotification(editingEvent);
        }

        if (changeListener != null) changeListener.onChanged();
        dismiss();
    }

    private void deleteEvent() {
        if (editingEvent != null) {
            db.deleteEvent(editingEvent.getId());
            cancelEventNotification(editingEvent);
            if (changeListener != null) changeListener.onChanged();
        }
        dismiss();
    }

    // ---------------- Notification logic ----------------
    private void scheduleEventNotification(EventItem e) {
        Calendar cal = Calendar.getInstance();
        String[] parts = e.getDateIso().split("-");
        cal.set(Calendar.YEAR, Integer.parseInt(parts[0]));
        cal.set(Calendar.MONTH, Integer.parseInt(parts[1]) - 1);
        cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(parts[2]));
        cal.set(Calendar.HOUR_OF_DAY, 6);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);

        long triggerTime = cal.getTimeInMillis();

        Intent intent = new Intent(requireContext(), EventAlarmReceiver.class);
        intent.putExtra("event_title", e.getTitle());
        intent.putExtra("event_id", e.getId());

        PendingIntent pi = PendingIntent.getBroadcast(
                requireContext(),
                e.getId(),
                intent,
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                        ? PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
                        : PendingIntent.FLAG_UPDATE_CURRENT
        );

        android.app.AlarmManager am = (android.app.AlarmManager) requireContext().getSystemService(Context.ALARM_SERVICE);
        if (am != null) {
            am.setExact(android.app.AlarmManager.RTC_WAKEUP, triggerTime, pi);
        }
    }

    private void cancelEventNotification(EventItem e) {
        Intent intent = new Intent(requireContext(), EventAlarmReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(
                requireContext(),
                e.getId(),
                intent,
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                        ? PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
                        : PendingIntent.FLAG_UPDATE_CURRENT
        );

        android.app.AlarmManager am = (android.app.AlarmManager) requireContext().getSystemService(Context.ALARM_SERVICE);
        if (am != null) {
            am.cancel(pi);
        }
    }
}
