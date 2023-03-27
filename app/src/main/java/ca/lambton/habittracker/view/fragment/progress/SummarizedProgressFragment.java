package ca.lambton.habittracker.view.fragment.progress;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import ca.lambton.habittracker.R;
import ca.lambton.habittracker.databinding.FragmentTodayProgressBinding;
import ca.lambton.habittracker.habit.model.HabitProgress;
import ca.lambton.habittracker.habit.model.Progress;
import ca.lambton.habittracker.habit.viewmodel.HabitViewModel;
import ca.lambton.habittracker.util.Frequency;
import ca.lambton.habittracker.util.Utils;

public class SummarizedProgressFragment extends Fragment {
    private FragmentTodayProgressBinding binding;
    private HabitViewModel habitViewModel;
    private float finalResult;

    private float todayProgress = 0;
    private float totalFrequencies;
    private final List<HabitProgress> habitProgresses = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = FragmentTodayProgressBinding.inflate(inflater);

        Fragment recycleDetailedProgress = new TodayDetailProgressFragment();
        getParentFragmentManager().beginTransaction().replace(R.id.fragment_detailed_progress, recycleDetailedProgress).commit();

        habitViewModel = new ViewModelProvider(requireActivity()).get(HabitViewModel.class);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        habitViewModel.getAllProgress().observe(getViewLifecycleOwner(), habitProgresses1 -> {
            //System.out.println("My Progress: " + habitProgresses1.size());
            LocalDate todayDate = LocalDate.now();
            AtomicInteger index = new AtomicInteger();
            habitProgresses.clear();

            for (HabitProgress hp : habitProgresses1) {

                String startDateString = Utils.parseDate(hp.getHabit().getStartDate());
                String endDateString = Utils.parseDate(hp.getHabit().getEndDate());

                LocalDate startDate = LocalDate.parse(startDateString);
                LocalDate endDate = LocalDate.parse(endDateString);

                if (todayDate.isEqual(startDate) || todayDate.isAfter(startDate) && (todayDate.isEqual(endDate) || (todayDate.isBefore(endDate)))) {
                    habitProgresses.add(hp);
                }
            }

            totalFrequencies = 0;
            todayProgress = 0;

            habitProgresses.forEach(habitProgress -> {
                totalFrequencies += habitProgress.getHabit().getFrequency();

                // Filter by daily/weekly
                String startDateString = Utils.parseDate(habitProgress.getHabit().getStartDate());
                String endDateString = Utils.parseDate(habitProgress.getHabit().getEndDate());

                LocalDate startDate = LocalDate.parse(startDateString);
                LocalDate endDate = LocalDate.parse(endDateString);

                if (todayDate.isEqual(startDate) || todayDate.isAfter(startDate) && (todayDate.isEqual(endDate) || (todayDate.isBefore(endDate)))) {
                    if (habitProgresses1.get(index.get()).getHabit().getFrequencyUnit().equalsIgnoreCase(Frequency.DAILY.name())) {
                        todayProgress += habitProgress.getProgressList().stream().filter(progress -> progress.getDate().equals(todayDate.toString())).map(Progress::getCounter).mapToInt(Integer::intValue).sum();
                    } else if (habitProgresses1.get(index.get()).getHabit().getFrequencyUnit().equalsIgnoreCase(Frequency.WEEKLY.name())) {
                        todayProgress += habitProgress.getProgressList().stream().filter(progress -> progress.getDate().equals(todayDate.toString())).map(Progress::getCounter).mapToInt(Integer::intValue).sum();
                    } else if (habitProgresses1.get(index.get()).getHabit().getFrequencyUnit().equalsIgnoreCase(Frequency.MONTHLY.name())) {
                        todayProgress += habitProgress.getProgressList().stream().filter(progress -> progress.getDate().equals(todayDate.toString())).map(Progress::getCounter).mapToInt(Integer::intValue).sum();
                    }
                }

                index.set(index.get() + 1);

            });
            finalResult = (todayProgress / totalFrequencies) * 100;
            binding.percentageNumText.setText((int) finalResult + "%");
            binding.totalDailyProgressbar.setProgress((int) finalResult, true);
        });
    }

    private void calculateAvgProgress(int... progress) {
        // TODO: pass the average of habit process integer
        int sum = Arrays.stream(progress).sum();

        binding.percentageNumText.setText((sum / progress.length) + "%");
        binding.totalDailyProgressbar.setProgress((sum / progress.length), true);
    }
}
