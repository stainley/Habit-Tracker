package ca.lambton.habittracker.view.ongoingHabits;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;

import ca.lambton.habittracker.databinding.FragmentPrivateHabitDetailBinding;

public class PrivateHabitDetailFragment extends Fragment {

    FragmentPrivateHabitDetailBinding binding;
    private GridView ongoingHabitDetailGridInfo;

    public PrivateHabitDetailFragment() {
        // Required empty public constructor
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

        ongoingHabitDetailGridInfo = (GridView) binding.ongoingHabitDetailGridView;
        ArrayList<OngoingHabitDetailGridInfo> ongoingHabitDetailGridInfoModelArrayList = new ArrayList<OngoingHabitDetailGridInfo>();
        ongoingHabitDetailGridInfoModelArrayList.add(new OngoingHabitDetailGridInfo("Your current streak", "5"));
        ongoingHabitDetailGridInfoModelArrayList.add(new OngoingHabitDetailGridInfo("Days completed", "42/100"));
        ongoingHabitDetailGridInfoModelArrayList.add(new OngoingHabitDetailGridInfo("Your highest streak", "10"));
        ongoingHabitDetailGridInfoModelArrayList.add(new OngoingHabitDetailGridInfo("This week’s target", "2/3"));
        ongoingHabitDetailGridInfoModelArrayList.add(new OngoingHabitDetailGridInfo("Days missed", "5"));
        ongoingHabitDetailGridInfoModelArrayList.add(new OngoingHabitDetailGridInfo("This month’s target", "5/15"));
        OngoingHabitDetailGridInfoAdapter adapter = new OngoingHabitDetailGridInfoAdapter(getContext(), ongoingHabitDetailGridInfoModelArrayList);
        ongoingHabitDetailGridInfo.setAdapter(adapter);

        return binding.getRoot();
    }
}