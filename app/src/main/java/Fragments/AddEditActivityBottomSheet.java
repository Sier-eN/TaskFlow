package Fragments;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import Database.DatabaseHelper;
import com.example.apptg.R;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.Calendar;

import item.ActivityItem;

public class AddEditActivityBottomSheet extends BottomSheetDialogFragment {

    private EditText edtTitle, edtDate, edtStartTime, edtEndTime;
    private ImageView imgColor, imgSave, imgDelete;
    private ActivityItem activity;
    private DatabaseHelper db;
    private String selectedColor = "#FF0000";
    private Runnable listener;

    public static AddEditActivityBottomSheet newInstance(ActivityItem activity) {
        AddEditActivityBottomSheet sheet = new AddEditActivityBottomSheet();
        Bundle args = new Bundle();
        if (activity != null) args.putInt("activityId", activity.getId());
        sheet.setArguments(args);
        return sheet;
    }

    public void setListener(Runnable listener) { this.listener = listener; }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottomsheet_add_edit_activity, container, false);
        edtTitle = view.findViewById(R.id.edt_title);
        edtDate = view.findViewById(R.id.edt_date);
        edtStartTime = view.findViewById(R.id.edt_start_time);
        edtEndTime = view.findViewById(R.id.edt_end_time);
        imgColor = view.findViewById(R.id.img_color);
        imgSave = view.findViewById(R.id.img_save);
        imgDelete = view.findViewById(R.id.img_delete);

        db = new DatabaseHelper(getContext());

        if (getArguments() != null && getArguments().containsKey("activityId")) {
            int id = getArguments().getInt("activityId");
            for (ActivityItem a : db.getAllActivities()) {
                if (a.getId() == id) { activity = a; break; }
            }
            if (activity != null) {
                edtTitle.setText(activity.getTitle());
                edtDate.setText(activity.getDateIso());
                edtStartTime.setText(activity.getStartTime());
                edtEndTime.setText(activity.getEndTime());
                selectedColor = activity.getColorHex();
                imgDelete.setVisibility(View.VISIBLE);
            }
        } else imgDelete.setVisibility(View.GONE);

        edtDate.setOnClickListener(v -> showDatePicker(edtDate));
        edtStartTime.setOnClickListener(v -> showTimePicker(edtStartTime));
        edtEndTime.setOnClickListener(v -> showTimePicker(edtEndTime));

        imgColor.setColorFilter(android.graphics.Color.parseColor(selectedColor));
        imgColor.setOnClickListener(v -> {
            // Chọn màu: click lần lượt 5 màu
            if ("#FF0000".equals(selectedColor)) selectedColor = "#00FF00";
            else if ("#00FF00".equals(selectedColor)) selectedColor = "#0000FF";
            else if ("#0000FF".equals(selectedColor)) selectedColor = "#FFFF00";
            else if ("#FFFF00".equals(selectedColor)) selectedColor = "#FF00FF";
            else selectedColor = "#FF0000";
            imgColor.setColorFilter(android.graphics.Color.parseColor(selectedColor));
        });

        imgSave.setOnClickListener(v -> saveActivity());
        imgDelete.setOnClickListener(v -> deleteActivity());

        return view;
    }

    private void showDatePicker(EditText edt) {
        Calendar c = Calendar.getInstance();
        DatePickerDialog dp = new DatePickerDialog(getContext(),
                (view, year, month, day) -> edt.setText(String.format("%04d-%02d-%02d", year, month + 1, day)),
                c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
        dp.show();
    }

    private void showTimePicker(EditText edt) {
        Calendar c = Calendar.getInstance();
        TimePickerDialog tp = new TimePickerDialog(getContext(),
                (view, hour, minute) -> edt.setText(String.format("%02d:%02d", hour, minute)),
                c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true);
        tp.show();
    }

    private void saveActivity() {
        String title = edtTitle.getText().toString().trim();
        String date = edtDate.getText().toString().trim();
        String start = edtStartTime.getText().toString().trim();
        String end = edtEndTime.getText().toString().trim();
        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(date)) {
            Toast.makeText(getContext(), "Nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        if (activity == null) {
            ActivityItem newActivity = new ActivityItem(0, title, date, selectedColor);
            newActivity.setStartTime(start);
            newActivity.setEndTime(end);
            db.insertActivity(newActivity);
        } else {
            activity.setTitle(title);
            activity.setDateIso(date);
            activity.setStartTime(start);
            activity.setEndTime(end);
            activity.setColorHex(selectedColor);
            db.updateActivity(activity);
        }

        if (listener != null) listener.run();
        dismiss();
    }

    private void deleteActivity() {
        if (activity != null) {
            db.deleteActivity(activity.getId());
            if (listener != null) listener.run();
            dismiss();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        View view = getView();
        if (view != null) {
            View parent = (View) view.getParent();
            parent.getLayoutParams().height = (int) (getResources().getDisplayMetrics().heightPixels * 0.75);
            BottomSheetBehavior behavior = BottomSheetBehavior.from(parent);
            behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            behavior.setSkipCollapsed(true);
        }
    }
}
