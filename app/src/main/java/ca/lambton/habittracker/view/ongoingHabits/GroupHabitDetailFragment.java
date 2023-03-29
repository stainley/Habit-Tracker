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

import ca.lambton.habittracker.databinding.FragmentGroupHabitDetailBinding;

public class GroupHabitDetailFragment extends Fragment {

    FragmentGroupHabitDetailBinding binding;

    private GridView groupCircularProgressGrid;

    private GridView ongoingHabitDetailGridInfo;

    public GroupHabitDetailFragment() {
        // Required empty public constructor
    }

    public static GroupHabitDetailFragment newInstance(String param1, String param2) {
        GroupHabitDetailFragment fragment = new GroupHabitDetailFragment();
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
        binding = FragmentGroupHabitDetailBinding.inflate(inflater, container, false);

        groupCircularProgressGrid = (GridView) binding.groupCircularProgressGrid;

        GroupCircularProgressGridAdapter adapter = new GroupCircularProgressGridAdapter(getContext(), new ArrayList<String>(Arrays.asList("75", "85", "67", "92")));
        groupCircularProgressGrid.setAdapter(adapter);

//        RadioGroup radioGroup = findViewById(R.id.my_radio_group);
//        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
//                // Do something when a RadioButton is checked
//                RadioButton checkedRadioButton = findViewById(checkedId);
//                String checkedText = checkedRadioButton.getText().toString();
//                Toast.makeText(getApplicationContext(), "Selected option: " + checkedText, Toast.LENGTH_SHORT).show();
//            }
//        });

        ongoingHabitDetailGridInfo = (GridView) binding.ongoingHabitDetailGridView;
        ArrayList<OngoingHabitDetailGridInfo> ongoingHabitDetailGridInfoModelArrayList = new ArrayList<OngoingHabitDetailGridInfo>();
        ongoingHabitDetailGridInfoModelArrayList.add(new OngoingHabitDetailGridInfo("Your current streak", "5"));
        ongoingHabitDetailGridInfoModelArrayList.add(new OngoingHabitDetailGridInfo("Days completed", "42/100"));
        ongoingHabitDetailGridInfoModelArrayList.add(new OngoingHabitDetailGridInfo("Your highest streak", "10"));
        ongoingHabitDetailGridInfoModelArrayList.add(new OngoingHabitDetailGridInfo("This week’s target", "2/3"));
        ongoingHabitDetailGridInfoModelArrayList.add(new OngoingHabitDetailGridInfo("Days missed", "5"));
        ongoingHabitDetailGridInfoModelArrayList.add(new OngoingHabitDetailGridInfo("This month’s target", "5/15"));
        OngoingHabitDetailGridInfoAdapter progressAdapter = new OngoingHabitDetailGridInfoAdapter(getContext(), ongoingHabitDetailGridInfoModelArrayList);
        ongoingHabitDetailGridInfo.setAdapter(progressAdapter);

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