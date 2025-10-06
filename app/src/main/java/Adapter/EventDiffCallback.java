package Adapter;

import androidx.recyclerview.widget.DiffUtil;
import java.util.List;
import item.EventItem;

public class EventDiffCallback extends DiffUtil.Callback {

    private final List<EventItem> oldList;
    private final List<EventItem> newList;

    public EventDiffCallback(List<EventItem> oldList, List<EventItem> newList) {
        this.oldList = oldList;
        this.newList = newList;
    }

    @Override
    public int getOldListSize() { return oldList.size(); }

    @Override
    public int getNewListSize() { return newList.size(); }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return oldList.get(oldItemPosition).getId() == newList.get(newItemPosition).getId();
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        EventItem oldItem = oldList.get(oldItemPosition);
        EventItem newItem = newList.get(newItemPosition);
        return oldItem.getTitle().equals(newItem.getTitle())
                && oldItem.getDateIso().equals(newItem.getDateIso())
                && oldItem.getColorHex().equals(newItem.getColorHex());
    }
}
