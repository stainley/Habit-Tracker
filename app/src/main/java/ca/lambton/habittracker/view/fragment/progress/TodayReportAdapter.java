package ca.lambton.habittracker.view.fragment.progress;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.antgroup.antv.f2.F2CanvasView;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import ca.lambton.habittracker.R;
import ca.lambton.habittracker.habit.model.HabitProgress;
import ca.lambton.habittracker.habit.model.Progress;

public class TodayReportAdapter extends RecyclerView.Adapter<TodayReportAdapter.TodayReportViewHolder> {

    private final List<HabitProgress> habitProgresses;
    private final OnTodayReportChangeListener onTodayReportChangeListener;


    public TodayReportAdapter(List<HabitProgress> habitProgresses, OnTodayReportChangeListener onTodayReportChangeListener) {
        this.habitProgresses = habitProgresses;
        this.onTodayReportChangeListener = onTodayReportChangeListener;


    }

    @NonNull
    @Override
    public TodayReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.progress_graph, parent, false);
        return new TodayReportViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TodayReportViewHolder holder, int position) {
        double frequency = Double.parseDouble(habitProgresses.get(position).getHabit().getFrequency());

        Map<String, Integer> progressList = habitProgresses.get(position).getProgressList()
                .stream()
                .collect(Collectors.groupingBy(Progress::getDate, Collectors.summingInt(Progress::getCounter)));

        progressList.size();
        AtomicReference<Double> result = new AtomicReference<>((double) 0);
        progressList.forEach((date, sum) -> {
            result.updateAndGet(v -> ((double) (v + (sum / frequency) * 100)));
        });

        int percentageValue = (int) (result.get() / progressList.size());

        holder.percentage.setText(String.format(Locale.CANADA, "%d%%", percentageValue));
        holder.progressIndicator.setProgress(percentageValue);
        holder.habitName.setText(habitProgresses.get(position).getHabit().getName());

        this.onTodayReportChangeListener.onGraphChange(holder.canvasView, position);

    }

    @Override
    public int getItemCount() {
        return habitProgresses.size();
    }


    static class TodayReportViewHolder extends RecyclerView.ViewHolder {

        private final CircularProgressIndicator progressIndicator;
        private final TextView habitName;
        private final F2CanvasView canvasView;
        private final TextView percentage;

        public TodayReportViewHolder(@NonNull View itemView) {
            super(itemView);
            progressIndicator = itemView.findViewById(R.id.progress_percent);
            habitName = itemView.findViewById(R.id.habit_text);
            canvasView = itemView.findViewById(R.id.canvas_view);
            percentage = itemView.findViewById(R.id.percent_num);
        }
    }


    public interface OnTodayReportChangeListener {
        void onGraphChange(F2CanvasView canvasView, int position);
    }
}
