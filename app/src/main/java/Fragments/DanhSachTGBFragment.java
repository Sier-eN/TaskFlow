package Fragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import Database.DatabaseHelper;
import com.example.apptg.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Adapter.ActivitiesAdapter;
import item.ActivityItem;

public class DanhSachTGBFragment extends Fragment {

    private RecyclerView recyclerView;
    private ActivitiesAdapter adapter;
    private DatabaseHelper db;
    private ImageView imgAdd;

    public DanhSachTGBFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_danhsachtgb, container, false);
        recyclerView = view.findViewById(R.id.recycler_activities);
        imgAdd = view.findViewById(R.id.img_add);
        db = new DatabaseHelper(getContext());

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ActivitiesAdapter(new ArrayList<>(), this::openEditActivity);
        recyclerView.setAdapter(adapter);

        loadActivities();

        imgAdd.setOnClickListener(v -> openEditActivity(null));

        return view;
    }

    private void openEditActivity(ActivityItem activity) {
        AddEditActivityBottomSheet bottomSheet = AddEditActivityBottomSheet.newInstance(activity);
        bottomSheet.setListener(this::loadActivities);
        bottomSheet.show(getParentFragmentManager(), "AddEditActivityBottomSheet");
    }

    private void loadActivities() {
        List<ActivityItem> allActivities = db.getAllActivities();
        Map<String, List<ActivityItem>> grouped = new HashMap<>();
        for (ActivityItem a : allActivities) {
            grouped.computeIfAbsent(a.getDateIso(), k -> new ArrayList<>()).add(a);
        }

        List<String> sortedDates = new ArrayList<>(grouped.keySet());
        Collections.sort(sortedDates);

        List<ActivitiesAdapter.ActivityGroup> groupList = new ArrayList<>();
        for (String date : sortedDates) {
            groupList.add(new ActivitiesAdapter.ActivityGroup(date, grouped.get(date)));
        }

        adapter.setGroups(groupList);
    }
}
