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

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

import ca.lambton.habittracker.R;
import ca.lambton.habittracker.databinding.FragmentTodayDueProgressBinding;
import ca.lambton.habittracker.habit.model.HabitProgress;
import ca.lambton.habittracker.habit.model.Progress;
import ca.lambton.habittracker.habit.viewmodel.HabitViewModel;
import ca.lambton.habittracker.habit.viewmodel.HabitViewModelFactory;
import ca.lambton.habittracker.util.Frequency;
import ca.lambton.habittracker.util.Utils;
import ca.lambton.habittracker.view.fragment.calendar.ProgressCalendarFragment;

public class DueTodayProgressFragment extends Fragment {

    private HabitViewModel habitViewModel;
    FragmentTodayDueProgressBinding binding;
    RecyclerView progressButtonsRecycleView;
    private ProgressButtonAdapter progressButtonAdapter;

    private float todayProgress;
    private float totalFrequencies;
    private final List<HabitProgress> habitProgresses = new ArrayList<>();


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = FragmentTodayDueProgressBinding.inflate(LayoutInflater.from(requireContext()));

        //Fragment progressCalendar = new ProgressCalendarFragment();
        //TODO: Add logic due for today Habits
        Fragment progressCalendar = ProgressCalendarFragment.newInstance((int) todayProgress);
        FragmentManager fragmentManager = getParentFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.weekly_calendar, progressCalendar).commit();

        progressButtonsRecycleView = binding.progressButtons;

        habitProgresses.clear();

        habitViewModel = new ViewModelProvider(requireActivity(), new HabitViewModelFactory(requireActivity().getApplication())).get(HabitViewModel.class);

        progressButtonAdapter = new ProgressButtonAdapter(habitProgresses, new ProgressButtonAdapter.OnHabitOperationCallback() {

            @Override
            public void onIncreaseClick(View view, int position) {
                AtomicInteger counter = new AtomicInteger();

                HabitProgress habitProgress = habitProgresses.get(position);

                System.out.println("Frequency: " + habitProgress.getHabit().getFrequency());

                long habitId = habitProgress.getHabit().getId();

                Progress progress = new Progress();
                progress.setHabitId(habitId);
                counter.addAndGet(1);
                progress.setCounter(counter.get());
                progress.setUpdatedDate(new Date().getTime());
                progress.setDate(LocalDate.now().toString());
                progress.setTime(LocalTime.now().truncatedTo(ChronoUnit.SECONDS).toString());


                if (habitProgress.getProgressList().size() < Integer.parseInt(habitProgress.getHabit().getFrequency())) {
                    habitViewModel.increase(progress);
                }
            }

            @Override
            public void onDecreaseClick(View view, int position) {
                HabitProgress habitProgress = habitProgresses.get(position);
                List<Progress> progressList = habitProgress.getProgressList();

                if (progressList.size() > 0) {
                    long progressId = progressList.stream().map(Progress::getProgressId).reduce((first, second) -> second).get();
                    habitViewModel.decrease(progressId);
                }
            }
        });
        LocalDate todayDate = LocalDate.now();

        habitViewModel.getAllProgress().observe(requireActivity(), habitProgresses1 -> {
            System.out.println("My Progress: " + habitProgresses1.size());
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


                System.out.println(startDate);
            }

            totalFrequencies = 0;
            todayProgress = 0;
            habitProgresses1.forEach(habitProgress -> {
                totalFrequencies += Integer.parseInt(habitProgress.getHabit().getFrequency());

                System.out.println(habitProgresses1.get(index.get()).getHabit().getFrequencyUnit().equalsIgnoreCase(Frequency.DAILY.name()) + " RESULT");

                // Filter by daily/weekly
                if (habitProgresses1.get(index.get()).getHabit().getFrequencyUnit().equalsIgnoreCase(Frequency.DAILY.name())) {
                    todayProgress += habitProgress.getProgressList().stream().filter(progress -> progress.getDate().equals(todayDate.toString())).map(Progress::getCounter).mapToInt(Integer::intValue).sum();
                } else if (habitProgresses1.get(index.get()).getHabit().getFrequencyUnit().equalsIgnoreCase(Frequency.WEEKLY.name())) {
                    todayProgress += habitProgress.getProgressList().stream().filter(progress -> progress.getDate().equals(todayDate.toString())).map(Progress::getCounter).mapToInt(Integer::intValue).sum();
                } else if (habitProgresses1.get(index.get()).getHabit().getFrequencyUnit().equalsIgnoreCase(Frequency.MONTHLY.name())) {
                    todayProgress += habitProgress.getProgressList().stream().filter(progress -> progress.getDate().equals(todayDate.toString())).map(Progress::getCounter).mapToInt(Integer::intValue).sum();
                }

                index.set(index.get() + 1);

                float result = (todayProgress / totalFrequencies) * 100;
                System.out.println(result);
                Fragment fragmentProgressCalendar = ProgressCalendarFragment.newInstance((int) result);
                fragmentManager.beginTransaction().replace(R.id.weekly_calendar, fragmentProgressCalendar).commit();
            });


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


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    }

}
