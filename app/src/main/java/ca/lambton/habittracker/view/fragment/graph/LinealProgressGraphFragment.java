package ca.lambton.habittracker.view.fragment.graph;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.antgroup.antv.f2.F2CanvasView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import ca.lambton.habittracker.databinding.FragmentGraphAnalysisBinding;
import ca.lambton.habittracker.habit.model.HabitProgress;
import ca.lambton.habittracker.habit.model.Progress;
import ca.lambton.habittracker.habit.viewmodel.HabitViewModel;
import ca.lambton.habittracker.util.Utils;
import ca.lambton.habittracker.view.fragment.graph.adapter.LinealProgressGraphAdapter;

public class LinealProgressGraphFragment extends Fragment {

    FragmentGraphAnalysisBinding binding;
    private final List<HabitProgress> habitProgressList = new ArrayList<>();
    private HabitViewModel habitViewModel;
    private FirebaseUser mUser;
    private String source;
    private F2CanvasView linealGraphView;
    private HabitProgress habitProgress;

    private LinealProgressGraphFragment() {
    }

    public static LinealProgressGraphFragment newInstance(HabitProgress habitProgress) {
        LinealProgressGraphFragment INSTANCE = new LinealProgressGraphFragment();

        Bundle args = new Bundle();
        args.putSerializable("habit_progress", habitProgress);
        INSTANCE.setArguments(args);
        return INSTANCE;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = FragmentGraphAnalysisBinding.inflate(LayoutInflater.from(requireContext()));
        mUser = FirebaseAuth.getInstance().getCurrentUser();

        linealGraphView = binding.linealGraphView;
        habitViewModel = new ViewModelProvider(requireActivity()).get(HabitViewModel.class);

        assert getArguments() != null;
        habitProgress = (HabitProgress) getArguments().getSerializable("habit_progress");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();

        LocalDate todayDate = LocalDate.now();

        habitViewModel.getAllProgress().observe(requireActivity(), habitProgresses -> {

            List<HabitProgress> habitProgressFiltered = habitProgresses.stream()
                    .filter(dbUser -> dbUser.getHabit().getUserId().equals(mUser.getUid()))
                    .collect(Collectors.toList());

            habitProgressList.clear();
            habitProgressFiltered.forEach(habitProgress -> {
                String startDateString = Utils.parseDate(habitProgress.getHabit().getStartDate());
                String endDateString = Utils.parseDate(habitProgress.getHabit().getEndDate());

                LocalDate startDate = LocalDate.parse(startDateString);
                LocalDate endDate = LocalDate.parse(endDateString);

                if (todayDate.isEqual(startDate) || todayDate.isAfter(startDate) && (todayDate.isEqual(endDate) || (todayDate.isBefore(endDate)))) {
                    habitProgressList.add(habitProgress);

                    AtomicInteger value = new AtomicInteger();
                    JSONArray jsonArray = new JSONArray();
                    Map<String, Integer> groupByDate = habitProgressList.get(0).getProgressList().stream()
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

                        //set data to chart
                        source = jsonArray.toString();
                        System.out.println(source);

                    });
                }
            });

            linealGraphView.setAdapter(new LinealProgressGraphAdapter(source, habitProgress));

        });


    }
}
