package javaclass;

import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import com.example.apptg.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.Calendar;

import Database.DatabaseHelper;
import item.BaoThuc;

public class ThemBaoThucBottomSheet extends BottomSheetDialogFragment {

    private TimePicker timePicker;
    private ToggleButton tbT2, tbT3, tbT4, tbT5, tbT6, tbT7, tbCN;
    private ImageView imgLuu, imgXoa, imgChonNhac;
    private TextView tvTenNhac;
    private CardView cardChonNhac, cvXoa;
    private DatabaseHelper dbHelper;
    private BaoThuc baoThuc;

    private Ringtone currentRingtone;
    private String selectedRingtoneUri;

    public static ThemBaoThucBottomSheet newInstance() {
        return new ThemBaoThucBottomSheet();
    }

    public void setBaoThuc(BaoThuc baoThuc) {
        this.baoThuc = baoThuc;

        if (getView() != null) {
            if (baoThuc != null) {
                loadBaoThucData();
            } else {
                resetViewsForNew();
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_thembaothuc, container, false);

        // Ánh xạ
        timePicker = view.findViewById(R.id.timePicker);
        tbT2 = view.findViewById(R.id.tbT2);
        tbT3 = view.findViewById(R.id.tbT3);
        tbT4 = view.findViewById(R.id.tbT4);
        tbT5 = view.findViewById(R.id.tbT5);
        tbT6 = view.findViewById(R.id.tbT6);
        tbT7 = view.findViewById(R.id.tbT7);
        tbCN = view.findViewById(R.id.tbCN);

        cvXoa = view.findViewById(R.id.cvXoa);
        imgLuu = view.findViewById(R.id.imgLuu);
        imgXoa = view.findViewById(R.id.imgXoa);

        cardChonNhac = view.findViewById(R.id.cardChonNhac);
        imgChonNhac = view.findViewById(R.id.imgChonNhac);
        tvTenNhac = view.findViewById(R.id.tvTenNhac);

        dbHelper = new DatabaseHelper(getContext());
        timePicker.setIs24HourView(true);

        // Chọn nhạc
        View.OnClickListener openRingtone = v -> openRingtonePicker();
        cardChonNhac.setOnClickListener(openRingtone);
        imgChonNhac.setOnClickListener(openRingtone);

        // Lưu
        imgLuu.setOnClickListener(v -> saveBaoThuc());

        // Xóa
        imgXoa.setOnClickListener(v -> deleteBaoThuc());

        // Load dữ liệu
        if (baoThuc != null) {
            loadBaoThucData();
        } else {
            resetViewsForNew();
        }

        return view;
    }

    private void resetViewsForNew() {
        Calendar now = Calendar.getInstance();
        timePicker.setHour(now.get(Calendar.HOUR_OF_DAY));
        timePicker.setMinute(now.get(Calendar.MINUTE));

        tbT2.setChecked(false);
        tbT3.setChecked(false);
        tbT4.setChecked(false);
        tbT5.setChecked(false);
        tbT6.setChecked(false);
        tbT7.setChecked(false);
        tbCN.setChecked(false);

        selectedRingtoneUri = null;
        updateRingtoneName();

        imgXoa.setVisibility(View.GONE);
        cvXoa.setVisibility(View.GONE);
    }

    private void loadBaoThucData() {
        if (baoThuc == null) return;

        timePicker.setHour(baoThuc.getH());
        timePicker.setMinute(baoThuc.getM());

        tbT2.setChecked(baoThuc.getT2() == 1);
        tbT3.setChecked(baoThuc.getT3() == 1);
        tbT4.setChecked(baoThuc.getT4() == 1);
        tbT5.setChecked(baoThuc.getT5() == 1);
        tbT6.setChecked(baoThuc.getT6() == 1);
        tbT7.setChecked(baoThuc.getT7() == 1);
        tbCN.setChecked(baoThuc.getCn() == 1);

        selectedRingtoneUri = baoThuc.getRingtoneUri();
        updateRingtoneName();

        imgXoa.setVisibility(View.VISIBLE);
        cvXoa.setVisibility(View.VISIBLE);
    }

    private void updateRingtoneName() {
        if (selectedRingtoneUri != null) {
            Ringtone r = RingtoneManager.getRingtone(getContext(), Uri.parse(selectedRingtoneUri));
            if (r != null) tvTenNhac.setText(r.getTitle(getContext()));
        } else {
            tvTenNhac.setText("Chưa chọn nhạc");
        }
    }

    private void saveBaoThuc() {
        stopRingtone();

        int h = timePicker.getHour();
        int m = timePicker.getMinute();
        int t2 = tbT2.isChecked() ? 1 : 0;
        int t3 = tbT3.isChecked() ? 1 : 0;
        int t4 = tbT4.isChecked() ? 1 : 0;
        int t5 = tbT5.isChecked() ? 1 : 0;
        int t6 = tbT6.isChecked() ? 1 : 0;
        int t7 = tbT7.isChecked() ? 1 : 0;
        int cn = tbCN.isChecked() ? 1 : 0;

        if (baoThuc != null) {
            baoThuc.setH(h);
            baoThuc.setM(m);
            baoThuc.setT2(t2);
            baoThuc.setT3(t3);
            baoThuc.setT4(t4);
            baoThuc.setT5(t5);
            baoThuc.setT6(t6);
            baoThuc.setT7(t7);
            baoThuc.setCn(cn);
            baoThuc.setBat(1);
            baoThuc.setRingtoneUri(selectedRingtoneUri);
            dbHelper.updateBaoThuc(baoThuc);

            AlarmCanceler.huyBaoThuc(getContext(), baoThuc);
            AlarmScheduler.datBaoThuc(getContext(), baoThuc);
        } else {
            BaoThuc newBaoThuc = new BaoThuc(h, m, t2, t3, t4, t5, t6, t7, cn, 1);
            newBaoThuc.setRingtoneUri(selectedRingtoneUri);
            long id = dbHelper.insertBaoThuc(newBaoThuc);
            newBaoThuc.setId((int) id);
            AlarmScheduler.datBaoThuc(getContext(), newBaoThuc);
        }

        getParentFragmentManager().setFragmentResult("refresh_baothuc", new Bundle());
        dismiss();
    }

    private void deleteBaoThuc() {
        stopRingtone();
        if (baoThuc != null) {
            dbHelper.deleteBaoThuc(baoThuc.getId());
            AlarmCanceler.huyBaoThuc(getContext(), baoThuc);
            getParentFragmentManager().setFragmentResult("refresh_baothuc", new Bundle());
            dismiss();
        }
    }

    private void openRingtonePicker() {
        Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALARM);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Chọn nhạc chuông");
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, false);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI,
                selectedRingtoneUri != null ? Uri.parse(selectedRingtoneUri) : null);

        startActivityForResult(intent, 999);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 999 && resultCode == getActivity().RESULT_OK && data != null) {
            Uri uri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
            if (uri != null) {
                selectedRingtoneUri = uri.toString();
                updateRingtoneName();
            }
        }
    }

    private void stopRingtone() {
        if (currentRingtone != null && currentRingtone.isPlaying()) {
            currentRingtone.stop();
            currentRingtone = null;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        stopRingtone();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopRingtone();
    }

    @Override
    public void onStart() {
        super.onStart();

        View view = getView();
        if (view == null) return;

        View parent = (View) view.getParent();
        parent.setBackgroundResource(android.R.color.transparent);

        com.google.android.material.bottomsheet.BottomSheetBehavior<View> behavior =
                com.google.android.material.bottomsheet.BottomSheetBehavior.from(parent);

        int height = (int) (getResources().getDisplayMetrics().heightPixels * 0.8);
        view.getLayoutParams().height = height;
        view.requestLayout();

        behavior.setPeekHeight(height);
        behavior.setState(com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED);
    }
}
