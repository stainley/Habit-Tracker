package ca.lambton.habittracker.view.ongoingHabits;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ca.lambton.habittracker.databinding.FragmentGroupHabitsBinding;
import ca.lambton.habittracker.habit.model.Habit;
import ca.lambton.habittracker.habit.viewmodel.HabitViewModel;
import ca.lambton.habittracker.habit.viewmodel.HabitViewModelFactory;

public class GroupHabitsFragment extends Fragment {

    FragmentGroupHabitsBinding binding;
    private OngoingHabitsRecycleAdapter groupOngoingHabitListAdapter;

    HabitViewModel habitViewModel;

    private final List<Habit> habits = new ArrayList<>();

    public GroupHabitsFragment() {
        // Required empty public constructor
    }

    public static GroupHabitsFragment newInstance(String param1, String param2) {
        GroupHabitsFragment fragment = new GroupHabitsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentGroupHabitsBinding.inflate(inflater, container, false);
        RecyclerView groupRecyclerView = binding.groupOngoingHabitsList;

        habitViewModel = new ViewModelProvider(this, new HabitViewModelFactory(getActivity().getApplication())).get(HabitViewModel.class);
        habitViewModel.getAllHabit().observe(getViewLifecycleOwner(), result -> {
            this.habits.clear();
            this.habits.addAll(result);
            groupOngoingHabitListAdapter.notifyDataSetChanged();
        });

        groupOngoingHabitListAdapter = new OngoingHabitsRecycleAdapter(habits, getOnCallbackOngoingHabit(habits), this.getContext(), true);
        groupRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        groupRecyclerView.setAdapter(groupOngoingHabitListAdapter);

        return binding.getRoot();
    }

    @NonNull
    private OngoingHabitsRecycleAdapter.OnOngoingHabitsCallback getOnCallbackOngoingHabit(List<Habit> habits) {
        return new OngoingHabitsRecycleAdapter.OnOngoingHabitsCallback() {

            @Override
            public void onRowClicked(int position) {

            }
        };
    }
}