package ca.lambton.habittracker.view.today;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.progressindicator.CircularProgressIndicator;

import java.util.List;

import ca.lambton.habittracker.R;
import ca.lambton.habittracker.habit.model.HabitProgress;
import ca.lambton.habittracker.habit.model.Progress;

public class ProgressButtonAdapter extends RecyclerView.Adapter<ProgressButtonAdapter.ProgressButtonViewHolder> {

    private final List<HabitProgress> habitProgresses;
    private final OnHabitOperationCallback onHabitOperationCallback;

    public ProgressButtonAdapter(List<HabitProgress> habitProgresses, OnHabitOperationCallback onHabitOperationCallback) {
        this.habitProgresses = habitProgresses;
        this.onHabitOperationCallback = onHabitOperationCallback;
    }

    @NonNull
    @Override
    public ProgressButtonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_today_progress, parent, false);
        return new ProgressButtonViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProgressButtonViewHolder holder, int position) {

        holder.increaseButton.setOnClickListener(v -> onHabitOperationCallback.onIncreaseClick(holder.increaseButton, position));
        holder.decreaseButton.setOnClickListener(v -> onHabitOperationCallback.onDecreaseClick(holder.decreaseButton, position));

        holder.habitTitle.setText(habitProgresses.get(position).getHabit().getName());
        StringBuilder progressBuilder = new StringBuilder();
        String frequency = habitProgresses.get(position).getHabit().getFrequency();


        if (habitProgresses.get(position).getProgressList().size() > 0) {
            float counter = habitProgresses.get(position).getProgressList().stream().mapToInt(Progress::getCounter).sum();
            float freq = Float.parseFloat(habitProgresses.get(position).getHabit().getFrequency());

            float result = (counter / freq) * 100;

            progressBuilder.append("" + (int) counter);
            holder.progressIndicator.setProgress((int) result, true);
        } else {
            progressBuilder.append("0");
            holder.progressIndicator.setProgress(0, true);
        }
        progressBuilder
                .append("/")
                .append(frequency);
        holder.progressNumeric.setText(progressBuilder.toString());
    }

    @Override
    public int getItemCount() {
        return habitProgresses.size();
    }

    protected static class ProgressButtonViewHolder extends RecyclerView.ViewHolder {
        private final CircularProgressIndicator progressIndicator;
        private final TextView habitTitle;
        private final TextView progressNumeric;
        private final ImageButton increaseButton;
        private final ImageButton decreaseButton;

        public ProgressButtonViewHolder(@NonNull View itemView) {
            super(itemView);
            increaseButton = itemView.findViewById(R.id.increase_button);
            decreaseButton = itemView.findViewById(R.id.decrease_button);
            progressIndicator = itemView.findViewById(R.id.progress_indicator);
            habitTitle = itemView.findViewById(R.id.habit_title);
            progressNumeric = itemView.findViewById(R.id.progress_numeric);
        }
    }

    public interface OnHabitOperationCallback {
        void onIncreaseClick(View view, int position);

        void onDecreaseClick(View view, int position);
    }
}
