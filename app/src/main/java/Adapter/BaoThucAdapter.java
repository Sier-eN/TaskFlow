package Adapter;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.apptg.R;
import Database.DatabaseHelper;
import item.BaoThuc;
import javaclass.AlarmCanceler;
import javaclass.AlarmScheduler;

import java.util.List;

public class BaoThucAdapter extends RecyclerView.Adapter<BaoThucAdapter.BaoThucViewHolder> {

    private Context context;
    private List<BaoThuc> baoThucList;
    private DatabaseHelper dbHelper;
    private OnItemClickListener listener;

    public BaoThucAdapter(Context context, List<BaoThuc> baoThucList) {
        this.context = context;
        this.baoThucList = baoThucList;
        this.dbHelper = new DatabaseHelper(context);
    }

    @NonNull
    @Override
    public BaoThucViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.thebaothuc, parent, false);
        return new BaoThucViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull BaoThucViewHolder holder, int position) {
        BaoThuc baoThuc = baoThucList.get(position);

        holder.tvTime.setText(baoThuc.getTimeString());

        // Hiển thị ngày lặp
        int[] ngayTrongTuan = {baoThuc.getT2(), baoThuc.getT3(), baoThuc.getT4(),
                baoThuc.getT5(), baoThuc.getT6(), baoThuc.getT7(), baoThuc.getCn()};
        String[] tenThu = {"T2", "T3", "T4", "T5", "T6", "T7", "CN"};

        int countSelected = 0;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 7; i++) {
            if (ngayTrongTuan[i] == 1) {
                countSelected++;
                if (sb.length() > 0) sb.append(", ");
                sb.append(tenThu[i]);
            }
        }

        if (countSelected == 7) {
            holder.tvRepeat.setText("Hằng ngày");
        } else if (countSelected > 0) {
            holder.tvRepeat.setText(sb.toString());
        } else {
            holder.tvRepeat.setText(baoThuc.isActive() ? "Hôm nay" : "Không lặp");
        }

        // Switch bật/tắt
        holder.swAlarm.setOnCheckedChangeListener(null);
        holder.swAlarm.setChecked(baoThuc.isActive());
        int finalCountSelected = countSelected;
        holder.swAlarm.setOnCheckedChangeListener((buttonView, isChecked) -> {
            baoThuc.setBat(isChecked ? 1 : 0);

            // Cập nhật database
            ContentValues values = new ContentValues();
            values.put("bat", baoThuc.getBat());
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            db.update(DatabaseHelper.TABLE_ALARM, values, "id=?", new String[]{String.valueOf(baoThuc.getId())});
            db.close();

            // Bật hoặc hủy báo thức thực tế
            if (isChecked) {
                AlarmScheduler.datBaoThuc(context, baoThuc);
            } else {
                AlarmCanceler.huyBaoThuc(context, baoThuc);
            }

            // Cập nhật TextView ngày lặp
            if (finalCountSelected == 7) {
                holder.tvRepeat.setText("Hằng ngày");
            } else if (finalCountSelected > 0) {
                holder.tvRepeat.setText(isChecked ? sb.toString() : "Không lặp");
            } else {
                holder.tvRepeat.setText(isChecked ? "Hôm nay" : "Không lặp");
            }
        });

        // Click item để sửa
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(baoThuc);
            }
        });
    }





    @Override
    public int getItemCount() {
        return baoThucList != null ? baoThucList.size() : 0;
    }

    public static class BaoThucViewHolder extends RecyclerView.ViewHolder {
        TextView tvTime, tvRepeat;
        Switch swAlarm;

        public BaoThucViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvRepeat = itemView.findViewById(R.id.thoigian);
            swAlarm = itemView.findViewById(R.id.swAlarm);
        }
    }

    // Interface để fragment gọi BottomSheet khi click item
    public interface OnItemClickListener {
        void onItemClick(BaoThuc baoThuc);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}
