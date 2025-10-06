package Adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.apptg.R;

import item.EventItem;

public class EventAdapter extends ListAdapter<EventItem, EventAdapter.VH> {

    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(EventItem item);
    }

    public EventAdapter(OnItemClickListener l) {
        super(DIFF_CALLBACK);
        this.listener = l;
    }

    private static final DiffUtil.ItemCallback<EventItem> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<EventItem>() {
                @Override
                public boolean areItemsTheSame(@NonNull EventItem oldItem, @NonNull EventItem newItem) {
                    return oldItem.getId() == newItem.getId();
                }

                @Override
                public boolean areContentsTheSame(@NonNull EventItem oldItem, @NonNull EventItem newItem) {
                    return oldItem.equals(newItem);
                }
            };

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event_card, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        EventItem e = getItem(position);
        holder.tvTitle.setText(e.getTitle());
        holder.tvDate.setText(e.getDateIso());

        try {
            int bgColor = Color.parseColor(e.getColorHex());
            holder.viewColor.setBackgroundColor(bgColor);
        } catch (Exception ex) {
            holder.viewColor.setBackgroundColor(Color.GRAY);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(e);
        });
    }

    static class VH extends RecyclerView.ViewHolder {
        View viewColor;
        TextView tvTitle, tvDate;

        VH(@NonNull View itemView) {
            super(itemView);
            viewColor = itemView.findViewById(R.id.view_color);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvDate = itemView.findViewById(R.id.tv_date);
        }
    }
}
