package ca.lambton.habittracker.view.ongoingHabits;

import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import ca.lambton.habittracker.R;
import ca.lambton.habittracker.databinding.FragmentPrivateHabitDetailBinding;
import ca.lambton.habittracker.habit.model.Habit;
import ca.lambton.habittracker.habit.model.HabitProgress;
import ca.lambton.habittracker.habit.model.Progress;
import ca.lambton.habittracker.habit.viewmodel.HabitViewModel;
import ca.lambton.habittracker.habit.viewmodel.HabitViewModelFactory;
import ca.lambton.habittracker.util.Utils;
import ca.lambton.habittracker.util.calendar.monthly.CustomCalendarView;

public class PrivateHabitDetailFragment extends Fragment {

    FragmentPrivateHabitDetailBinding binding;
    private GridView ongoingHabitDetailGridInfo;

    private GridView achievementGridInfo;

    HabitViewModel habitViewModel;

    ArrayList<OngoingHabitDetailGridInfo> ongoingHabitDetailGridInfoModelArrayList;

    private HabitProgress habitProgress;
    int daysTargetCounter = 0;
    int daysTargetTotal = 0;

    int daysCompletedCounter = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        habitViewModel = new ViewModelProvider(this, new HabitViewModelFactory(getActivity().getApplication())).get(HabitViewModel.class);
        habitProgress = PrivateHabitDetailFragmentArgs.fromBundle(requireArguments()).getHabitProgress();

        if (habitProgress != null) {
            daysTargetTotal = habitProgress.getHabit().getFrequency();
            daysTargetCounter = getDaysTargetCounter(habitProgress.getHabit());
            daysCompletedCounter = getDaysCompletedCounter(habitProgress.getHabit());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        int totalTimesToComplete;
        int frequencyValue = 0;

        binding = FragmentPrivateHabitDetailBinding.inflate(inflater, container, false);

        binding.imageView.setOnClickListener(this::editHabitName);

        ongoingHabitDetailGridInfo = binding.ongoingHabitDetailGridView;
        ongoingHabitDetailGridInfoModelArrayList = new ArrayList<>();
        ongoingHabitDetailGridInfoModelArrayList.add(new OngoingHabitDetailGridInfo("Your current streak", String.valueOf(Utils.currentNumberOfStreak(habitProgress))));
        ongoingHabitDetailGridInfoModelArrayList.add(new OngoingHabitDetailGridInfo("Your highest streak", String.valueOf(Utils.maxNumbersOfStreak(habitProgress))));

        if (habitProgress.getHabit() != null) {
            binding.habitNameLabel.setText(habitProgress.getHabit().getName());
            frequencyValue = habitProgress.getHabit().getFrequency();

            if (habitProgress.getHabit().getFrequencyUnit().equals("DAILY")) {
                totalTimesToComplete = frequencyValue * (int) Utils.getTotalDays(habitProgress.getHabit());
                displayPercentageTotal(totalTimesToComplete, habitProgress.getHabit());
                ongoingHabitDetailGridInfoModelArrayList.add(new OngoingHabitDetailGridInfo("This day’s target", daysTargetCounter + "/" + daysTargetTotal));
                ongoingHabitDetailGridInfoModelArrayList.add(new OngoingHabitDetailGridInfo("Days completed", daysCompletedCounter + "/" + Utils.getTotalDays(habitProgress.getHabit())));

            } else if (habitProgress.getHabit().getFrequencyUnit().equals("WEEKLY")) {
                totalTimesToComplete = frequencyValue * (int) Utils.getTotalOfWeeks(habitProgress.getHabit());
                displayPercentageTotal(totalTimesToComplete, habitProgress.getHabit());
                ongoingHabitDetailGridInfoModelArrayList.add(new OngoingHabitDetailGridInfo("This week’s target", daysTargetCounter + "/" + daysTargetTotal));
                ongoingHabitDetailGridInfoModelArrayList.add(new OngoingHabitDetailGridInfo("Days completed", daysCompletedCounter + "/" + Utils.getTotalOfWeeks(habitProgress.getHabit())));
            } else {
                if (Utils.getTotalDays(habitProgress.getHabit()) < 30 || Utils.getTotalDays(habitProgress.getHabit()) == 30) {
                    totalTimesToComplete = frequencyValue * 1;
                } else {
                    totalTimesToComplete = frequencyValue * (int) Utils.getTotalOfMonths(habitProgress.getHabit());
                }

                displayPercentageTotal(totalTimesToComplete, habitProgress.getHabit());
                ongoingHabitDetailGridInfoModelArrayList.add(new OngoingHabitDetailGridInfo("This month’s target", daysTargetCounter + "/" + daysTargetTotal));
                ongoingHabitDetailGridInfoModelArrayList.add(new OngoingHabitDetailGridInfo("Days completed", daysCompletedCounter + "/" + Utils.getTotalOfMonths(habitProgress.getHabit())));
            }
        }

        OngoingHabitDetailGridInfoAdapter adapter = new OngoingHabitDetailGridInfoAdapter(getContext(), ongoingHabitDetailGridInfoModelArrayList);
        ongoingHabitDetailGridInfo.setAdapter(adapter);

        achievementGridInfo = (GridView) binding.achievementsGridView;
        ArrayList<AchievementInfo> achievementModelArrayList = new ArrayList<AchievementInfo>();
        achievementModelArrayList.add(new AchievementInfo("Complete the First\n" + "Day of Your Habit", R.drawable.ic_achievement_score, R.drawable.ic_achievement_star_disable));
        achievementModelArrayList.add(new AchievementInfo("Complete 15% of \n" + "your Habit Duration", R.drawable.ic_achievement_score, R.drawable.ic_achievement_star_disable));
        achievementModelArrayList.add(new AchievementInfo("Complete 25% of\n" + " your Habit Duration", R.drawable.ic_achievement_score, R.drawable.ic_achievement_star_disable));
        achievementModelArrayList.add(new AchievementInfo("Complete 50% of\n" + " your Habit Duration", R.drawable.ic_achievement_score, R.drawable.ic_achievement_star_disable));
        achievementModelArrayList.add(new AchievementInfo("Complete 75% of\n" + " your Habit Duration\n" + "+\n" + "Coupon", R.drawable.ic_achievement_score, R.drawable.ic_achievement_star_disable));
        achievementModelArrayList.add(new AchievementInfo("Complete 100% of\n" + " your Habit Duration\n" + "+\n" + "Coupon", R.drawable.ic_achievement_score, R.drawable.ic_achievement_star_disable));
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

    private void editHabitName(View view) {
        TextInputEditText newHabit = new TextInputEditText(requireContext());
        newHabit.setInputType(InputType.TYPE_CLASS_TEXT);
        newHabit.setSingleLine();
        newHabit.setPadding(50, 0, 50, 32);
        String habitName = habitProgress.getHabit().getName();
        newHabit.setText(habitName != null ? habitName : "");
        newHabit.setHint("New Habit");
        newHabit.setPadding(75, newHabit.getPaddingTop(), newHabit.getPaddingRight(), newHabit.getPaddingBottom());
        new MaterialAlertDialogBuilder(requireContext()).setView(newHabit).setMessage("Enter your new habit").setNeutralButton("Cancel", (dialog, which) -> {
        }).setNegativeButton("Save", (dialog, which) -> {
            String inputText = Objects.requireNonNull(newHabit.getText()).toString();
            if (inputText.equals("")) {
                Toast.makeText(requireContext(), "Couldn't be empty", Toast.LENGTH_SHORT).show();
            } else {
                habitProgress.getHabit().setName(newHabit.getText().toString());
            }
            binding.habitNameLabel.setText(newHabit.getText().toString());
            habitViewModel.updateHabit(habitProgress.getHabit());
            Toast.makeText(requireContext(), "Habit Name updated.", Toast.LENGTH_SHORT).show();

        }).setCancelable(false).show();
    }

    private void displayPercentageTotal(int count, Habit habit) {

        AtomicInteger totalProgress = new AtomicInteger();

        totalProgress.addAndGet(habitProgress.getProgressList().stream().filter(progress -> progress.getHabitId() == habit.getId()).mapToInt(Progress::getCounter).sum());

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
    }

    public int getDaysTargetCounter(Habit habit) {
        AtomicInteger todayProgress = new AtomicInteger();
        LocalDate todayDate = LocalDate.now();

        LocalDate startDate = convertToLocalDateViaInstant(habitProgress.getHabit().getStartDate());
        LocalDate endDate = convertToLocalDateViaInstant(habitProgress.getHabit().getEndDate());

        if (isDateInRange(todayDate, startDate, endDate)) {
            todayProgress.addAndGet(habitProgress.getProgressList().stream().filter(progress -> isProgressForToday(habit, todayDate, progress)).mapToInt(Progress::getCounter).sum());
        }

        return todayProgress.get();
    }

    public int getDaysCompletedCounter(Habit habit) {
        AtomicInteger totalProgress = new AtomicInteger();

        totalProgress.addAndGet(habitProgress.getProgressList().stream().filter(progress -> progress.getHabitId() == habit.getId()).mapToInt(Progress::getCounter).sum());

        return totalProgress.get() / habit.getFrequency();
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

            Snackbar.make(view, "Would you like to delete this habit?", Toast.LENGTH_SHORT).setAnchorView(view).setAction("Yes", v -> {
                habitViewModel.deleteHabit(habitProgress.getHabit());
                Navigation.findNavController(view).popBackStack();
            }).show();
        }
    }
}