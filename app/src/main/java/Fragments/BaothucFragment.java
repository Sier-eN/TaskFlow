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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

import Adapter.BaoThucAdapter;
import Database.AppDatabase;
import dao.BaoThucDao;
import item.BaoThuc;
import javaclass.ThemBaoThucBottomSheet;

public class BaothucFragment extends Fragment {

    private RecyclerView rvBaoThuc;
    private ImageView ivAdd;
    private BaoThucAdapter adapter;
    private List<BaoThuc> baoThucList = new ArrayList<>();
    private BaoThucDao baoThucDao;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_baothuc, container, false);

        rvBaoThuc = view.findViewById(R.id.rv_baothuc);
        ivAdd = view.findViewById(R.id.img_add);

        adapter = new BaoThucAdapter(getContext(), baoThucList);
        rvBaoThuc.setLayoutManager(new LinearLayoutManager(getContext()));
        rvBaoThuc.setAdapter(adapter);

        baoThucDao = AppDatabase.getInstance(requireContext()).baoThucDao();

        loadBaoThuc();

        ivAdd.setOnClickListener(v -> openBottomSheet(null));
        adapter.setOnItemClickListener(this::openBottomSheet);

        return view;
    }

    private void loadBaoThuc() {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<BaoThuc> list = baoThucDao.getAll();
            requireActivity().runOnUiThread(() -> {
                baoThucList.clear();
                baoThucList.addAll(list);
                adapter.notifyDataSetChanged();
            });
        });
    }

    private void openBottomSheet(BaoThuc baoThuc) {
        ThemBaoThucBottomSheet bottomSheet = ThemBaoThucBottomSheet.newInstance();
        bottomSheet.setBaoThuc(baoThuc);
        bottomSheet.setListener(this::loadBaoThuc);
        bottomSheet.show(getParentFragmentManager(), "ThemBaoThucBottomSheet");
    }

    @Override
    public void onResume() {
        super.onResume();
        loadBaoThuc();
    }
}
