package ca.lambton.habittracker.habit.view.fragment.complete;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import ca.lambton.habittracker.databinding.DetailedCompleteHabitBinding;
import ca.lambton.habittracker.habit.model.Habit;
import ca.lambton.habittracker.habit.model.HabitProgress;
import ca.lambton.habittracker.habit.model.Progress;
import ca.lambton.habittracker.util.Frequency;
import ca.lambton.habittracker.util.Utils;

public class CompleteStreakFragment extends Fragment {

    private DetailedCompleteHabitBinding binding;
    private HabitProgress habitProgress;

    public CompleteStreakFragment() {
    }

    public CompleteStreakFragment newInstance(HabitProgress habitProgress) {
        CompleteStreakFragment completeStreakFragment = new CompleteStreakFragment();

        Bundle bundle = new Bundle();
        bundle.putSerializable("habit_progress", habitProgress);
        completeStreakFragment.setArguments(bundle);
        return completeStreakFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DetailedCompleteHabitBinding.inflate(LayoutInflater.from(requireContext()));


    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        return binding.getRoot();
    }


    @Override
    public void onStart() {
        super.onStart();

        assert getArguments() != null;
        habitProgress = (HabitProgress) getArguments().getSerializable("habit_progress");

        if (habitProgress != null) {

            Habit habit = habitProgress.getHabit();

            double frequency = habitProgress.getHabit().getFrequency();
            Map<String, Integer> progressMap = habitProgress.getProgressList()
                    .stream()
                    .collect(Collectors.groupingBy(Progress::getDate, Collectors.summingInt(Progress::getCounter)));

            long numbersOfDaysEntered = progressMap.values().stream()
                    .filter(value -> value == frequency).mapToInt(Integer::intValue).count();

            binding.numDaysValue.setText(String.valueOf(numbersOfDaysEntered));

            /*double sumOfPercentages = progressMap.values().stream()
                    .mapToDouble(sum -> (sum / frequency) * 100)
                    .count();
*/
            long totalDays = Utils.getTotalDays(habit);

            long numbersOfDayMissed = totalDays - numbersOfDaysEntered;
            binding.daysMissedValue.setText(String.valueOf(numbersOfDayMissed));

            String maxStreak = String.valueOf(Math.max(Utils.maxNumbersOfStreak(habitProgress), Utils.currentNumberOfStreak(habitProgress)));
            binding.highestStreakValue.setText(maxStreak);
        }
    }


}
