package ca.lambton.habittracker.habit.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.squareup.picasso.Picasso;

import java.util.List;

import ca.lambton.habittracker.R;
import ca.lambton.habittracker.habit.model.HabitProgress;
import ca.lambton.habittracker.habit.model.Progress;
import de.hdodenhof.circleimageview.CircleImageView;

public class CompleteHabitAdapter extends RecyclerView.Adapter<CompleteHabitAdapter.CompleteHabitViewModel> {


    private final List<HabitProgress> habitProgresses;
    private final OnCompleteListener onCompleteListener;

    public CompleteHabitAdapter(List<HabitProgress> habitProgresses, OnCompleteListener onCompleteListener) {
        this.habitProgresses = habitProgresses;
        this.onCompleteListener = onCompleteListener;
    }


    @NonNull
    @Override
    public CompleteHabitViewModel onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_habit_complete_row, parent, false);
        return new CompleteHabitViewModel(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CompleteHabitViewModel holder, int position) {
        holder.habitName.setText(habitProgresses.get(position).getHabit().getName());

        this.onCompleteListener.onCompleteRowHandler(holder.imageHabit, holder.completeHabitCard, position);

        int progress = computeProgress(habitProgresses.get(position));
        String progressText = progress + "%";
        holder.progressText.setText(progressText);
        holder.circularProgressIndicator.setProgress(progress);
    }

    private int computeProgress(HabitProgress habitProgress) {
        float progressTotal = 0;
        float totalFrequencies = habitProgress.getHabit().getFrequency();

        progressTotal += habitProgress.getProgressList().stream()
                .filter(progress -> progress.getHabitId() == habitProgress.getHabit().getId())
                .map(Progress::getCounter)
                .mapToInt(Integer::intValue).sum();
        float result = (progressTotal / totalFrequencies) * 100;

        return (int) result;
    }


    @Override
    public int getItemCount() {
        return habitProgresses.size();
    }

    public static final class CompleteHabitViewModel extends RecyclerView.ViewHolder {

        private final TextView habitName;
        private final CircleImageView imageHabit;
        private final MaterialCardView completeHabitCard;
        private final CircularProgressIndicator circularProgressIndicator;
        private final TextView progressText;

        public CompleteHabitViewModel(@NonNull View itemView) {
            super(itemView);
            habitName = itemView.findViewById(R.id.habitNameLabel);
            imageHabit = itemView.findViewById(R.id.image_habit);
            completeHabitCard = itemView.findViewById(R.id.complete_habit_card);
            circularProgressIndicator = itemView.findViewById(R.id.habit_progressbar);
            progressText = itemView.findViewById(R.id.progress_text);
        }
    }

    public interface OnCompleteListener {
        void onCompleteRowHandler(CircleImageView circleImageView, MaterialCardView completeHabitCard, int position);
    }
}
