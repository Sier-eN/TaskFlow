package Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.apptg.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Database.AppDatabase;
import dao.HenGioDao;
import item.HenGio;

public class HenGioAdapter extends RecyclerView.Adapter<HenGioAdapter.ViewHolder> {

    private final List<HenGio> list;
    private final Context context;
    private final HenGioDao henGioDao;
    private final Map<Integer, CountDownTimer> timerMap = new HashMap<>();
    private final Map<Integer, Integer> remainingTimeMap = new HashMap<>();
    private final Map<Integer, MediaPlayer> playerMap = new HashMap<>();

    public HenGioAdapter(Context context, List<HenGio> list) {
        this.context = context;
        this.list = list;
        this.henGioDao = AppDatabase.getInstance(context).henGioDao();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.itemhengio, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HenGio henGio = list.get(position);
        int totalSeconds = henGio.getGio() * 3600 + henGio.getPhut() * 60 + henGio.getGiay();
        remainingTimeMap.put(henGio.getId(), totalSeconds);

        holder.progressCircle.setMax(totalSeconds);
        holder.progressCircle.setProgress(totalSeconds);
        holder.tvTimerInfo.setText(String.format("Bộ hẹn giờ: %dh %dm %ds",
                henGio.getGio(), henGio.getPhut(), henGio.getGiay()));
        holder.tvTime.setText(formatTime(totalSeconds));

        //  Nút Play/Pause
        holder.btnPlay.setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();
            if (pos == RecyclerView.NO_POSITION) return;

            if (timerMap.containsKey(henGio.getId())) {
                // Đang chạy → tạm dừng
                timerMap.get(henGio.getId()).cancel();
                timerMap.remove(henGio.getId());
                holder.btnPlay.setImageResource(R.drawable.play);
            } else {
                // Bắt đầu hoặc tiếp tục
                startTimer(holder, henGio);
                holder.btnPlay.setImageResource(R.drawable.pause);
            }
        });

        // Nút +1 phút
        holder.btnAddTime.setOnClickListener(v -> {
            int currentRemain = remainingTimeMap.getOrDefault(henGio.getId(), totalSeconds);
            boolean isRunning = timerMap.containsKey(henGio.getId());

            // Tăng thêm 60 giây
            int newRemain = currentRemain + 60;
            remainingTimeMap.put(henGio.getId(), newRemain);

            // Cập nhật giao diện ngay
            holder.progressCircle.setMax(newRemain);
            holder.progressCircle.setProgress(newRemain);
            holder.tvTime.setText(formatTime(newRemain));
            holder.tvTimerInfo.setText(String.format("Bộ hẹn giờ: %dh %dm %ds",
                    newRemain / 3600, (newRemain % 3600) / 60, newRemain % 60));

            // Nếu đang chạy thì khởi động lại với thời gian mới (đếm tiếp)
            if (isRunning) {
                timerMap.get(henGio.getId()).cancel();
                startTimer(holder, henGio);
            }
        });


        // Nút Reset
        holder.btnReset.setOnClickListener(v -> {
            int startTime = henGio.getGio() * 3600 + henGio.getPhut() * 60 + henGio.getGiay();
            remainingTimeMap.put(henGio.getId(), startTime);

            // Dừng timer nếu đang chạy
            if (timerMap.containsKey(henGio.getId())) {
                timerMap.get(henGio.getId()).cancel();
                timerMap.remove(henGio.getId());
            }

            // Dừng chuông nếu đang kêu
            if (playerMap.containsKey(henGio.getId())) {
                MediaPlayer mp = playerMap.get(henGio.getId());
                if (mp.isPlaying()) mp.stop();
                mp.release();
                playerMap.remove(henGio.getId());
            }

            holder.progressCircle.setMax(startTime);
            holder.progressCircle.setProgress(startTime);
            holder.tvTime.setText(formatTime(startTime));
            holder.tvTimerInfo.setText(String.format("Bộ hẹn giờ: %dh %dm %ds",
                    henGio.getGio(), henGio.getPhut(), henGio.getGiay()));
            holder.btnPlay.setImageResource(R.drawable.play);
        });

        // Nút X (xóa)
        holder.btnClose.setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();
            if (pos == RecyclerView.NO_POSITION) return;

            HenGio item = list.get(pos);

            // Hủy timer nếu đang chạy
            if (timerMap.containsKey(item.getId())) {
                timerMap.get(item.getId()).cancel();
                timerMap.remove(item.getId());
            }

            // Dừng chuông nếu đang phát
            if (playerMap.containsKey(item.getId())) {
                MediaPlayer mp = playerMap.get(item.getId());
                if (mp.isPlaying()) mp.stop();
                mp.release();
                playerMap.remove(item.getId());
            }

            // 🗑 Xóa khỏi database (chạy trong thread riêng)
            new Thread(() -> henGioDao.delete(item)).start();

            // Xóa khỏi danh sách hiển thị
            list.remove(pos);
            notifyItemRemoved(pos);
        });
    }

    private void startTimer(ViewHolder holder, HenGio henGio) {
        int remain = remainingTimeMap.getOrDefault(henGio.getId(),
                henGio.getGio() * 3600 + henGio.getPhut() * 60 + henGio.getGiay());

        CountDownTimer timer = new CountDownTimer(remain * 1000L, 1000) {
            int remaining = remain;

            @Override
            public void onTick(long millisUntilFinished) {
                remaining--;
                remainingTimeMap.put(henGio.getId(), remaining);
                holder.tvTime.setText(formatTime(remaining));
                holder.progressCircle.setProgress(remaining);
            }

            @Override
            public void onFinish() {
                holder.tvTime.setText("00:00:00");
                holder.progressCircle.setProgress(0);
                holder.tvTimerInfo.setText("⏰ Hết giờ!");
                holder.btnPlay.setImageResource(R.drawable.play);

                MediaPlayer mp = MediaPlayer.create(context, R.raw.timer_done);
                playerMap.put(henGio.getId(), mp);
                mp.setOnCompletionListener(mediaPlayer -> {
                    mediaPlayer.release();
                    playerMap.remove(henGio.getId());
                });
                mp.start();
            }
        }.start();

        timerMap.put(henGio.getId(), timer);
    }

    private String formatTime(int totalSeconds) {
        int h = totalSeconds / 3600;
        int m = (totalSeconds % 3600) / 60;
        int s = totalSeconds % 60;
        return String.format("%02d:%02d:%02d", h, m, s);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTimerInfo, tvTime;
        ProgressBar progressCircle;
        ImageView btnClose, btnPlay, btnReset;
        Button btnAddTime;

        ViewHolder(View itemView) {
            super(itemView);
            tvTimerInfo = itemView.findViewById(R.id.tvTimerInfo);
            tvTime = itemView.findViewById(R.id.tvTime);
            progressCircle = itemView.findViewById(R.id.progressCircle);
            btnClose = itemView.findViewById(R.id.btnClose);
            btnPlay = itemView.findViewById(R.id.btnPlay);
            btnReset = itemView.findViewById(R.id.btnReset);
            btnAddTime = itemView.findViewById(R.id.btnAddTime);
        }
    }
}