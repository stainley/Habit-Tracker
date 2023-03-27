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

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

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

    private float todayProgress = 0;
    private float totalFrequencies;
    private final List<HabitProgress> habitProgresses = new ArrayList<>();


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = FragmentTodayDueProgressBinding.inflate(LayoutInflater.from(requireContext()));

        //TODO: Add logic due for today Habits
        //Fragment progressCalendar = ProgressCalendarFragment.newInstance((int) todayProgress);
        Fragment progressCalendar = new ProgressCalendarFragment();
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

                LocalDate today = LocalDate.now();

                for (Progress progress1 : habitProgress.getProgressList()) {
                    
                    if (today.isEqual(LocalDate.parse(progress1.getDate())) || today.isAfter(LocalDate.parse(progress1.getDate()))) {

                        int totalCompletedToday = habitProgress.getProgressList().stream()
                                .filter(pro -> pro.getDate().equalsIgnoreCase(today.toString()))
                                .mapToInt(Progress::getCounter).sum();

                        if (totalCompletedToday < Integer.parseInt(habitProgress.getHabit().getFrequency())) {
                            habitViewModel.increase(progress);
                            break;
                        }
                    }
                }

                if (habitProgress.getProgressList().size() == 0) {
                    habitViewModel.increase(progress);
                }

            }

            // Decrease only from today, don't delete pass record
            @Override
            public void onDecreaseClick(View view, int position) {
                HabitProgress habitProgress = habitProgresses.get(position);
                List<Progress> progressList = habitProgress.getProgressList();

                if (progressList.size() > 0) {
                    LocalDate today = LocalDate.now();

                    List<Long> idToRemove = progressList.stream()
                            .filter(old -> LocalDate.parse(old.getDate()).isEqual(today) || LocalDate.parse(old.getDate()).isAfter(today))
                            .map(Progress::getProgressId)
                            .collect(Collectors.toList());

                    if (idToRemove.size() > 0) {
                        long progressId = idToRemove.stream()
                                .reduce((first, second) -> second)
                                .get();
                        habitViewModel.decrease(progressId);
                    }
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

            habitProgresses.forEach(habitProgress -> {
                totalFrequencies += Integer.parseInt(habitProgress.getHabit().getFrequency());

                System.out.println(habitProgresses1.get(index.get()).getHabit().getFrequencyUnit().equalsIgnoreCase(Frequency.DAILY.name()) + " RESULT");

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

            float result = (todayProgress / totalFrequencies) * 100;
            Fragment fragmentProgressCalendar = ProgressCalendarFragment.newInstance((int) result);
            fragmentManager.beginTransaction().replace(R.id.weekly_calendar, fragmentProgressCalendar).commit();

            progressButtonAdapter.notifyDataSetChanged();
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
