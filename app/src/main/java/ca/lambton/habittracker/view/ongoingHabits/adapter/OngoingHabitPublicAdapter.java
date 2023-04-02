package ca.lambton.habittracker.view.ongoingHabits.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import ca.lambton.habittracker.R;
import ca.lambton.habittracker.habit.model.Habit;
import ca.lambton.habittracker.util.Utils;

public class OngoingHabitPublicAdapter extends RecyclerView.Adapter<OngoingHabitPublicAdapter.OngoingPublicViewHolder> {

    private final List<Habit> habits;
    private final OngoingPublicHabitListener ongoingPublicHabitListener;

    public OngoingHabitPublicAdapter(List<Habit> habits, @NotNull OngoingPublicHabitListener listener) {
        this.habits = habits;
        this.ongoingPublicHabitListener = listener;
    }

    @NonNull
    @Override
    public OngoingPublicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_ongoing_public_habit, parent, false);
        return new OngoingPublicViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OngoingPublicViewHolder holder, int position) {
        holder.habitNameLabel.setText(habits.get(position).getName());

        holder.durationUnit.setText(Utils.capitalize(habits.get(position).getDurationUnit()));
        holder.durationTime.setText(String.valueOf(habits.get(position).getDuration()));
        holder.frequencyUnit.setText(Utils.capitalize(habits.get(position).getFrequencyUnit()));
        holder.frequencyValue.setText(String.valueOf(habits.get(position).getFrequency()));
        holder.frequency.setText(String.valueOf(habits.get(position).getFrequency()));
        holder.publicHabitCardView.setOnClickListener(view -> ongoingPublicHabitListener.onCardClicked(position));
    }

    @Override
    public int getItemCount() {
        return habits.size();
    }

    static final class OngoingPublicViewHolder extends RecyclerView.ViewHolder {
        private final TextView habitNameLabel;
        private final MaterialCardView publicHabitCardView;
        private final TextView frequency;
        private final TextView durationTime;

        private final TextView durationUnit;
        /*private final TextView duration;*/
        private final TextView frequencyValue;
        private final TextView frequencyUnit;

        public OngoingPublicViewHolder(@NonNull View itemView) {
            super(itemView);
            habitNameLabel = itemView.findViewById(R.id.habitNameLabel);
            publicHabitCardView = itemView.findViewById(R.id.ongoingHabitCard);
            frequency = itemView.findViewById(R.id.frequency);
            durationUnit = itemView.findViewById(R.id.duration_value);
            frequencyValue = itemView.findViewById(R.id.frequency_unit_value);
            frequencyUnit = itemView.findViewById(R.id.frequency_unit);
            durationTime = itemView.findViewById(R.id.duration_time);
        }
    }

    public interface OngoingPublicHabitListener {
        void onCardClicked(int position);

    }
}
