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
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import ca.lambton.habittracker.databinding.FragmentDailyHabitProgressBinding;
import ca.lambton.habittracker.habit.model.Progress;
import ca.lambton.habittracker.habit.viewmodel.HabitViewModel;
import ca.lambton.habittracker.habit.viewmodel.HabitViewModelFactory;

public class DailyProgressFragment extends Fragment {

    private FragmentDailyHabitProgressBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentDailyHabitProgressBinding.inflate(inflater);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LocalDate today = LocalDate.now();
        HabitViewModel habitViewModel = new ViewModelProvider(getViewModelStore(), new HabitViewModelFactory(requireActivity().getApplication())).get(HabitViewModel.class);

        habitViewModel.getAllProgress().observe(getViewLifecycleOwner(), habitProgresses -> {
            AtomicInteger totalHabit = new AtomicInteger();
            AtomicInteger habitCompleted = new AtomicInteger();

            habitProgresses.forEach(habitProgress -> {

                Map<Long, Integer> summarizedByHabitId = habitProgress.getProgressList().stream()
                        .filter(oldDate -> LocalDate.parse(oldDate.getDate()).isEqual(today) || LocalDate.parse(oldDate.getDate()).isAfter(today))
                        .collect(Collectors.groupingBy(Progress::getHabitId, Collectors.summingInt(Progress::getCounter)));

                summarizedByHabitId.forEach((habitId, habitProgressNumber) -> {
                    totalHabit.getAndIncrement();

                    if (habitProgress.getHabit().getId() == habitId && habitProgress.getHabit().getFrequency() == habitProgressNumber) {
                        habitCompleted.getAndIncrement();
                    }
                });

                binding.completedDailyHabitLabel.setText(habitCompleted.get() + "/" + totalHabit.get());
            });
        });
    }
}
