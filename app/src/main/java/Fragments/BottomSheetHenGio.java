package Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.apptg.R;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import Database.AppDatabase;
import item.HenGio;

public class BottomSheetHenGio extends BottomSheetDialogFragment {

    private TextView tvTime;
    private StringBuilder input = new StringBuilder();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.hengiobottomsheet, container, false);

        tvTime = view.findViewById(R.id.tvTime);

        // Các nút số
        int[] numberButtons = {
                R.id.btnNum0, R.id.btnNum1, R.id.btnNum2, R.id.btnNum3,
                R.id.btnNum4, R.id.btnNum5, R.id.btnNum6, R.id.btnNum7,
                R.id.btnNum8, R.id.btnNum9, R.id.btnNum00
        };

        for (int id : numberButtons) {
            Button b = view.findViewById(id);
            b.setOnClickListener(v -> {
                input.append(((Button) v).getText().toString());
                updateTimeDisplay();
            });
        }

        // Nút xoá
        view.findViewById(R.id.btnBack).setOnClickListener(v -> {
            if (input.length() > 0) {
                input.deleteCharAt(input.length() - 1);
                updateTimeDisplay();
            }
        });

        // Nút huỷ
        view.findViewById(R.id.btnCancel).setOnClickListener(v -> dismiss());

        // Nút bắt đầu / lưu
        view.findViewById(R.id.btnStart).setOnClickListener(v -> saveToDatabase());

        updateTimeDisplay();
        return view;
    }

    // Cập nhật hiển thị thời gian
    private void updateTimeDisplay() {
        String digits = input.toString();
        if (digits.isEmpty()) {
            tvTime.setText("00h 00m 00s");
            return;
        }
        if (digits.length() > 6) digits = digits.substring(digits.length() - 6);
        while (digits.length() < 6) digits = "0" + digits;

        int gio = Integer.parseInt(digits.substring(0, 2));
        int phut = Integer.parseInt(digits.substring(2, 4));
        int giay = Integer.parseInt(digits.substring(4, 6));

        if (phut > 59) phut = 59;
        if (giay > 59) giay = 59;

        tvTime.setText(String.format("%02dh %02dm %02ds", gio, phut, giay));
    }

    // Lưu vào CSDL và cập nhật RecyclerView
    private void saveToDatabase() {
        String text = tvTime.getText().toString();
        String[] parts = text.replace("h", "").replace("m", "").replace("s", "").trim().split(" ");

        int gio = Integer.parseInt(parts[0]);
        int phut = Integer.parseInt(parts[1]);
        int giay = Integer.parseInt(parts[2]);

        HenGio henGio = new HenGio(gio, phut, giay);

        new Thread(() -> {
            AppDatabase.getInstance(requireContext()).henGioDao().insert(henGio);

            requireActivity().runOnUiThread(() -> {
                // Gọi cập nhật lại danh sách trong HenGioFragment
                if (getTargetFragment() instanceof HenGioFragment) {
                    ((HenGioFragment) getTargetFragment()).refreshData();
                }
                dismiss();
            });
        }).start();
    }
    @Override
    public void onStart() {
        super.onStart();

        View view = getView();
        if (view != null) {
            // Lấy layout của bottom sheet
            View parent = (View) view.getParent();
            BottomSheetBehavior<?> behavior = BottomSheetBehavior.from(parent);

            // 🔹Đặt chiều cao 85% màn hình
            int height = (int) (requireContext().getResources().getDisplayMetrics().heightPixels * 0.80);
            parent.getLayoutParams().height = height;
            parent.requestLayout();

            //  Mở rộng hoàn toàn
            behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            behavior.setSkipCollapsed(true);
        }
    }

    @Override
    public int getTheme() {
        return com.google.android.material.R.style.Theme_Material3_Light_BottomSheetDialog;
    }
}
