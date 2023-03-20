package ca.lambton.habittracker.view.ongoingHabits;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

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

        holder.habitNameLabel.setText(habits.get(position).getName());
        holder.habitPercentageNumText.setText("73%");
        holder.ongoingHabitCard.setOnClickListener(view -> {
            onOngoingHabitsCallback.onRowClicked(position);
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
        void onRowClicked(int position);
    }
}
