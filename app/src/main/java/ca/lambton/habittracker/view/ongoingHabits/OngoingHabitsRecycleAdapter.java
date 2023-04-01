package ca.lambton.habittracker.view.ongoingHabits;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import ca.lambton.habittracker.R;
import ca.lambton.habittracker.habit.model.Habit;

public class OngoingHabitsRecycleAdapter extends RecyclerView.Adapter<OngoingHabitsRecycleAdapter.ViewHolder> {

    private final OnOngoingHabitsCallback onOngoingHabitsCallback;
    private final List<Habit> habits;
    private Context context;

    private Boolean isGroup;

    public OngoingHabitsRecycleAdapter(List<Habit> habits, OnOngoingHabitsCallback onCallback) {
        this.habits = habits;
        this.onOngoingHabitsCallback = onCallback;
    }

    public OngoingHabitsRecycleAdapter(List<Habit> habits, OnOngoingHabitsCallback onCallback, Context context, Boolean isGroup) {
        this.habits = habits;
        this.onOngoingHabitsCallback = onCallback;
        this.context = context;
        this.isGroup = isGroup;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.card_ongoing_habit_layout, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        int totalTimesToComplete = 0;
        String frequencyUnit = habits.get(position).getFrequencyUnit();
        int frequencyValue = habits.get(position).getFrequency();
        long startDateMillis = habits.get(position).getStartDate();
        long endDateMillis = habits.get(position).getEndDate();

        LocalDate startDate = Instant.ofEpochMilli(startDateMillis).atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate endDate = Instant.ofEpochMilli(endDateMillis).atZone(ZoneId.systemDefault()).toLocalDate();
        long daysBetween = ChronoUnit.DAYS.between(startDate, endDate);

        if (frequencyUnit.equals("DAILY")) {
            totalTimesToComplete = frequencyValue * (int) daysBetween;
            onOngoingHabitsCallback.getProgressList(holder.habitPercentageNumText, totalTimesToComplete, position);
        } else if (frequencyUnit.equals("WEEKLY")) {
            totalTimesToComplete = frequencyValue * ((int) daysBetween / 7);
            onOngoingHabitsCallback.getProgressList(holder.habitPercentageNumText, totalTimesToComplete, position);
        } else {
            totalTimesToComplete = frequencyValue * ((int) daysBetween / 30);
            onOngoingHabitsCallback.getProgressList(holder.habitPercentageNumText, totalTimesToComplete, position);
        }

        holder.habitNameLabel.setText(habits.get(position).getName());
        holder.ongoingHabitCard.setOnClickListener(view -> {
            onOngoingHabitsCallback.onRowClicked(position, isGroup);
        });

        if (isGroup) {
            holder.memberCountLabel.setText("2 Members");
        } else {
            holder.memberCountLabel.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return habits.size();
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView habitNameLabel;
        private final TextView memberCountLabel;

        private final TextView habitPercentageNumText;

        private final CardView ongoingHabitCard;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            habitNameLabel = itemView.findViewById(R.id.habitNameLabel);
            memberCountLabel = itemView.findViewById(R.id.memberCountLabel);
            habitPercentageNumText = itemView.findViewById(R.id.habitPercentageNumText);
            ongoingHabitCard = itemView.findViewById(R.id.ongoingHabitCard);
        }
    }

    public interface OnOngoingHabitsCallback {
        void onRowClicked(int position, boolean isGroup);

        int getProgressList(TextView habitPercentageNumText, int totalTimesToComplete, int position);
    }
}
