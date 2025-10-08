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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.apptg.R;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import Adapter.ColorAdapter;
import Database.AppDatabase;
import dao.ActivityItemDao;
import item.ActivityItem;

public class AddEditActivityBottomSheet extends BottomSheetDialogFragment {

    private EditText edtTitle, edtDate, edtStartTime, edtEndTime, edtDescription;
    private ImageView imgSave, imgDelete;
    private RecyclerView rvColors;
    private ActivityItem activity;
    private ActivityItemDao activityDao;
    private String selectedColor = "#F44336"; // Mặc định đỏ
    private Runnable listener;

    // 10 màu
    private List<String> colorList = Arrays.asList(
            "#F44336", "#4CAF50", "#2196F3", "#FFEB3B", "#9C27B0",
            "#FF9800", "#00BCD4", "#E91E63", "#795548", "#607D8B"
    );

    private ColorAdapter colorAdapter;

    public static AddEditActivityBottomSheet newInstance(ActivityItem activity) {
        AddEditActivityBottomSheet sheet = new AddEditActivityBottomSheet();
        Bundle args = new Bundle();
        if (activity != null) args.putInt("activityId", activity.getId());
        sheet.setArguments(args);
        return sheet;
    }

    public void setListener(Runnable listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottomsheet_add_edit_activity, container, false);

        edtTitle = view.findViewById(R.id.edt_title);
        edtDate = view.findViewById(R.id.edt_date);
        edtStartTime = view.findViewById(R.id.edt_start_time);
        edtEndTime = view.findViewById(R.id.edt_end_time);
        edtDescription = view.findViewById(R.id.edt_description);
        imgSave = view.findViewById(R.id.img_save);
        imgDelete = view.findViewById(R.id.img_delete);
        rvColors = view.findViewById(R.id.rv_colors);

        activityDao = AppDatabase.getInstance(requireContext()).activityItemDao();

        // Setup RecyclerView chọn màu
        colorAdapter = new ColorAdapter(getContext(), colorList, (colorHex, pos) -> {
            selectedColor = colorHex;
        });
        rvColors.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rvColors.setAdapter(colorAdapter);

        // Load dữ liệu nếu đang sửa
        if (getArguments() != null && getArguments().containsKey("activityId")) {
            int id = getArguments().getInt("activityId");
            new Thread(() -> {
                List<ActivityItem> all = activityDao.getAll();
                for (ActivityItem a : all) {
                    if (a.getId() == id) {
                        activity = a;
                        break;
                    }
                }
                if (activity != null && getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        edtTitle.setText(activity.getTitle());
                        edtDate.setText(activity.getDateIso());
                        edtStartTime.setText(activity.getStartTime());
                        edtEndTime.setText(activity.getEndTime());
                        edtDescription.setText(activity.getDescription());
                        selectedColor = activity.getColorHex();
                        imgDelete.setVisibility(View.VISIBLE);

                        int pos = colorList.indexOf(selectedColor);
                        if (pos != -1) colorAdapter.setSelectedPos(pos);
                    });
                }
            }).start();
        } else {
            imgDelete.setVisibility(View.GONE);
        }

        edtDate.setOnClickListener(v -> showDatePicker(edtDate));
        edtStartTime.setOnClickListener(v -> showTimePicker(edtStartTime));
        edtEndTime.setOnClickListener(v -> showTimePicker(edtEndTime));

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
        String description = edtDescription.getText().toString().trim();

        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(date)) {
            Toast.makeText(getContext(), "Nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        new Thread(() -> {
            if (activity == null) {
                ActivityItem newActivity = new ActivityItem(title, date, start, end, selectedColor, description);
                activityDao.insert(newActivity);
            } else {
                activity.setTitle(title);
                activity.setDateIso(date);
                activity.setStartTime(start);
                activity.setEndTime(end);
                activity.setDescription(description);
                activity.setColorHex(selectedColor);
                activityDao.update(activity);
            }

            if (listener != null && getActivity() != null)
                getActivity().runOnUiThread(listener);

            dismiss();
        }).start();
    }

    private void deleteActivity() {
        if (activity != null) {
            new Thread(() -> {
                activityDao.delete(activity);
                if (listener != null && getActivity() != null)
                    getActivity().runOnUiThread(listener);
                dismiss();
            }).start();
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
