package ca.lambton.habittracker.view.ongoingHabits;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
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

public class PrivateHabitDetailFragment extends Fragment {

    FragmentPrivateHabitDetailBinding binding;
    private GridView ongoingHabitDetailGridInfo;

    private GridView achievementGridInfo;

    HabitViewModel habitViewModel;

    private FirebaseUser mUser;

    ArrayList<OngoingHabitDetailGridInfo> ongoingHabitDetailGridInfoModelArrayList;

    private HabitProgress habitProgress;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        habitViewModel = new ViewModelProvider(this, new HabitViewModelFactory(getActivity().getApplication())).get(HabitViewModel.class);
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        habitProgress = PrivateHabitDetailFragmentArgs.fromBundle(requireArguments()).getHabitProgress();
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

        ongoingHabitDetailGridInfo = binding.ongoingHabitDetailGridView;
        ongoingHabitDetailGridInfoModelArrayList = new ArrayList<>();
        ongoingHabitDetailGridInfoModelArrayList.add(new OngoingHabitDetailGridInfo("Your current streak", "0"));
        ongoingHabitDetailGridInfoModelArrayList.add(new OngoingHabitDetailGridInfo("Your highest streak", "0"));

        if (habitProgress.getHabit() != null) {
            binding.habitNameLabel.setText(habitProgress.getHabit().getName());
            frequencyValue = habitProgress.getHabit().getFrequency();
            startDateMillis = habitProgress.getHabit().getStartDate();
            endDateMillis = habitProgress.getHabit().getEndDate();

            LocalDate startDate = Instant.ofEpochMilli(startDateMillis).atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate endDate = Instant.ofEpochMilli(endDateMillis).atZone(ZoneId.systemDefault()).toLocalDate();
            daysBetween = ChronoUnit.DAYS.between(startDate, endDate);

            if (habitProgress.getHabit().getFrequencyUnit().equals("DAILY")) {
                totalTimesToComplete = frequencyValue * (int) daysBetween;
                getProgressList(totalTimesToComplete, habitProgress.getHabit(), (int) daysBetween);
                ongoingHabitDetailGridInfoModelArrayList.add(new OngoingHabitDetailGridInfo("This day’s target", "0/" + habitProgress.getHabit().getFrequency()));
                ongoingHabitDetailGridInfoModelArrayList.add(new OngoingHabitDetailGridInfo("Days completed", "0/" + daysBetween));

            } else if (habitProgress.getHabit().getFrequencyUnit().equals("WEEKLY")) {
                double totalDays = (daysBetween / 7) * frequencyValue;
                totalTimesToComplete = frequencyValue * ((int) daysBetween / 7);
                getProgressList(totalTimesToComplete, habitProgress.getHabit(), (int) daysBetween);
                ongoingHabitDetailGridInfoModelArrayList.add(new OngoingHabitDetailGridInfo("This week’s target", "0/" + habitProgress.getHabit().getFrequency()));
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
                getProgressList(totalTimesToComplete, habitProgress.getHabit(), (int) daysBetween);
                ongoingHabitDetailGridInfoModelArrayList.add(new OngoingHabitDetailGridInfo("This month’s target", "0/" + habitProgress.getHabit().getFrequency()));
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

        binding.deleteButtonCard.setOnClickListener(this::deleteHabit);
        return binding.getRoot();
    }

    private void getProgressList(int count, Habit habit, int daysBetween) {
        AtomicInteger todayProgress = new AtomicInteger();
        AtomicInteger totalProgress = new AtomicInteger();
        LocalDate todayDate = LocalDate.now();

        habitViewModel.getAllProgress()
                .observe(requireActivity(), habitProgresses -> {
                    List<HabitProgress> userHabitProgresses = habitProgresses.stream()
                            .filter(progress -> progress.getHabit().getUserId().equals(mUser.getUid()))
                            .collect(Collectors.toList());

                    for (HabitProgress habitProgress : userHabitProgresses) {
                        totalProgress.addAndGet(habitProgress.getProgressList().stream()
                                .filter(progress -> progress.getHabitId() == habit.getId())
                                .mapToInt(Progress::getCounter)
                                .sum());

                        if (totalProgress.get() == 0) {
                            binding.habitPercentageNumText.setText("0%");
                        } else {
                            int percentage = totalProgress.get() * 100 / count;
                            binding.habitPercentageNumText.setText(percentage + "%");
                            binding.habitProgressbar.setProgress(percentage);
                            binding.congratulationText.setVisibility(View.VISIBLE);
                            binding.percentTextGoalAchieved.setVisibility(View.VISIBLE);
                            binding.percentTextGoalAchieved.setText(percentage + "% of your goal is achieved");
                        }

//                        LocalDate startDate = convertToLocalDateViaInstant(habitProgress.getHabit().getStartDate());
//                        LocalDate endDate = convertToLocalDateViaInstant(habitProgress.getHabit().getEndDate());
//
//                        if (isDateInRange(todayDate, startDate, endDate)) {
//                            todayProgress.addAndGet(habitProgress.getProgressList().stream()
//                                    .filter(progress -> isProgressForToday(habit, todayDate, progress))
//                                    .mapToInt(Progress::getCounter)
//                                    .sum());
//                        }
                    }

//                    int completedDays = totalProgress.get() / habit.getFrequency();
//                    ongoingHabitDetailGridInfoModelArrayList.set(2, new OngoingHabitDetailGridInfo("This day’s target", todayProgress + "/" + habit.getFrequency()));
//                    ongoingHabitDetailGridInfoModelArrayList.set(3, new OngoingHabitDetailGridInfo("Days completed", completedDays + "/" + daysBetween));
//                    ongoingHabitDetailGridInfo.setAdapter(new OngoingHabitDetailGridInfoAdapter(getContext(), ongoingHabitDetailGridInfoModelArrayList));
                });
    }

    private LocalDate convertToLocalDateViaInstant(Long dateToConvert) {
        long millisecondsSinceEpoch = dateToConvert;
        Instant instant = Instant.ofEpochMilli(millisecondsSinceEpoch);
        ZonedDateTime zonedDateTime = instant.atZone(ZoneOffset.UTC);
        LocalDate localDate = zonedDateTime.toLocalDate();
        return localDate;
    }

    private boolean isDateInRange(LocalDate date, LocalDate startDate, LocalDate endDate) {
        return date.isEqual(startDate) || (date.isAfter(startDate) && date.isBefore(endDate.plusDays(1)));
    }

    private boolean isProgressForToday(Habit habit, LocalDate todayDate, Progress progress) {
        return progress.getHabitId() == habit.getId() && progress.getDate().equals(todayDate.toString());
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

//    private void getProgressList(int count, Habit habit, int daysBetween) {
//        AtomicInteger todayProgress = new AtomicInteger();
//        AtomicInteger totalProgress = new AtomicInteger();
//        AtomicInteger index = new AtomicInteger();
//        LocalDate todayDate = LocalDate.now();
//
//        habitViewModel.getAllProgress().observe(requireActivity(), habitProgresses1 -> {
//            List<HabitProgress> myHabitProgressFiltered = habitProgresses1.stream().filter(dbUser -> dbUser.getHabit().getUserId().equals(mUser.getUid())).collect(Collectors.toList());
//
//            for (HabitProgress hp : myHabitProgressFiltered) {
//                totalProgress.addAndGet(hp.getProgressList().stream().filter(progress -> progress.getHabitId() == habit.getId()).map(Progress::getCounter).mapToInt(Integer::intValue).sum());
//
//                if (totalProgress.get() == 0) {
//                    binding.habitPercentageNumText.setText("0%");
//                } else {
//                    binding.habitPercentageNumText.setText((totalProgress.get() * 100 / count) + "%");
//                    binding.habitProgressbar.setProgress(totalProgress.get() * 100 / count);
//                    binding.congratulationText.setVisibility(View.VISIBLE);
//                    binding.percentTextGoalAchieved.setVisibility(View.VISIBLE);
//                    binding.percentTextGoalAchieved.setText(totalProgress.get() * 100 / count + "% of your goal is achieved");
//                }
//
//                String startDateString = Utils.parseDate(hp.getHabit().getStartDate());
//                String endDateString = Utils.parseDate(hp.getHabit().getEndDate());
//
//                LocalDate startDate = LocalDate.parse(startDateString);
//                LocalDate endDate = LocalDate.parse(endDateString);
//
//                if (todayDate.isEqual(startDate) || todayDate.isAfter(startDate) && (todayDate.isEqual(endDate) || (todayDate.isBefore(endDate)))) {
//
//                    if (myHabitProgressFiltered.get(index.get()).getHabit().getFrequencyUnit().equalsIgnoreCase(Frequency.DAILY.name())) {
//                        todayProgress.addAndGet(hp.getProgressList().stream().filter(progress -> progress.getHabitId() == habit.getId() && progress.getDate().equals(todayDate.toString())).map(Progress::getCounter).mapToInt(Integer::intValue).sum());
//                    } else if (myHabitProgressFiltered.get(index.get()).getHabit().getFrequencyUnit().equalsIgnoreCase(Frequency.WEEKLY.name())) {
//                        todayProgress.addAndGet(hp.getProgressList().stream().filter(progress -> progress.getHabitId() == habit.getId() && progress.getDate().equals(todayDate.toString())).map(Progress::getCounter).mapToInt(Integer::intValue).sum());
//                    } else if (myHabitProgressFiltered.get(index.get()).getHabit().getFrequencyUnit().equalsIgnoreCase(Frequency.MONTHLY.name())) {
//                        todayProgress.addAndGet(hp.getProgressList().stream().filter(progress -> progress.getHabitId() == habit.getId() && progress.getDate().equals(todayDate.toString())).map(Progress::getCounter).mapToInt(Integer::intValue).sum());
//                    }
//                }
//
//                index.set(index.get() + 1);
//            }
//
//            ongoingHabitDetailGridInfoModelArrayList.set(2, new OngoingHabitDetailGridInfo("This day’s target", todayProgress.get() + "/" + habit.getFrequency()));
//            ongoingHabitDetailGridInfoModelArrayList.set(3, new OngoingHabitDetailGridInfo("Days completed", (totalProgress.get() / habit.getFrequency()) + "/" + daysBetween));
//            ongoingHabitDetailGridInfo.setAdapter(new OngoingHabitDetailGridInfoAdapter(getContext(), ongoingHabitDetailGridInfoModelArrayList));
//        });
//    }


}