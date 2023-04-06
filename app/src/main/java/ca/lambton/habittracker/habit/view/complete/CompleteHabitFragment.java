package ca.lambton.habittracker.habit.view.complete;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.google.android.material.snackbar.Snackbar;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import ca.lambton.habittracker.R;
import ca.lambton.habittracker.common.fragment.graph.LinealProgressGraphFragment;
import ca.lambton.habittracker.databinding.FragmentCompleteHabitBinding;
import ca.lambton.habittracker.habit.model.HabitProgress;
import ca.lambton.habittracker.habit.model.Progress;
import ca.lambton.habittracker.habit.view.GraphData;
import ca.lambton.habittracker.habit.view.fragment.complete.CompleteStreakFragment;
import ca.lambton.habittracker.habit.viewmodel.HabitViewModel;
import ca.lambton.habittracker.habit.viewmodel.HabitViewModelFactory;
import ca.lambton.habittracker.util.Utils;
import ca.lambton.habittracker.util.calendar.monthly.CustomCalendarView;


public class CompleteHabitFragment extends Fragment {

    private FragmentCompleteHabitBinding binding;
    private TextView habitNameLabel;
    private HabitProgress habitProgress;
    private HabitViewModel habitViewModel;
    private final List<GraphData> graphDataList = new ArrayList<>();
    private CustomCalendarView progressCalendarView;
    private final HashSet<Date> progressDate = new HashSet<>();
    private final ArrayList<String> progressValue = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        binding = FragmentCompleteHabitBinding.inflate(LayoutInflater.from(requireContext()));
        this.habitNameLabel = binding.habitNameLabel;
        progressCalendarView = binding.calendarView;

        habitProgress = CompleteHabitFragmentArgs.fromBundle(requireArguments()).getHabitProgress();
        habitViewModel = new ViewModelProvider(getViewModelStore(), new HabitViewModelFactory(requireActivity().getApplication())).get(HabitViewModel.class);

        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragmentManager parentFragmentManager = getParentFragmentManager();

        // Linear chart of percentage by date
        Fragment graphFragment = LinealProgressGraphFragment.newInstance(graphDataList);
        parentFragmentManager.beginTransaction().replace(R.id.progress_chart_container, graphFragment).commit();


        Fragment completeStreakFragment = new CompleteStreakFragment().newInstance(habitProgress);
        parentFragmentManager.beginTransaction().replace(R.id.complete_information_detailed, completeStreakFragment).commit();


        binding.deleteHabitCard.setOnClickListener(this::deleteHabit);



        return binding.getRoot();
    }

    private void deleteHabit(View view) {
        if (habitProgress != null) {

            Snackbar.make(view, "Would you like to delete this habit?", Toast.LENGTH_SHORT)
                    .setAnchorView(view)
                    .setAction("Yes", v -> {
                        habitViewModel.deleteHabit(habitProgress.getHabit());
                        Navigation.findNavController(view).popBackStack();
                    }).show();
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {


        if (habitProgress != null) {
            this.habitNameLabel.setText(habitProgress.getHabit().getName());
            int resultHabitProgress = Utils.computeProgress(habitProgress);
            binding.habitProgressbar.setProgress(resultHabitProgress);
            binding.habitPercentageNumText.setText(resultHabitProgress + "%");
            String congratulationMessage = "You were " + resultHabitProgress + "% consistent in your habit";
            binding.congratulationText.setText(congratulationMessage);
        }

        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Get the habit frequency
        int frequency = habitProgress.getHabit().getFrequency();

        // Group by date and summarize the progress
        Map<String, Integer> groupByDate = habitProgress.getProgressList()
                .stream()
                .collect(Collectors.groupingBy(Progress::getDate, Collectors.summingInt(Progress::getCounter)));

        groupByDate.forEach((date, summarize) -> {
            float total = ((float) summarize / (float) frequency) * 100;
            graphDataList.add(new GraphData(Math.round(total), date));

            LocalDate localDate = LocalDate.parse(date);


            Calendar calendar = Calendar.getInstance(Locale.CANADA);
            calendar.set(Calendar.YEAR, localDate.getYear());
            calendar.set(Calendar.MONTH, localDate.getMonthValue() - 1);
            calendar.set(Calendar.DAY_OF_MONTH, localDate.getDayOfMonth());

            Date dayProgress = calendar.getTime();

            progressDate.add(dayProgress);
            progressValue.add(String.valueOf(Math.round(total)));
        });

        progressCalendarView.updateCalendar(progressDate, progressValue);
    }
}
