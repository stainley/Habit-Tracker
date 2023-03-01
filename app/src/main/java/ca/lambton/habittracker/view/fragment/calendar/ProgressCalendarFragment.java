package ca.lambton.habittracker.view.calendar;

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

    private CalendarDayLayoutBinding binding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //View view = inflater.inflate(R.layout.calendar_day_layout, container, false);
        binding = CalendarDayLayoutBinding.inflate(inflater);
        View view = binding.getRoot();

        GridView calendarGridView = binding.calendarGridView;

        CalendarAdapter calendarAdapter = new CalendarAdapter(requireContext(), getWeekdaysWithPercentage(100));
        calendarGridView.setAdapter(calendarAdapter);

        return view;
    }

    private List<DayData> getWeekdaysWithPercentage(int percentage) {
        List<DayData> dayDataList = new ArrayList<>();

        Calendar calendar = Calendar.getInstance(TimeZone.getDefault(), Locale.CANADA);
        //calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);

        for (int i = 0; i < 7; i++) {
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
