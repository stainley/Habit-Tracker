package ca.lambton.habittracker.habit.view.progress;

import android.icu.util.LocaleData;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import ca.lambton.habittracker.databinding.FragmentRecycleViewBinding;
import ca.lambton.habittracker.habit.model.HabitProgress;
import ca.lambton.habittracker.habit.model.Progress;
import ca.lambton.habittracker.habit.viewmodel.HabitViewModel;
import ca.lambton.habittracker.util.Utils;

public class TodayReportFragment extends Fragment {

    private FragmentRecycleViewBinding binding;
    private TodayReportAdapter todayReportAdapter;
    private final List<HabitProgress> habitProgressList = new ArrayList<>();
    private HabitViewModel habitViewModel;
    private FirebaseUser mUser;
    private RecyclerView recycleView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = FragmentRecycleViewBinding.inflate(LayoutInflater.from(requireContext()));
        recycleView = binding.recycleView;
        habitViewModel = new ViewModelProvider(requireActivity()).get(HabitViewModel.class);

        mUser = FirebaseAuth.getInstance().getCurrentUser();

        recycleView.setLayoutManager(new GridLayoutManager(requireContext(), 2, LinearLayoutManager.VERTICAL, false));
        recycleView.setAdapter(todayReportAdapter);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        todayReportAdapter = new TodayReportAdapter(habitProgressList, (canvasView, position) -> canvasView.setAdapter(new F2CanvasView.Adapter() {
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

                JSONArray jsonArray = new JSONArray();

                AtomicInteger value = new AtomicInteger();

                Map<String, Integer> groupByDate = habitProgressList.get(position).getProgressList().stream()
                        .collect(Collectors.groupingBy(Progress::getDate, Collectors.summingInt(Progress::getCounter)));


                groupByDate.forEach((date, totalPerDay) -> {
                    value.getAndIncrement();

                    JSONObject jsonObject = new JSONObject();
                    try {

                        jsonObject.put("progress", totalPerDay);
                        jsonObject.put("date", date);

                        jsonArray.put(jsonObject);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                });

                //set data to chart
                String source = jsonArray.toString();
                System.out.println(source);
                mChart.source(source);

                mChart.setScale("progress", new F2Chart.ScaleConfigBuilder().precision(0).max(habitProgressList.get(position).getHabit().getFrequency()).min(0));
                mChart.setScale("date", new F2Chart.ScaleConfigBuilder().precision(0).tickCount(2).range(new double[]{0.1, 0.9}));

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
        }));

        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();

        LocalDate todayDate = LocalDate.now();
        habitViewModel.getAllProgress().observe(requireActivity(), habitProgresses -> {

            List<HabitProgress> habitProgressFiltered = habitProgresses.stream().filter(dbUser -> dbUser.getHabit().getUserId().equals(mUser.getUid())).collect(Collectors.toList());

            habitProgressList.clear();
            habitProgressFiltered.forEach(habitProgress -> {

                String startDateString = Utils.parseDate(habitProgress.getHabit().getStartDate());
                String endDateString = Utils.parseDate(habitProgress.getHabit().getEndDate());

                LocalDate startDate = LocalDate.parse(startDateString);
                LocalDate endDate = LocalDate.parse(endDateString);

                if (todayDate.isEqual(startDate) || todayDate.isAfter(startDate) && (todayDate.isEqual(endDate) || (todayDate.isBefore(endDate)))) {
                    habitProgressList.add(habitProgress);
                }
            });

            todayReportAdapter.notifyDataSetChanged();
        });
        recycleView.setAdapter(todayReportAdapter);

    }
}
