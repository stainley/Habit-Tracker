package ca.lambton.habittracker.view.ongoingHabits;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;

import ca.lambton.habittracker.R;
import ca.lambton.habittracker.databinding.FragmentPrivateHabitDetailBinding;
import ca.lambton.habittracker.habit.model.Habit;

public class PrivateHabitDetailFragment extends Fragment {

    FragmentPrivateHabitDetailBinding binding;
    private GridView ongoingHabitDetailGridInfo;

    private GridView achievementGridInfo;

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentPrivateHabitDetailBinding.inflate(inflater, container, false);

        assert getArguments() != null;
        Habit habit = (Habit) getArguments().getSerializable("habit");

        if (habit != null) {
            System.out.println("habit " + habit);
        }

        if (habit != null) {
            binding.habitNameLabel.setText(habit.getName());
        }

        ongoingHabitDetailGridInfo = (GridView) binding.ongoingHabitDetailGridView;
        ArrayList<OngoingHabitDetailGridInfo> ongoingHabitDetailGridInfoModelArrayList = new ArrayList<OngoingHabitDetailGridInfo>();
        ongoingHabitDetailGridInfoModelArrayList.add(new OngoingHabitDetailGridInfo("Your current streak", "0"));
        ongoingHabitDetailGridInfoModelArrayList.add(new OngoingHabitDetailGridInfo("Days completed", "0/100"));
        ongoingHabitDetailGridInfoModelArrayList.add(new OngoingHabitDetailGridInfo("Your highest streak", "0"));

        if (habit != null) {
            if (habit.getFrequencyUnit() == "DAILY") {
                ongoingHabitDetailGridInfoModelArrayList.add(new OngoingHabitDetailGridInfo("This day’s target", "0/3"));
            } else if (habit.getFrequencyUnit() == "WEEKLY") {
                ongoingHabitDetailGridInfoModelArrayList.add(new OngoingHabitDetailGridInfo("This week’s target", "0/3"));
            }
            else {
                ongoingHabitDetailGridInfoModelArrayList.add(new OngoingHabitDetailGridInfo("This month’s target", "0/15"));
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
}