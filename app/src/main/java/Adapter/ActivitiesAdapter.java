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

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    private List<ActivityGroup> groups = new ArrayList<>();
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

    // Dùng để chuyển vị trí Adapter sang nhóm + item
    private Object getItemAtPosition(int position) {
        int count = 0;
        for (ActivityGroup g : groups) {
            if (position == count) return g; // header
            count++;
            int size = g.activities.size();
            if (position < count + size) return g.activities.get(position - count);
            count += size;
        }
        return null;
    }

    @Override
    public int getItemViewType(int position) {
        Object item = getItemAtPosition(position);
        if (item instanceof ActivityGroup) return TYPE_HEADER;
        else return TYPE_ITEM;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
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
        Object item = getItemAtPosition(position);
        if (holder instanceof HeaderViewHolder && item instanceof ActivityGroup) {
            ((HeaderViewHolder) holder).tvDate.setText(((ActivityGroup) item).date);
        } else if (holder instanceof ActivityViewHolder && item instanceof ActivityItem) {
            ActivityItem activity = (ActivityItem) item;
            ActivityViewHolder avh = (ActivityViewHolder) holder;

            avh.tvTitle.setText(activity.getTitle());

            String timeText = "";
            if (activity.getStartTime() != null && !activity.getStartTime().isEmpty())
                timeText += activity.getStartTime();
            if (activity.getEndTime() != null && !activity.getEndTime().isEmpty())
                timeText += " - " + activity.getEndTime();
            avh.tvTime.setText(timeText);

            try {
                avh.viewColor.setBackgroundColor(Color.parseColor(activity.getColorHex()));
            } catch (Exception e) {
                avh.viewColor.setBackgroundColor(Color.GRAY);
            }

            avh.itemView.setOnClickListener(v -> {
                if (listener != null) listener.onActivityClick(activity);
            });
        }
    }

    @Override
    public int getItemCount() {
        int count = 0;
        for (ActivityGroup g : groups) {
            count += 1 + g.activities.size(); // header + items
        }
        return count;
    }

    // --- ViewHolders ---
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

    // --- Class nhóm ---
    public static class ActivityGroup {
        String date;
        List<ActivityItem> activities;

        public ActivityGroup(String date, List<ActivityItem> activities) {
            this.date = date;
            this.activities = activities;
        }
    }
}
