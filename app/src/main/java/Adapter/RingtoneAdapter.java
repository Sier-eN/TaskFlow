package Adapter;
import android.content.Context;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.apptg.R;

import java.util.List;

import item.RingtoneItem;

public class RingtoneAdapter extends RecyclerView.Adapter<RingtoneAdapter.ViewHolder> {

    private Context context;
    private List<RingtoneItem> ringtoneList;
    private Uri selectedRingtoneUri = null;
    private Ringtone currentRingtone = null;

    public RingtoneAdapter(Context context, List<RingtoneItem> ringtoneList) {
        this.context = context;
        this.ringtoneList = ringtoneList;
    }

    public Uri getSelectedRingtoneUri() {
        return selectedRingtoneUri;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_ringtone, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RingtoneItem item = ringtoneList.get(position);
        holder.tvTitle.setText(item.getTitle());

        // RadioButton chọn nhạc chuông
        holder.radioButton.setChecked(selectedRingtoneUri != null && selectedRingtoneUri.equals(item.getUri()));
        holder.radioButton.setOnClickListener(v -> {
            selectedRingtoneUri = item.getUri();
            notifyDataSetChanged();
        });

        // Nút thử nhạc chuông
        holder.btnPlay.setOnClickListener(v -> {
            if (currentRingtone != null && currentRingtone.isPlaying()) {
                currentRingtone.stop();
            }
            try {
                currentRingtone = RingtoneManager.getRingtone(context, item.getUri());
                currentRingtone.play();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public int getItemCount() {
        return ringtoneList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle;
        Button btnPlay;
        RadioButton radioButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvRingtoneTitle);
            btnPlay = itemView.findViewById(R.id.btnPlay);
            radioButton = itemView.findViewById(R.id.radioSelect);
        }
    }
}
