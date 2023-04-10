package ca.lambton.habittracker.habit.view.home;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import ca.lambton.habittracker.R;
import ca.lambton.habittracker.databinding.FragmentHomeBinding;
import ca.lambton.habittracker.habit.model.Habit;
import ca.lambton.habittracker.habit.model.HabitProgress;
import ca.lambton.habittracker.habit.model.Progress;
import ca.lambton.habittracker.habit.viewmodel.HabitViewModel;
import ca.lambton.habittracker.util.Frequency;
import ca.lambton.habittracker.util.Notification;
import ca.lambton.habittracker.util.Utils;
import ca.lambton.habittracker.common.fragment.calendar.ProgressCalendarFragment;
import ca.lambton.habittracker.habit.view.progress.DailyProgressFragment;
import ca.lambton.habittracker.habit.view.progress.SummarizedProgressFragment;
import ca.lambton.habittracker.habit.view.quote.QuoteFragment;

public class HomeFragment extends Fragment {

    private FirebaseUser mUser;
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
        mUser = FirebaseAuth.getInstance().getCurrentUser();

        habitViewModel = new ViewModelProvider(requireActivity()).get(HabitViewModel.class);
        notificationChannel();

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Fragment calendarFragment = ProgressCalendarFragment.newInstance((int) finalResult);
        supportFragmentManager.beginTransaction().replace(R.id.home_calendar_view, calendarFragment).commit();

        Fragment quoteDayFragment = new QuoteFragment();
        supportFragmentManager.beginTransaction().replace(R.id.quoteDayFragmentView, quoteDayFragment).commit();

        DailyProgressFragment dailyProgressFragment = new DailyProgressFragment();
        supportFragmentManager.beginTransaction().replace(R.id.daily_habit_progress, dailyProgressFragment).commit();


        Fragment summarizedProgress = new SummarizedProgressFragment();
        getParentFragmentManager().beginTransaction().replace(R.id.summarizedProgressView, summarizedProgress).commit();


        if (mUser != null) {
            String uName = mUser.getDisplayName() != null ? mUser.getDisplayName() : "";
            String userDisplay = getResources().getString(R.string.hello) + " " + uName;
            binding.greetingMessageLabel.setText(userDisplay);
        }

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    }

    @Override
    public void onStart() {
        super.onStart();
        todayCalendarProgress();
        calendarNotifications();
    }

    public void calendarNotifications() {
        habitViewModel.getAllHabit().observe(getViewLifecycleOwner(), habits -> {

            List<Notification> notifications = new ArrayList<>();

            List<Habit> habitWithTimer = habits.stream().filter(user -> user.getUserId().equals(mUser.getUid())).filter(timer -> timer.getTimer() != null).filter(alarmStatus -> !alarmStatus.isAlarmStarted()).collect(Collectors.toList());

            habitWithTimer.forEach(habit -> {
                Notification notification = new Notification();
                notification.setId((int) habit.getId());
                notification.setMessage(habit.getName());

                Calendar calendarStart = Calendar.getInstance();
                calendarStart.setTimeInMillis(habit.getStartDate());

                Calendar calendarEnd = Calendar.getInstance();
                calendarEnd.setTimeInMillis(habit.getEndDate());

                try {
                    DateFormat format = new SimpleDateFormat("HH:mm a", Locale.getDefault());
                    Date time = format.parse(habit.getTimer());
                    Calendar calendarTime = Calendar.getInstance();
                    assert time != null;
                    calendarTime.setTime(time);

                    LocalDateTime startNotification = LocalDateTime.of(calendarStart.get(Calendar.YEAR), calendarStart.get(Calendar.MONTH), calendarStart.get(Calendar.DAY_OF_MONTH), calendarTime.get(Calendar.HOUR_OF_DAY), calendarTime.get(Calendar.MINUTE));
                    LocalDateTime endNotification = LocalDateTime.of(calendarEnd.get(Calendar.YEAR), calendarEnd.get(Calendar.MONTH), calendarEnd.get(Calendar.DAY_OF_MONTH), calendarTime.get(Calendar.HOUR_OF_DAY), calendarTime.get(Calendar.MINUTE));

                    notification.setStartNotification(startNotification);
                    notification.setEndNotification(endNotification);

                    notifications.add(notification);
                    habit.setAlarmStarted(true);
                    habitViewModel.updateHabit(habit);

                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }

            });
            if (notifications.size() > 0)
                Utils.scheduleNotifications(requireContext(), notifications);

        });
    }


    public void todayCalendarProgress() {

        habitViewModel.getAllProgress().observe(getViewLifecycleOwner(), habitProgresses1 -> {

            LocalDate todayDate = LocalDate.now();
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
            finalResult = (todayProgress / totalFrequencies) * 100;

            Fragment calendarFragment = ProgressCalendarFragment.newInstance((int) finalResult);
            supportFragmentManager.beginTransaction().replace(R.id.home_calendar_view, calendarFragment).commit();
        });
    }

    private void notificationChannel() {

        CharSequence name = "Habitude Reminder";
        String description = "Please complete your Habit";
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel channel = new NotificationChannel("ReminderChannel", name, importance);
        channel.setDescription(description);

        NotificationManager notificationManager = requireActivity().getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }

}
