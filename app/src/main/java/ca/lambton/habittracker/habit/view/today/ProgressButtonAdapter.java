package ca.lambton.habittracker.habit.view.today;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.progressindicator.CircularProgressIndicator;

import java.time.LocalDate;
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
        LocalDate todayDate = LocalDate.now();
        holder.increaseButton.setOnClickListener(v -> onHabitOperationCallback.onIncreaseClick(holder.increaseButton, position));
        holder.decreaseButton.setOnClickListener(v -> onHabitOperationCallback.onDecreaseClick(holder.decreaseButton, position));

        holder.habitTitle.setText(habitProgresses.get(position).getHabit().getName());
        StringBuilder progressText = new StringBuilder();
        int frequency = habitProgresses.get(position).getHabit().getFrequency();

        float[] progressNumeric = new float[1];
        holder.progressIndicator.setProgress(0, true);

        if (habitProgresses.get(position).getProgressList().size() > 0) {

            habitProgresses.get(position).getProgressList().forEach(progress -> {
                LocalDate progressUpdated = LocalDate.parse(progress.getDate());

                if (todayDate.isEqual(progressUpdated)) {

                    float counter = habitProgresses.get(position).getProgressList().stream()
                            .filter(pro -> pro.getDate().equalsIgnoreCase(progressUpdated.toString()))
                            .mapToInt(Progress::getCounter).sum();

                    float freq = habitProgresses.get(position).getHabit().getFrequency();

                    float result = (counter / freq) * 100;
                    progressNumeric[0] = counter;

                    holder.progressIndicator.setProgress((int) result, false);
                }
            });

            progressText.append("").append((int) progressNumeric[0]);
        } else {
            progressText.append("0");
        }
        progressText
                .append("/")
                .append(frequency);
        holder.progressNumeric.setText(progressText.toString());
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
