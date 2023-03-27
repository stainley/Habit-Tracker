package ca.lambton.habittracker.view.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import ca.lambton.habittracker.R;
import ca.lambton.habittracker.databinding.FragmentHomeBinding;
import ca.lambton.habittracker.habit.model.HabitProgress;
import ca.lambton.habittracker.habit.model.Progress;
import ca.lambton.habittracker.habit.viewmodel.HabitViewModel;
import ca.lambton.habittracker.habit.viewmodel.HabitViewModelFactory;
import ca.lambton.habittracker.util.Frequency;
import ca.lambton.habittracker.util.Utils;
import ca.lambton.habittracker.view.fragment.calendar.ProgressCalendarFragment;
import ca.lambton.habittracker.view.fragment.progress.DailyProgressFragment;
import ca.lambton.habittracker.view.fragment.progress.SummarizedProgressFragment;
import ca.lambton.habittracker.view.fragment.quote.QuoteFragment;

public class HomeFragment extends Fragment {

    private FragmentManager supportFragmentManager;
    private FragmentHomeBinding binding;
    private HabitViewModel habitViewModel;
    private float finalResult;

    private float todayProgress = 0;
    private float totalFrequencies;
    private final List<HabitProgress> habitProgresses = new ArrayList<>();


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = FragmentHomeBinding.inflate(LayoutInflater.from(requireContext()));
        supportFragmentManager = getChildFragmentManager();

        habitViewModel = new ViewModelProvider(requireActivity()).get(HabitViewModel.class);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragmentHomeBinding binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        todayCalendarProgress();

        Fragment calendarFragment = ProgressCalendarFragment.newInstance((int) finalResult);
        supportFragmentManager.beginTransaction().replace(R.id.home_calendar_view, calendarFragment).commit();

        Fragment quoteDayFragment = new QuoteFragment();
        supportFragmentManager.beginTransaction().replace(R.id.quoteDayFragmentView, quoteDayFragment).commit();

        DailyProgressFragment dailyProgressFragment = new DailyProgressFragment();
        supportFragmentManager.beginTransaction().replace(R.id.daily_habit_progress, dailyProgressFragment).commit();


        Fragment summarizedProgress = new SummarizedProgressFragment();
        getParentFragmentManager().beginTransaction().replace(R.id.summarizedProgressView, summarizedProgress).commit();

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    public void todayCalendarProgress() {

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


                System.out.println(startDate);
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

            Fragment calendarFragment = ProgressCalendarFragment.newInstance((int) finalResult);
            supportFragmentManager.beginTransaction().replace(R.id.home_calendar_view, calendarFragment).commit();
        });
    }

}
