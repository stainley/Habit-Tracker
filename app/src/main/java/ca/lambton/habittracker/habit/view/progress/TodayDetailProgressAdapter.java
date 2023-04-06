package ca.lambton.habittracker.habit.view.progress;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ca.lambton.habittracker.R;

public class TodayDetailProgressAdapter extends RecyclerView.Adapter<TodayDetailProgressAdapter.TodayDetailViewHolder> {

    private final List<TodayDetailProgressFragment.DailyProgressData> progressDataList;

    public TodayDetailProgressAdapter(List<TodayDetailProgressFragment.DailyProgressData> progressDataList) {
        this.progressDataList = progressDataList;
    }

    @NonNull
    @Override
    public TodayDetailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_daily_progress_percentage, parent, false);
        return new TodayDetailViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TodayDetailViewHolder holder, int position) {
        holder.habitNameText.setText(progressDataList.get(position).getHabitName());

        holder.progressImage.setImageDrawable(progressDataList.get(position).getProgressValue());
    }

    @Override
    public int getItemCount() {
        return progressDataList.size();
    }

    static class TodayDetailViewHolder extends RecyclerView.ViewHolder {

        private final TextView habitNameText;
        private final ImageView progressImage;

        public TodayDetailViewHolder(@NonNull View itemView) {
            super(itemView);

            habitNameText = itemView.findViewById(R.id.habit_name_text);
            progressImage = itemView.findViewById(R.id.habit_progress);
        }
    }
}
