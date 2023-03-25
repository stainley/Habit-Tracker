package ca.lambton.habittracker.view.fragment.calendar;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import ca.lambton.habittracker.databinding.CalendarDayLayoutBinding;
import ca.lambton.habittracker.util.DayData;

public class ProgressCalendarFragment extends Fragment {

    private int percentage;

    public static ProgressCalendarFragment newInstance(int percentage) {
        ProgressCalendarFragment fragment = new ProgressCalendarFragment();
        Bundle args = new Bundle();
        args.putInt("PERCENT", percentage);
        fragment.setArguments(args);
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        CalendarDayLayoutBinding binding = CalendarDayLayoutBinding.inflate(inflater);
        View view = binding.getRoot();

        GridView calendarGridView = binding.calendarGridView;

        if (getArguments() != null) {
            percentage = getArguments().getInt("PERCENT");
        }

        CalendarAdapter calendarAdapter = new CalendarAdapter(requireContext(), getWeekdaysWithPercentage(percentage));
        calendarGridView.setAdapter(calendarAdapter);

        return view;
    }

    private List<DayData> getWeekdaysWithPercentage(int percentage) {
        List<DayData> dayDataList = new ArrayList<>();

        Calendar calendar = Calendar.getInstance(TimeZone.getDefault(), Locale.CANADA);
        //calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);

        for (int i = 0; i < 6; i++) {
            String dayOfWeek = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault());
            int dayNumber = calendar.getTime().getDate();
            DayData dayData = new DayData(dayOfWeek, dayNumber, i == 0 ? percentage : 0);

            System.out.println("First Day of Week: " + dayNumber);
            dayDataList.add(dayData);
            calendar.add(Calendar.DAY_OF_WEEK, 1);
        }
        return dayDataList;
    }
}
