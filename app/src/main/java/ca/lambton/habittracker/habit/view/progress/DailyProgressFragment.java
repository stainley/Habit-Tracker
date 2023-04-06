package ca.lambton.habittracker.habit.view.progress;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import ca.lambton.habittracker.databinding.FragmentDailyHabitProgressBinding;
import ca.lambton.habittracker.habit.model.HabitProgress;
import ca.lambton.habittracker.habit.model.Progress;
import ca.lambton.habittracker.habit.viewmodel.HabitViewModel;

public class DailyProgressFragment extends Fragment {
    private FragmentDailyHabitProgressBinding binding;
    private HabitViewModel habitViewModel;
    private FirebaseUser mUser;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = FragmentDailyHabitProgressBinding.inflate(LayoutInflater.from(requireContext()));
        habitViewModel = new ViewModelProvider(requireActivity()).get(HabitViewModel.class);


    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LocalDate today = LocalDate.now();


        habitViewModel.getAllProgress().observe(getViewLifecycleOwner(), habitProgresses -> {
            AtomicInteger totalHabit = new AtomicInteger();
            AtomicInteger habitCompleted = new AtomicInteger();


            List<HabitProgress> myHabitProgressFiltered = habitProgresses.stream()
                    .filter(dbUser -> dbUser.getHabit().getUserId().equals(mUser.getUid()))
                    .collect(Collectors.toList());

            myHabitProgressFiltered.forEach(habitProgress -> {

                Map<Long, Integer> summarizedByHabitId = habitProgress.getProgressList().stream()
                        .filter(oldDate -> LocalDate.parse(oldDate.getDate()).isEqual(today) || LocalDate.parse(oldDate.getDate()).isAfter(today))
                        .collect(Collectors.groupingBy(Progress::getHabitId, Collectors.summingInt(Progress::getCounter)));
                totalHabit.getAndIncrement();

                summarizedByHabitId.forEach((habitId, habitProgressNumber) -> {

                    if (habitProgress.getHabit().getId() == habitId && habitProgress.getHabit().getFrequency() == habitProgressNumber) {
                        habitCompleted.getAndIncrement();
                    }
                });

                String completedDaily = habitCompleted.get() + "/" + totalHabit.get();
                binding.completedDailyHabitLabel.setText(completedDaily);
            });
        });
    }
}
