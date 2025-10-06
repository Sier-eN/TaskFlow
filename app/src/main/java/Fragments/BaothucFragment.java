package Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.apptg.R;

import java.util.List;

import Adapter.BaoThucAdapter;
import Database.DatabaseHelper;
import item.BaoThuc;
import javaclass.ThemBaoThucBottomSheet;

public class BaothucFragment extends Fragment {

    private RecyclerView rvBaoThuc;
    private ImageView ivAdd;
    private BaoThucAdapter adapter;
    private DatabaseHelper dbHelper;
    private List<BaoThuc> baoThucList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_baothuc, container, false);

        rvBaoThuc = view.findViewById(R.id.rv_baothuc);
        ivAdd = view.findViewById(R.id.img_add);

        dbHelper = new DatabaseHelper(getContext());
        baoThucList = dbHelper.getAllBaoThuc();

        adapter = new BaoThucAdapter(getContext(), baoThucList);
        rvBaoThuc.setLayoutManager(new LinearLayoutManager(getContext()));
        rvBaoThuc.setAdapter(adapter);

        // Click thêm mới
        ivAdd.setOnClickListener(v -> {
            ThemBaoThucBottomSheet bottomSheet = ThemBaoThucBottomSheet.newInstance();
            bottomSheet.setBaoThuc(null); // tạo mới
            bottomSheet.show(getParentFragmentManager(), "ThemBaoThucBottomSheet");
        });

        // Click sửa báo thức
        adapter.setOnItemClickListener(baoThuc -> {
            ThemBaoThucBottomSheet bottomSheet = ThemBaoThucBottomSheet.newInstance();
            bottomSheet.setBaoThuc(baoThuc); // chỉnh sửa
            bottomSheet.show(getParentFragmentManager(), "ThemBaoThucBottomSheet");
        });

        // Lắng nghe refresh dữ liệu từ BaoThucActivity
        getParentFragmentManager().setFragmentResultListener(
                "refresh_baothuc", this,
                (requestKey, bundle) -> refreshData()
        );

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh dữ liệu ngay khi fragment hiển thị lại
        refreshData();
    }

    private void refreshData() {
        if (dbHelper == null) return;
        if (baoThucList == null) return;

        baoThucList.clear();
        baoThucList.addAll(dbHelper.getAllBaoThuc());
        adapter.notifyDataSetChanged();
    }
}
