package Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.apptg.R;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.util.List;

import Database.AppDatabase;
import Adapter.HenGioAdapter;
import item.HenGio;

public class HenGioFragment extends Fragment {

    private RecyclerView recyclerView;
    private HenGioAdapter adapter;
    private CardView cardAdd;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_hengio, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        cardAdd = view.findViewById(R.id.card_add);
        cardAdd.setOnClickListener(v -> {
            BottomSheetHenGio bottomSheet = new BottomSheetHenGio();
            bottomSheet.setTargetFragment(this, 1); // 🔁 để callback
            bottomSheet.show(getParentFragmentManager(), "BottomSheetHenGio");
        });

        loadHenGioList();
        return view;
    }

    // Load danh sách ban đầu
    public void loadHenGioList() {
        new Thread(() -> {
            List<HenGio> list = AppDatabase.getInstance(requireContext())
                    .henGioDao()
                    .getAll();

            requireActivity().runOnUiThread(() -> {
                adapter = new HenGioAdapter(requireContext(), list);
                recyclerView.setAdapter(adapter);
            });
        }).start();
    }

    //  Gọi lại khi thêm mới
    public void refreshData() {
        loadHenGioList();
    }
}
