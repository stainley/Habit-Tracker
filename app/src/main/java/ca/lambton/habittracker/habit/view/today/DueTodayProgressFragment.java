package ca.lambton.habittracker.habit.view.today;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import ca.lambton.habittracker.R;
import ca.lambton.habittracker.common.fragment.calendar.ProgressCalendarFragment;
import ca.lambton.habittracker.databinding.FragmentTodayDueProgressBinding;
import ca.lambton.habittracker.habit.model.Habit;
import ca.lambton.habittracker.habit.model.HabitProgress;
import ca.lambton.habittracker.habit.model.Progress;
import ca.lambton.habittracker.habit.viewmodel.HabitViewModel;
import ca.lambton.habittracker.leaderboard.model.Leaderboard;
import ca.lambton.habittracker.util.Frequency;
import ca.lambton.habittracker.util.Utils;

public class DueTodayProgressFragment extends Fragment implements OnProgressCallback {

    private HabitViewModel habitViewModel;
    FragmentTodayDueProgressBinding binding;
    RecyclerView progressButtonsRecycleView;
    private ProgressButtonAdapter progressButtonAdapter;
    private FragmentManager fragmentManager;
    private float todayProgress = 0;
    private float totalFrequencies;
    private final List<HabitProgress> habitProgresses = new ArrayList<>();
    private FirebaseUser mUser;
    int habitPosition = 0;
    private OnProgressCallback onProgressCallback;
    private static final String KEY_HABIT_ID = "habit_id";
    private static final String KEY_COLLECTED = "collected";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_UPDATED = "updated";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = FragmentTodayDueProgressBinding.inflate(LayoutInflater.from(requireContext()));

        fragmentManager = getParentFragmentManager();
        progressButtonsRecycleView = binding.progressButtons;

        mUser = FirebaseAuth.getInstance().getCurrentUser();

        habitProgresses.clear();
        habitViewModel = new ViewModelProvider(requireActivity()).get(HabitViewModel.class);

        progressButtonsRecycleView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        progressButtonsRecycleView.setAdapter(progressButtonAdapter);
        onProgressCallback = this;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Fragment progressCalendar = ProgressCalendarFragment.newInstance(0);
        fragmentManager.beginTransaction().replace(R.id.due_today_calendar, progressCalendar).commit();

        progressButtonAdapter = new ProgressButtonAdapter(habitProgresses, new ProgressButtonAdapter.OnHabitOperationCallback() {

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
                progress.setDate(LocalDate.now().toString());
                progress.setTime(LocalTime.now().truncatedTo(ChronoUnit.SECONDS).toString());
                habitPosition = position;

                LocalDate today = LocalDate.now();

                for (Progress progress1 : habitProgress.getProgressList()) {

                    if (today.isEqual(LocalDate.parse(progress1.getDate())) || today.isAfter(LocalDate.parse(progress1.getDate()))) {

                        int totalCompletedToday = habitProgress.getProgressList().stream().filter(pro -> pro.getDate().equalsIgnoreCase(today.toString())).mapToInt(Progress::getCounter).sum();

                        if (totalCompletedToday < habitProgress.getHabit().getFrequency()) {
                            habitViewModel.increase(progress);

                            if (habitProgress.getHabit().getFrequency() == (totalCompletedToday + 1)) {

                                SharedPreferences spCollectedPoints = requireActivity().getSharedPreferences("achievements_" + habitId, Context.MODE_PRIVATE);
                                long habitIdCollected = spCollectedPoints.getLong(KEY_HABIT_ID, 0);
                                String dateCollected = spCollectedPoints.getString(KEY_UPDATED, LocalDate.now().toString());
                                boolean pointsCollected = spCollectedPoints.getBoolean(KEY_COLLECTED, false);

                                if (pointsCollected && habitIdCollected == habitId && LocalDate.now().toString().equalsIgnoreCase(dateCollected)) {
                                    // do nothing
                                    Toast.makeText(requireContext(), "Points already collected.", Toast.LENGTH_SHORT).show();
                                } else {
                                    SharedPreferences.Editor editor = spCollectedPoints.edit();
                                    editor.putLong(KEY_HABIT_ID, habitId);
                                    editor.putBoolean(KEY_COLLECTED, true);
                                    editor.putString(KEY_USER_ID, habitProgress.getHabit().getUserId());
                                    editor.putString(KEY_UPDATED, dateCollected);
                                    editor.apply();
                                    onProgressCallback.onProgressCompleted(view);
                                }

                            }
                            break;
                        }
                    }
                }

                if (habitProgress.getProgressList().size() == 0) {
                    habitViewModel.increase(progress);

                    if (habitProgress.getHabit().getFrequency() == 1) {
                        onProgressCallback.onProgressCompleted(view);
                    }
                }

            }

            // Decrease only from today, don't delete pass record
            @Override
            public void onDecreaseClick(View view, int position) {
                HabitProgress habitProgress = habitProgresses.get(position);
                List<Progress> progressList = habitProgress.getProgressList();

                if (progressList.size() > 0) {
                    LocalDate today = LocalDate.now();

                    List<Long> idToRemove = progressList.stream().filter(old -> LocalDate.parse(old.getDate()).isEqual(today) || LocalDate.parse(old.getDate()).isAfter(today)).map(Progress::getProgressId).collect(Collectors.toList());

                    if (idToRemove.size() > 0) {
                        long progressId = idToRemove.stream().reduce((first, second) -> second).get();
                        habitViewModel.decrease(progressId);
                    }
                }
            }
        });

        progressButtonsRecycleView.setAdapter(progressButtonAdapter);

        habitViewModel.getAllHabit().observe(getViewLifecycleOwner(), habit -> {
            // Summarize habit
            Map<String, Integer> habitLeaderBoard = habit.stream().filter(user -> user.getUserId().equals(mUser.getUid())).collect(Collectors.groupingBy(Habit::getUserId, Collectors.summingInt(Habit::getScore)));


            if (!mUser.getDisplayName().equalsIgnoreCase("") && mUser.getPhotoUrl() != null) {
                if (habitLeaderBoard.containsKey(mUser.getUid())) {
                    habitLeaderBoard.forEach((userId, total) -> {
                        Leaderboard leaderboard = new Leaderboard();
                        leaderboard.setScore(total);
                        leaderboard.setName(mUser.getDisplayName());
                        leaderboard.setImageUrl(mUser.getPhotoUrl() != null && !mUser.getPhotoUrl().toString().equals("") ? mUser.getPhotoUrl().toString() : "");
                        leaderboard.setAccountId(mUser.getUid());

                        habitViewModel.updateLeaderBoard(leaderboard);
                    });
                }
            } else {
                Toast.makeText(requireContext(), "Please update you profile name and photo", Toast.LENGTH_LONG).show();
            }

        });

        return binding.getRoot();
    }

    public void updateCalendar() {
        LocalDate todayDate = LocalDate.now();

        habitViewModel.getAllProgress().observe(requireActivity(), habitProgresses1 -> {
            AtomicInteger index = new AtomicInteger();
            habitProgresses.clear();


            List<HabitProgress> myHabitProgressFiltered = habitProgresses1.stream().filter(dbUser -> dbUser.getHabit().getUserId().equals(mUser.getUid())).collect(Collectors.toList());

            for (HabitProgress hp : myHabitProgressFiltered) {

                String startDateString = Utils.parseDate(hp.getHabit().getStartDate());
                String endDateString = Utils.parseDate(hp.getHabit().getEndDate());

                LocalDate startDate = LocalDate.parse(startDateString);
                LocalDate endDate = LocalDate.parse(endDateString);

                if (todayDate.isEqual(startDate) || todayDate.isAfter(startDate) && (todayDate.isEqual(endDate) || (todayDate.isBefore(endDate)))) {
                    habitProgresses.add(hp);
                }
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
                    if (myHabitProgressFiltered.get(index.get()).getHabit().getFrequencyUnit().equalsIgnoreCase(Frequency.DAILY.name())) {
                        todayProgress += habitProgress.getProgressList().stream().filter(progress -> progress.getDate().equals(todayDate.toString())).map(Progress::getCounter).mapToInt(Integer::intValue).sum();
                    } else if (myHabitProgressFiltered.get(index.get()).getHabit().getFrequencyUnit().equalsIgnoreCase(Frequency.WEEKLY.name())) {
                        todayProgress += habitProgress.getProgressList().stream().filter(progress -> progress.getDate().equals(todayDate.toString())).map(Progress::getCounter).mapToInt(Integer::intValue).sum();
                    } else if (myHabitProgressFiltered.get(index.get()).getHabit().getFrequencyUnit().equalsIgnoreCase(Frequency.MONTHLY.name())) {
                        todayProgress += habitProgress.getProgressList().stream().filter(progress -> progress.getDate().equals(todayDate.toString())).map(Progress::getCounter).mapToInt(Integer::intValue).sum();
                    }
                }
                index.set(index.get() + 1);

            });

            float result = (todayProgress / totalFrequencies) * 100;
            Fragment fragmentProgressCalendar = ProgressCalendarFragment.newInstance((int) result);

            if (fragmentManager != null) {
                fragmentManager.beginTransaction().replace(R.id.due_today_calendar, fragmentProgressCalendar).commitAllowingStateLoss();
            }

            progressButtonAdapter.notifyItemRangeChanged(0, myHabitProgressFiltered.size());
        });
    }

    @Override
    public void onStart() {
        super.onStart();

        updateCalendar();
    }

    @Override
    public void onProgressCompleted(View view) {
        NavDirections navDirections = ca.lambton.habittracker.habit.view.today.DueTodayFragmentDirections.actionCompleteHabitFragmentToCollectScoreFragment().setHabitProgress(habitProgresses.get(habitPosition));
        Navigation.findNavController(view).navigate(navDirections);
    }
}

interface OnProgressCallback {
    void onProgressCompleted(View view);
}
