package Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.apptg.R;

import java.util.List;

public class ColorAdapter extends RecyclerView.Adapter<ColorAdapter.VH> {
    private List<String> colors;
    private Context ctx;
    private int selectedPos = -1;
    private OnColorClickListener listener;

    public interface OnColorClickListener { void onColorClick(String colorHex, int pos); }

    public ColorAdapter(Context ctx, List<String> colors, OnColorClickListener l) {
        this.ctx = ctx; this.colors = colors; this.listener = l;
    }

    public void setSelectedPos(int pos) { this.selectedPos = pos; notifyDataSetChanged(); }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(ctx).inflate(R.layout.item_color, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        String hex = colors.get(position);
        try { holder.colorView.setBackgroundColor(Color.parseColor(hex)); } catch (Exception e) { holder.colorView.setBackgroundColor(Color.GRAY); }
        holder.icCheck.setVisibility(position == selectedPos ? View.VISIBLE : View.GONE);
        holder.itemView.setOnClickListener(v -> {
            selectedPos = position;
            notifyDataSetChanged();
            if (listener != null) listener.onColorClick(hex, position);
        });
    }

    @Override
    public int getItemCount() { return colors == null ? 0 : colors.size(); }

    static class VH extends RecyclerView.ViewHolder {
        View colorView; ImageView icCheck;
        VH(@NonNull View itemView) {
            super(itemView);
            colorView = itemView.findViewById(R.id.color_view);
            icCheck = itemView.findViewById(R.id.ic_check);
        }
    }
}
