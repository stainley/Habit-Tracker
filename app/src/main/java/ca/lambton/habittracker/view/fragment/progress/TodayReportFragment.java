package ca.lambton.habittracker.view.fragment.progress;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.antgroup.antv.f2.F2CanvasView;
import com.antgroup.antv.f2.F2Chart;
import com.github.mikephil.charting.charts.HorizontalBarChart;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import ca.lambton.habittracker.databinding.FragmentRecycleViewBinding;
import ca.lambton.habittracker.habit.model.HabitProgress;
import ca.lambton.habittracker.habit.viewmodel.HabitViewModel;
import ca.lambton.habittracker.habit.viewmodel.HabitViewModelFactory;

public class TodayReportFragment extends Fragment implements F2CanvasView.Adapter {

    private FragmentRecycleViewBinding binding;
    private TodayReportAdapter todayReportAdapter;
    private final List<HabitProgress> habitProgressList = new ArrayList<>();
    private HabitViewModel habitViewModel;

    //private F2Chart mChart;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = FragmentRecycleViewBinding.inflate(LayoutInflater.from(requireContext()));
        RecyclerView recycleView = binding.recycleView;
        habitViewModel = new ViewModelProvider(requireActivity(), new HabitViewModelFactory(requireActivity().getApplication())).get(HabitViewModel.class);
       /* if (mChart == null) {
            mChart = F2Chart.create(requireContext(), "LineChart-Kotlin", 700, 350);
        }*/
        todayReportAdapter = new TodayReportAdapter(habitProgressList, (canvasView, position) -> {


            /*chart = lineChart;
            LineData lineData = setData(7, 12.0f);
            lineChart.setData(lineData);
            lineChart.notifyDataSetChanged();*/
            canvasView.setAdapter(new F2CanvasView.Adapter() {
                F2Chart mChart;

                @Override
                public void onCanvasDraw(F2CanvasView canvasView) {

                    //Initialize the chart, because you need the width and height of the canvasView,
                    //initialize here to ensure that you can get the width and height of the canvasView

                    if (mChart == null) {
                        mChart = F2Chart.create(canvasView.getContext(), "Report", canvasView.getWidth(), canvasView.getHeight());
                    }
                    // 700 x 350
                    //Associating the chart and canvasView, the chart finally needs to display the chart on the canvasView
                    mChart.setCanvas(canvasView);
                    //set chart padding
                    mChart.padding(10.0, 10.0, 10.0, 10.0);
                    //load data from data.json

                    //TODO: obtain all records in one day
                    JSONArray jsonArray = new JSONArray();

                    AtomicInteger counter = new AtomicInteger();
                    AtomicInteger value = new AtomicInteger();
                    habitProgressList.get(position).getProgressList().forEach(progress -> {
                        value.getAndIncrement();
                        JSONObject jsonObject = new JSONObject();
                        try {

                            counter.addAndGet(progress.getCounter());
                            jsonObject.put("progress", counter.get());

                            new Date(progress.getUpdatedDate());
                            jsonObject.put("date", value.get());

                            jsonArray.put(jsonObject);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                        System.out.println(progress.getCounter() + "-" + new Date(progress.getUpdatedDate()));
                    });


                    //set data to chart

                    String source = jsonArray.toString();
                    System.out.println(source);
                    mChart.source(source);

                    mChart.setScale("progress", new F2Chart.ScaleConfigBuilder().precision(0).max(Double.parseDouble(habitProgressList.get(position).getHabit().getFrequency())).min(0));
                    mChart.setScale("date", new F2Chart.ScaleConfigBuilder().precision(1).tickCount(7).range(new double[]{0.1, 0.9}));


                    //Draw a polyline on the chart. The data mappings of the x-axis and y-axis of the polyline are genre and sold fields respectively.
                    mChart.line().color("type", new String[]{"ORANGE"})
                            .fixedSize(2.f)
                            .fixedShape("smooth")
                            .position("date*progress");

                    //Render and display on canvasView
                    mChart.render();
                }

                @Override
                public void onDestroy() {
                    if (mChart != null) {
                        mChart.destroy();
                    }
                }
            });

        });

        recycleView.setLayoutManager(new GridLayoutManager(requireContext(), 2, LinearLayoutManager.VERTICAL, false));
        recycleView.setAdapter(todayReportAdapter);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        habitViewModel.getAllProgress().observe(requireActivity(), habitProgresses -> {
            habitProgressList.clear();
            habitProgresses.forEach(habitProgress -> {
                habitProgressList.add(habitProgress);
            });

            todayReportAdapter.notifyDataSetChanged();
        });

        return binding.getRoot();
    }


    private static void onGraphChange(HorizontalBarChart barChart, int position) {

    }

    @Override
    public void onCanvasDraw(F2CanvasView f2CanvasView) {

    }


}
