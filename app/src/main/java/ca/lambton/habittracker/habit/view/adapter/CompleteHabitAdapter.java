package ca.lambton.habittracker.habit.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ca.lambton.habittracker.R;
import ca.lambton.habittracker.habit.model.HabitProgress;
import ca.lambton.habittracker.util.Utils;

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

        int progress = Utils.computeProgress(habitProgresses.get(position));
        String progressText = progress + "%";
        holder.progressText.setText(progressText);
        holder.circularProgressIndicator.setProgress(progress);


        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String endDate = simpleDateFormat.format(new Date(habitProgresses.get(position).getHabit().getEndDate()));
        String startDate = simpleDateFormat.format(new Date(habitProgresses.get(position).getHabit().getEndDate()));

        holder.startDateText.setText(startDate);
        holder.endDateText.setText(endDate);
    }

    @Override
    public int getItemCount() {
        return habitProgresses.size();
    }

    public static final class CompleteHabitViewModel extends RecyclerView.ViewHolder {

        private final TextView habitName;
        private final ImageView imageHabit;
        private final MaterialCardView completeHabitCard;
        private final CircularProgressIndicator circularProgressIndicator;
        private final TextView progressText;
        private final TextView startDateText;
        private final TextView endDateText;

        public CompleteHabitViewModel(@NonNull View itemView) {
            super(itemView);
            habitName = itemView.findViewById(R.id.habitNameLabel);
            imageHabit = itemView.findViewById(R.id.image_habit);
            completeHabitCard = itemView.findViewById(R.id.complete_habit_card);
            circularProgressIndicator = itemView.findViewById(R.id.habit_progressbar);
            progressText = itemView.findViewById(R.id.progress_text);
            startDateText = itemView.findViewById(R.id.start_date);
            endDateText = itemView.findViewById(R.id.end_date);
        }
    }

    public interface OnCompleteListener {
        void onCompleteRowHandler(ImageView circleImageView, MaterialCardView completeHabitCard, int position);
    }
}
