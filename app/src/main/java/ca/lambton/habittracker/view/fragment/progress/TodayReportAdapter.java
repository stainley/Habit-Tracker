package ca.lambton.habittracker.view.fragment.progress;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.antgroup.antv.f2.F2CanvasView;
import com.antgroup.antv.f2.F2Chart;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

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

        double totalCompleted = habitProgresses.get(position).getProgressList().stream().count();
        double frequency = Double.parseDouble(habitProgresses.get(position).getHabit().getFrequency());
        int percentageValue = (int) ((totalCompleted / frequency) * 100);
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
        //private final LineChart graphChart;
        private final F2CanvasView canvasView;
        private final TextView percentage;

        public TodayReportViewHolder(@NonNull View itemView) {
            super(itemView);
            progressIndicator = itemView.findViewById(R.id.progress_percent);
            habitName = itemView.findViewById(R.id.habit_text);
            //graphChart = itemView.findViewById(R.id.graph_chart);
            canvasView = itemView.findViewById(R.id.canvas_view);
            percentage = itemView.findViewById(R.id.percent_num);
        }
    }


    public interface OnTodayReportChangeListener {
        void onGraphChange(F2CanvasView canvasView, int position);
    }
}
