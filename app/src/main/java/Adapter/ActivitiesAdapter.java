package Adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.apptg.R;

import java.util.ArrayList;
import java.util.List;

import item.ActivityItem;

public class ActivitiesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<ActivityGroup> groups;
    private OnActivityClickListener listener;

    public interface OnActivityClickListener {
        void onActivityClick(ActivityItem activity);
    }

    public ActivitiesAdapter(List<ActivityGroup> groups, OnActivityClickListener listener) {
        this.groups = groups;
        this.listener = listener;
    }

    public void setGroups(List<ActivityGroup> groups) {
        this.groups = groups;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        int count = 0;
        for (ActivityGroup g : groups) {
            if (position == count) return 0; // Header
            count++;
            int size = g.activities.size();
            if (position < count + size) return 1; // Activity item
            count += size;
        }
        return 1;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == 0) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_activity_date_header, parent, false);
            return new HeaderViewHolder(v);
        } else {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_activity, parent, false);
            return new ActivityViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int count = 0;
        for (ActivityGroup g : groups) {
            if (position == count && holder instanceof HeaderViewHolder) {
                ((HeaderViewHolder) holder).tvDate.setText(g.date);
                return;
            }
            count++;
            int size = g.activities.size();
            if (position < count + size && holder instanceof ActivityViewHolder) {
                ActivityItem activity = g.activities.get(position - count);
                ActivityViewHolder avh = (ActivityViewHolder) holder;
                avh.tvTitle.setText(activity.getTitle());
                String timeText = "";
                if (activity.getStartTime() != null && !activity.getStartTime().isEmpty())
                    timeText += activity.getStartTime();
                if (activity.getEndTime() != null && !activity.getEndTime().isEmpty())
                    timeText += " - " + activity.getEndTime();
                avh.tvTime.setText(timeText);
                avh.viewColor.setBackgroundColor(Color.parseColor(activity.getColorHex()));

                avh.itemView.setOnClickListener(v -> {
                    if (listener != null) listener.onActivityClick(activity);
                });
                return;
            }
            count += size;
        }
    }

    @Override
    public int getItemCount() {
        int count = 0;
        for (ActivityGroup g : groups) {
            count += 1 + g.activities.size(); // 1 header + items
        }
        return count;
    }

    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate;
        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tv_date_header);
        }
    }

    static class ActivityViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvTime;
        View viewColor;
        public ActivityViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvTime = itemView.findViewById(R.id.tv_time);
            viewColor = itemView.findViewById(R.id.view_color);
        }
    }

    public static class ActivityGroup {
        String date;
        List<ActivityItem> activities;

        public ActivityGroup(String date, List<ActivityItem> activities) {
            this.date = date;
            this.activities = activities;
        }
    }
}
