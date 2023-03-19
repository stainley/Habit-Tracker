package ca.lambton.habittracker.view.today;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import ca.lambton.habittracker.R;
import ca.lambton.habittracker.databinding.FragmentTodayDueProgressBinding;
import ca.lambton.habittracker.habit.model.HabitProgress;
import ca.lambton.habittracker.habit.model.Progress;
import ca.lambton.habittracker.habit.viewmodel.HabitViewModel;
import ca.lambton.habittracker.habit.viewmodel.HabitViewModelFactory;
import ca.lambton.habittracker.view.fragment.calendar.ProgressCalendarFragment;

public class DueTodayProgressFragment extends Fragment {

    private HabitViewModel habitViewModel;
    FragmentTodayDueProgressBinding binding;
    RecyclerView progressButtonsRecycleView;
    private final List<HabitProgress> habitProgresses = new ArrayList<>();


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = FragmentTodayDueProgressBinding.inflate(LayoutInflater.from(requireContext()));

        Fragment progressCalendar = new ProgressCalendarFragment();
        FragmentManager fragmentManager = getParentFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.weekly_calendar, progressCalendar).commit();

        progressButtonsRecycleView = binding.progressButtons;

        habitProgresses.clear();

        habitViewModel = new ViewModelProvider(requireActivity(), new HabitViewModelFactory(requireActivity().getApplication())).get(HabitViewModel.class);
        ProgressButtonAdapter progressButtonAdapter = new ProgressButtonAdapter(habitProgresses, new ProgressButtonAdapter.OnHabitOperationCallback() {

            @Override
            public void onIncreaseClick(View view, int position) {
                AtomicInteger counter = new AtomicInteger();

                HabitProgress habitProgress = habitProgresses.get(position);
                long habitId = habitProgress.getHabit().getId();


                Progress progress = new Progress();
                progress.setHabitId(habitId);
                counter.addAndGet(1);
                progress.setCounter(counter.get());
                progress.setUpdatedDate(new Date().getTime());
                habitViewModel.increase(progress);
            }

            @Override
            public void onDecreaseClick(View view, int position) {
                HabitProgress habitProgress = habitProgresses.get(position);
                List<Progress> progressList = habitProgress.getProgressList();

                long progressId = progressList.stream().map(Progress::getProgressId).reduce((first, second) -> second).get();
                habitViewModel.decrease(progressId);
            }
        });

        habitViewModel.getAllProgress().observe(requireActivity(), habitProgresses1 -> {
            System.out.println("My Progress: " + habitProgresses1.size());
            habitProgresses.clear();
            habitProgresses.addAll(habitProgresses1);

            progressButtonAdapter.notifyItemRangeChanged(0, habitProgresses.size());
        });

        habitViewModel.fetchAllMyHabits(0).observe(requireActivity(), myHabits -> {
            System.out.println("My Habits: " + myHabits.size());
        });

        progressButtonsRecycleView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        progressButtonsRecycleView.setAdapter(progressButtonAdapter);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        return binding.getRoot();
    }
}
