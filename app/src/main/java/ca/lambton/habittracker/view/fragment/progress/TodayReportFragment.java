package ca.lambton.habittracker.view.fragment.progress;

import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IFillFormatter;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.Utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ca.lambton.habittracker.R;
import ca.lambton.habittracker.databinding.FragmentRecycleViewBinding;
import ca.lambton.habittracker.habit.model.Habit;
import ca.lambton.habittracker.habit.model.HabitProgress;
import ca.lambton.habittracker.habit.model.Progress;

public class TodayReportFragment extends Fragment {

    private FragmentRecycleViewBinding binding;
    private TodayReportAdapter todayReportAdapter;

    private LineChart chart;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = FragmentRecycleViewBinding.inflate(LayoutInflater.from(requireContext()));
        RecyclerView recycleView = binding.recycleView;

        List<HabitProgress> habitProgressList = new ArrayList<>();
        Habit habit = new Habit();
        habit.setId(1);
        habit.setName("Running");

        List<Progress> progressList = new ArrayList<>();
        Progress progress1 = new Progress();
        progress1.setCounter(1);
        progress1.setUpdatedDate(new Date().getTime());
        progress1.setHabitId(1);
        progressList.add(progress1);


        Progress progress2 = new Progress();
        progress2.setCounter(1);
        progress2.setUpdatedDate(new Date().getTime());
        progress2.setHabitId(1);
        progressList.add(progress2);


        habitProgressList.add(new HabitProgress(habit, progressList));


        todayReportAdapter = new TodayReportAdapter(habitProgressList, (lineChart, position) -> {
            chart = lineChart;
            LineData lineData = setData(7, 12.0f);
            lineChart.setData(lineData);
            lineChart.notifyDataSetChanged();
        });

        recycleView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        recycleView.setAdapter(todayReportAdapter);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return binding.getRoot();
    }

    private static void onGraphChange(HorizontalBarChart barChart, int position) {

    }

    private LineData setData(int count, float range) {
        LineData data = null;
        ArrayList<Entry> values = new ArrayList<>();

        for (int i = 0; i < count; i++) {

            float val = (float) (Math.random() * range) - 30;
            values.add(new Entry(i, val));
        }

        LineDataSet set1;

        if (chart.getData() != null &&
                chart.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet) chart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            set1.notifyDataSetChanged();
            chart.getData().notifyDataChanged();
            chart.notifyDataSetChanged();
        } else {
            // create a dataset and give it a type
            set1 = new LineDataSet(values, "");

            set1.setDrawIcons(false);
            //Remove circle from the line chart
            set1.setDrawCircles(false);
            set1.setHighlightEnabled(false);

            // draw dashed line
            //set1.enableDashedLine(10f, 5f, 0f);

            // black lines and points
            set1.setColor(getResources().getColor(R.color.yellow));
            set1.setCircleColor(getResources().getColor(R.color.yellow));

            // line thickness and point size
            set1.setLineWidth(2f);
            //set1.setCircleRadius(3f);

            // draw points as solid circles
            set1.setDrawCircleHole(false);

            // customize legend entry
            /*set1.setFormLineWidth(1f);
            set1.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
            set1.setFormSize(15.f);
*/

            set1.setDrawHighlightIndicators(false);
            set1.setDrawVerticalHighlightIndicator(false);
            set1.setDrawHorizontalHighlightIndicator(false);

            //Remove value from the chart
            set1.setDrawValues(false);

            // text size of values
            set1.setValueTextSize(9f);

            // draw selection line as dashed
            //set1.enableDashedHighlightLine(10f, 5f, 0f);

            // set the filled area
            set1.setDrawFilled(false);
            set1.setFillFormatter(new IFillFormatter() {
                @Override
                public float getFillLinePosition(ILineDataSet dataSet, LineDataProvider dataProvider) {
                    return chart.getAxisLeft().getAxisMinimum();
                }
            });

            // set color of filled area

            set1.setFillColor(Color.BLACK);

            ArrayList<ILineDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1); // add the data sets

            // create a data object with the data sets
            data = new LineData(dataSets);

            // set data

        }
        return data;
    }
}
