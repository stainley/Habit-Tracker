package ca.lambton.habittracker.view.ongoingHabits;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.Arrays;

import ca.lambton.habittracker.databinding.FragmentGroupHabitDetailBinding;

public class GroupHabitDetailFragment extends Fragment {

    FragmentGroupHabitDetailBinding binding;

    private GridView groupCircularProgressGrid;

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

        return binding.getRoot();
    }
}