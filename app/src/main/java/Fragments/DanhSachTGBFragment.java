package Fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;

import com.example.apptg.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import Adapter.ActivitiesAdapter;
import Database.AppDatabase;
import dao.ActivityItemDao;
import item.ActivityItem;

public class DanhSachTGBFragment extends Fragment {

    private RecyclerView recyclerView;
    private ActivitiesAdapter adapter;
    private ImageView imgAdd;
    private Spinner spinnerSort;

    private ActivityItemDao activityDao;

    public DanhSachTGBFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_danhsachtgb, container, false);

        recyclerView = view.findViewById(R.id.recycler_activities);
        imgAdd = view.findViewById(R.id.img_add);
        spinnerSort = view.findViewById(R.id.spinner_sort);

        // Setup DAO
        activityDao = AppDatabase.getInstance(requireContext()).activityItemDao();

        // RecyclerView setup
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ActivitiesAdapter(new ArrayList<>(), this::openEditActivity);
        recyclerView.setAdapter(adapter);

        // Spinner setup
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.sort_options,
                android.R.layout.simple_spinner_item
        );
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSort.setAdapter(spinnerAdapter);

        spinnerSort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                loadActivities(position); // 0: Ngày, 1: Tuần, 2: Tháng
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Nút thêm
        imgAdd.setOnClickListener(v -> openEditActivity(null));

        return view;
    }

    private void openEditActivity(ActivityItem activity) {
        AddEditActivityBottomSheet bottomSheet = AddEditActivityBottomSheet.newInstance(activity);
        bottomSheet.setListener(this::refreshSpinner); // reload khi thêm/sửa
        bottomSheet.show(getParentFragmentManager(), "AddEditActivityBottomSheet");
    }

    private void refreshSpinner() {
        // Giữ vị trí hiện tại của spinner
        int position = spinnerSort.getSelectedItemPosition();
        loadActivities(position);
    }

    private void loadActivities(int sortType) {
        new Thread(() -> {
            List<ActivityItem> allActivities = activityDao.getAll();
            List<ActivitiesAdapter.ActivityGroup> groupList = new ArrayList<>();

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Calendar cal = Calendar.getInstance();

            switch (sortType) {
                case 0: // Theo ngày
                    Map<String, List<ActivityItem>> groupedByDay = new HashMap<>();
                    for (ActivityItem a : allActivities) {
                        groupedByDay.computeIfAbsent(a.getDateIso(), k -> new ArrayList<>()).add(a);
                    }
                    List<String> sortedDates = new ArrayList<>(groupedByDay.keySet());
                    Collections.sort(sortedDates);
                    for (String date : sortedDates) {
                        groupList.add(new ActivitiesAdapter.ActivityGroup(date, groupedByDay.get(date)));
                    }
                    break;

                case 1: // Theo tuần
                    Map<String, List<ActivityItem>> groupedByWeek = new HashMap<>();
                    for (ActivityItem a : allActivities) {
                        try {
                            Date date = sdf.parse(a.getDateIso());
                            cal.setTime(date);
                            int week = cal.get(Calendar.WEEK_OF_YEAR);
                            int year = cal.get(Calendar.YEAR);
                            String weekKey = year + "-Tuần " + week;
                            groupedByWeek.computeIfAbsent(weekKey, k -> new ArrayList<>()).add(a);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                    List<String> sortedWeeks = new ArrayList<>(groupedByWeek.keySet());
                    Collections.sort(sortedWeeks);
                    for (String week : sortedWeeks) {
                        groupList.add(new ActivitiesAdapter.ActivityGroup(week, groupedByWeek.get(week)));
                    }
                    break;

                case 2: // Theo tháng
                    Map<String, List<ActivityItem>> groupedByMonth = new HashMap<>();
                    for (ActivityItem a : allActivities) {
                        String monthKey = a.getDateIso().substring(0, 7); // yyyy-MM
                        groupedByMonth.computeIfAbsent(monthKey, k -> new ArrayList<>()).add(a);
                    }
                    List<String> sortedMonths = new ArrayList<>(groupedByMonth.keySet());
                    Collections.sort(sortedMonths);
                    for (String month : sortedMonths) {
                        groupList.add(new ActivitiesAdapter.ActivityGroup("Tháng " + month, groupedByMonth.get(month)));
                    }
                    break;
            }

            requireActivity().runOnUiThread(() -> adapter.setGroups(groupList));
        }).start();
    }
}
