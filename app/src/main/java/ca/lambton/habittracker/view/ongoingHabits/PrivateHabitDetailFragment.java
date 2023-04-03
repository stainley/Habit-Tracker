package ca.lambton.habittracker.view.ongoingHabits;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import ca.lambton.habittracker.R;
import ca.lambton.habittracker.databinding.FragmentPrivateHabitDetailBinding;
import ca.lambton.habittracker.habit.model.Habit;
import ca.lambton.habittracker.habit.model.HabitProgress;
import ca.lambton.habittracker.habit.model.Progress;
import ca.lambton.habittracker.habit.viewmodel.HabitViewModel;
import ca.lambton.habittracker.habit.viewmodel.HabitViewModelFactory;
import ca.lambton.habittracker.util.Frequency;
import ca.lambton.habittracker.util.Utils;

public class PrivateHabitDetailFragment extends Fragment {

    FragmentPrivateHabitDetailBinding binding;
    private GridView ongoingHabitDetailGridInfo;

    private GridView achievementGridInfo;

    HabitViewModel habitViewModel;

    private FirebaseUser mUser;

    ArrayList<OngoingHabitDetailGridInfo> ongoingHabitDetailGridInfoModelArrayList;

    public PrivateHabitDetailFragment() {
    }

    public static PrivateHabitDetailFragment newInstance(String param1, String param2) {
        PrivateHabitDetailFragment fragment = new PrivateHabitDetailFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        habitViewModel = new ViewModelProvider(this, new HabitViewModelFactory(getActivity().getApplication())).get(HabitViewModel.class);
        mUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        int totalTimesToComplete;
        int frequencyValue = 0;
        long startDateMillis = 0;
        long endDateMillis = 0;
        long daysBetween = 0;

        binding = FragmentPrivateHabitDetailBinding.inflate(inflater, container, false);

        assert getArguments() != null;
        Habit habit = (Habit) getArguments().getSerializable("habit");

        ongoingHabitDetailGridInfo = (GridView) binding.ongoingHabitDetailGridView;
        ongoingHabitDetailGridInfoModelArrayList = new ArrayList<OngoingHabitDetailGridInfo>();
        ongoingHabitDetailGridInfoModelArrayList.add(new OngoingHabitDetailGridInfo("Your current streak", "0"));
        ongoingHabitDetailGridInfoModelArrayList.add(new OngoingHabitDetailGridInfo("Your highest streak", "0"));

        if (habit != null) {
            binding.habitNameLabel.setText(habit.getName());
            frequencyValue = habit.getFrequency();
            startDateMillis = habit.getStartDate();
            endDateMillis = habit.getEndDate();

            LocalDate startDate = Instant.ofEpochMilli(startDateMillis).atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate endDate = Instant.ofEpochMilli(endDateMillis).atZone(ZoneId.systemDefault()).toLocalDate();
            daysBetween = ChronoUnit.DAYS.between(startDate, endDate);

            if (habit.getFrequencyUnit().equals("DAILY")) {
                totalTimesToComplete = frequencyValue * (int) daysBetween;
                getProgressList(totalTimesToComplete, habit, (int) daysBetween);
                ongoingHabitDetailGridInfoModelArrayList.add(new OngoingHabitDetailGridInfo("This day’s target", "0/" + habit.getFrequency()));
                ongoingHabitDetailGridInfoModelArrayList.add(new OngoingHabitDetailGridInfo("Days completed", "0/" + daysBetween));

            } else if (habit.getFrequencyUnit().equals("WEEKLY")) {
                double totalDays = (daysBetween / 7) * frequencyValue;
                totalTimesToComplete = frequencyValue * ((int) daysBetween / 7);
                getProgressList(totalTimesToComplete, habit, (int) daysBetween);
                ongoingHabitDetailGridInfoModelArrayList.add(new OngoingHabitDetailGridInfo("This week’s target", "0/" + habit.getFrequency()));
                ongoingHabitDetailGridInfoModelArrayList.add(new OngoingHabitDetailGridInfo("Days completed", "0/" + totalDays));
            }
            else {
                double totalDays = (daysBetween / 30) * frequencyValue;
                if (daysBetween == 30) {
                    totalTimesToComplete = frequencyValue * 30;
                }
                else {
                    totalTimesToComplete = frequencyValue * ((int) daysBetween / 30);
                }
                getProgressList(totalTimesToComplete, habit, (int) daysBetween);
                ongoingHabitDetailGridInfoModelArrayList.add(new OngoingHabitDetailGridInfo("This month’s target", "0/" + habit.getFrequency()));
                ongoingHabitDetailGridInfoModelArrayList.add(new OngoingHabitDetailGridInfo("Days completed", "0/" + totalDays));
            }
        }

        OngoingHabitDetailGridInfoAdapter adapter = new OngoingHabitDetailGridInfoAdapter(getContext(), ongoingHabitDetailGridInfoModelArrayList);
        ongoingHabitDetailGridInfo.setAdapter(adapter);

        achievementGridInfo = (GridView) binding.achievementsGridView;
        ArrayList<AchievementInfo> achievementModelArrayList = new ArrayList<AchievementInfo>();
        achievementModelArrayList.add(new AchievementInfo("Complete the First\n" +
                "Day of Your Habit", R.drawable.ic_achievement_score, R.drawable.ic_achievement_star_disable));
        achievementModelArrayList.add(new AchievementInfo("Complete 15% of \n" +
                "your Habit Duration", R.drawable.ic_achievement_score, R.drawable.ic_achievement_star_disable));
        achievementModelArrayList.add(new AchievementInfo("Complete 25% of\n" +
                " your Habit Duration", R.drawable.ic_achievement_score, R.drawable.ic_achievement_star_disable));
        achievementModelArrayList.add(new AchievementInfo("Complete 50% of\n" +
                " your Habit Duration", R.drawable.ic_achievement_score, R.drawable.ic_achievement_star_disable));
        achievementModelArrayList.add(new AchievementInfo("Complete 75% of\n" +
                " your Habit Duration\n" +
                "+\n" +
                "Coupon", R.drawable.ic_achievement_score, R.drawable.ic_achievement_star_disable));
        achievementModelArrayList.add(new AchievementInfo("Complete 100% of\n" +
                " your Habit Duration\n" +
                "+\n" +
                "Coupon", R.drawable.ic_achievement_score, R.drawable.ic_achievement_star_disable));
        AchievementGridAdapter achievementAdapter = new AchievementGridAdapter(getContext(), achievementModelArrayList);
        achievementGridInfo.setAdapter(achievementAdapter);

        HashSet<Date> events = new HashSet<>();
        events.add(new Date(2023, 3, 13));
        events.add(new Date(2023, 3, 15));
        events.add(new Date(2023, 3, 21));
        ArrayList<String> habitProgress = new ArrayList<String>(Arrays.asList("0", "50", "100"));

        CustomCalendarView cv = (CustomCalendarView) binding.calendarView;
        cv.updateCalendar(events, habitProgress);

        return binding.getRoot();
    }

    private void getProgressList(int count, Habit habit, int daysBetween) {
        AtomicInteger todayProgress = new AtomicInteger();
        AtomicInteger totalProgress = new AtomicInteger();
        AtomicInteger index = new AtomicInteger();
        LocalDate todayDate = LocalDate.now();

        habitViewModel.getAllProgress().observe(requireActivity(), habitProgresses1 -> {
            List<HabitProgress> myHabitProgressFiltered = habitProgresses1.stream().filter(dbUser -> dbUser.getHabit().getUserId().equals(mUser.getUid())).collect(Collectors.toList());

            for (HabitProgress hp : myHabitProgressFiltered) {
                totalProgress.addAndGet(hp.getProgressList().stream().filter(progress -> progress.getHabitId() == habit.getId()).map(Progress::getCounter).mapToInt(Integer::intValue).sum());

                if (totalProgress.get() == 0) {
                    binding.habitPercentageNumText.setText("0%");
                } else {
                    binding.habitPercentageNumText.setText((totalProgress.get() * 100 / count) + "%");
                    binding.habitProgressbar.setProgress(totalProgress.get() * 100 / count);
                    binding.congratulationText.setVisibility(View.VISIBLE);
                    binding.percentTextGoalAchieved.setVisibility(View.VISIBLE);
                    binding.percentTextGoalAchieved.setText(totalProgress.get() * 100 / count + "% of your goal is achieved");
                }

                String startDateString = Utils.parseDate(hp.getHabit().getStartDate());
                String endDateString = Utils.parseDate(hp.getHabit().getEndDate());

                LocalDate startDate = LocalDate.parse(startDateString);
                LocalDate endDate = LocalDate.parse(endDateString);

                if (todayDate.isEqual(startDate) || todayDate.isAfter(startDate) && (todayDate.isEqual(endDate) || (todayDate.isBefore(endDate)))) {
                    //FIXME:  java.lang.IndexOutOfBoundsException: Index: 8, Size: 8
                    if (myHabitProgressFiltered.get(index.get()).getHabit().getFrequencyUnit().equalsIgnoreCase(Frequency.DAILY.name())) {
                        todayProgress.addAndGet(hp.getProgressList().stream().filter(progress -> progress.getHabitId() == habit.getId() && progress.getDate().equals(todayDate.toString())).map(Progress::getCounter).mapToInt(Integer::intValue).sum());
                    } else if (myHabitProgressFiltered.get(index.get()).getHabit().getFrequencyUnit().equalsIgnoreCase(Frequency.WEEKLY.name())) {
                        todayProgress.addAndGet(hp.getProgressList().stream().filter(progress -> progress.getHabitId() == habit.getId() && progress.getDate().equals(todayDate.toString())).map(Progress::getCounter).mapToInt(Integer::intValue).sum());
                    } else if (myHabitProgressFiltered.get(index.get()).getHabit().getFrequencyUnit().equalsIgnoreCase(Frequency.MONTHLY.name())) {
                        todayProgress.addAndGet(hp.getProgressList().stream().filter(progress -> progress.getHabitId() == habit.getId() && progress.getDate().equals(todayDate.toString())).map(Progress::getCounter).mapToInt(Integer::intValue).sum());
                    }
                }

                index.set(index.get() + 1);
            }

            ongoingHabitDetailGridInfoModelArrayList.set(2, new OngoingHabitDetailGridInfo("This day’s target", todayProgress.get() + "/" + habit.getFrequency()));
            ongoingHabitDetailGridInfoModelArrayList.set(3, new OngoingHabitDetailGridInfo("Days completed", (totalProgress.get() / habit.getFrequency()) + "/" + daysBetween));
            ongoingHabitDetailGridInfo.setAdapter(new OngoingHabitDetailGridInfoAdapter(getContext(), ongoingHabitDetailGridInfoModelArrayList));
        });
    }
}