package ca.lambton.habittracker.view.fragment.progress;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import java.util.List;

import ca.lambton.habittracker.R;
import ca.lambton.habittracker.habit.model.HabitProgress;

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
        holder.progressIndicator.setProgress(25);
        holder.habitName.setText(habitProgresses.get(position).getHabit().getName());

        this.onTodayReportChangeListener.onGraphChange(holder.graphChart, position);

    }

    @Override
    public int getItemCount() {
        return habitProgresses.size();
    }

    static class TodayReportViewHolder extends RecyclerView.ViewHolder {

        private final CircularProgressIndicator progressIndicator;
        private final TextView habitName;
        private final LineChart graphChart;

        public TodayReportViewHolder(@NonNull View itemView) {
            super(itemView);
            progressIndicator = itemView.findViewById(R.id.progress_percent);
            habitName = itemView.findViewById(R.id.habit_text);
            graphChart = itemView.findViewById(R.id.graph_chart);
        }
    }


    public interface OnTodayReportChangeListener {
        void onGraphChange(LineChart lineChart, int position);
    }
}
