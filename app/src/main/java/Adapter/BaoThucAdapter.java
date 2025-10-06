package Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.apptg.R;

import java.util.List;
import java.util.concurrent.Executors;

import Database.AppDatabase;
import dao.BaoThucDao;
import item.BaoThuc;
import javaclass.AlarmCanceler;
import javaclass.AlarmScheduler;

public class BaoThucAdapter extends RecyclerView.Adapter<BaoThucAdapter.BaoThucViewHolder> {

    private final Context context;
    private final List<BaoThuc> baoThucList;
    private final BaoThucDao baoThucDao;
    private OnItemClickListener listener;

    public BaoThucAdapter(Context context, List<BaoThuc> baoThucList) {
        this.context = context;
        this.baoThucList = baoThucList;
        this.baoThucDao = AppDatabase.getInstance(context).baoThucDao();
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

        int[] days = {baoThuc.getT2(), baoThuc.getT3(), baoThuc.getT4(), baoThuc.getT5(),
                baoThuc.getT6(), baoThuc.getT7(), baoThuc.getCn()};
        String[] names = {"T2","T3","T4","T5","T6","T7","CN"};
        StringBuilder sb = new StringBuilder();
        int count = 0;
        for (int i=0;i<7;i++){
            if (days[i]==1){
                if(sb.length()>0) sb.append(", ");
                sb.append(names[i]);
                count++;
            }
        }
        if(count==7) holder.tvRepeat.setText("Hằng ngày");
        else if(count>0) holder.tvRepeat.setText(sb.toString());
        else holder.tvRepeat.setText(baoThuc.isActive()?"Hôm nay":"Không lặp");

        holder.swAlarm.setOnCheckedChangeListener(null);
        holder.swAlarm.setChecked(baoThuc.isActive());

        int finalCount = count;
        holder.swAlarm.setOnCheckedChangeListener((buttonView, isChecked) -> {
            baoThuc.setBat(isChecked?1:0);
            Executors.newSingleThreadExecutor().execute(() -> baoThucDao.update(baoThuc));
            if(isChecked) AlarmScheduler.datBaoThuc(context,baoThuc);
            else AlarmCanceler.huyBaoThuc(context,baoThuc);

            holder.tvRepeat.setText(finalCount==7?"Hằng ngày":finalCount>0? (isChecked?sb.toString():"Không lặp"):(isChecked?"Hôm nay":"Không lặp"));
        });

        holder.itemView.setOnClickListener(v -> {
            if(listener!=null) listener.onItemClick(baoThuc);
        });
    }

    @Override
    public int getItemCount() {
        return baoThucList.size();
    }

    public static class BaoThucViewHolder extends RecyclerView.ViewHolder {
        TextView tvTime,tvRepeat;
        Switch swAlarm;
        public BaoThucViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvRepeat = itemView.findViewById(R.id.thoigian);
            swAlarm = itemView.findViewById(R.id.swAlarm);
        }
    }

    public interface OnItemClickListener{
        void onItemClick(BaoThuc baoThuc);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }
}
