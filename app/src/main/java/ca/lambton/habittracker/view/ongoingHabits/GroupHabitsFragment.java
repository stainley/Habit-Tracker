package ca.lambton.habittracker.view.ongoingHabits;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ca.lambton.habittracker.databinding.FragmentGroupHabitsBinding;
import ca.lambton.habittracker.habit.model.Habit;
import ca.lambton.habittracker.habit.viewmodel.HabitViewModel;

public class GroupHabitsFragment extends Fragment {

    FragmentGroupHabitsBinding binding;
    private OngoingHabitsRecycleAdapter groupOngoingHabitListAdapter;

    HabitViewModel habitViewModel;

    private final List<Habit> habits = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = FragmentGroupHabitsBinding.inflate(LayoutInflater.from(requireContext()));
        habitViewModel = new ViewModelProvider(requireActivity()).get(HabitViewModel.class);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        RecyclerView groupRecyclerView = binding.groupOngoingHabitsList;


        groupOngoingHabitListAdapter = new OngoingHabitsRecycleAdapter(habits, getOnCallbackOngoingHabit(habits, true), this.getContext(), true);
        groupRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        groupRecyclerView.setAdapter(groupOngoingHabitListAdapter);

        return binding.getRoot();
    }

    @NonNull
    private OngoingHabitsRecycleAdapter.OnOngoingHabitsCallback getOnCallbackOngoingHabit(List<Habit> habits, Boolean isGroup) {
        return (position, isGroup1) -> {

            NavDirections groupHabitsFragmentDirections = GroupHabitsFragmentDirections.actionGroupHabitsFragmentToGroupHabitDetailFragment();
            Navigation.findNavController(requireView()).navigate(groupHabitsFragmentDirections);
        };
    }

    @Override
    public void onStart() {
        super.onStart();

        habitViewModel.getAllHabit().observe(getViewLifecycleOwner(), result -> {
            this.habits.clear();
            this.habits.addAll(result);
            groupOngoingHabitListAdapter.notifyItemRangeChanged(0, result.size());
        });
    }
}