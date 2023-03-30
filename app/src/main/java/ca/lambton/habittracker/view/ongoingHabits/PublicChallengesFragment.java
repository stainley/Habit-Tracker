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

import ca.lambton.habittracker.databinding.FragmentPublicChallengesBinding;
import ca.lambton.habittracker.habit.model.Habit;
import ca.lambton.habittracker.habit.viewmodel.HabitViewModel;
import ca.lambton.habittracker.habit.viewmodel.HabitViewModelFactory;


public class PublicChallengesFragment extends Fragment {
    private FragmentPublicChallengesBinding binding;
    private HabitViewModel habitViewModel;
    private OngoingHabitsRecycleAdapter privateOngoingHabitListAdapter;
    private final List<Habit> habits = new ArrayList<>();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = FragmentPublicChallengesBinding.inflate(LayoutInflater.from(requireContext()));

        habitViewModel = new ViewModelProvider(this, new HabitViewModelFactory(requireActivity().getApplication())).get(HabitViewModel.class);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        RecyclerView recyclerView = binding.publicOngoingHabitsList;

        privateOngoingHabitListAdapter = new OngoingHabitsRecycleAdapter(habits, getOnCallbackOngoingHabit(habits, false), this.getContext(), false);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerView.setAdapter(privateOngoingHabitListAdapter);

        return binding.getRoot();
    }

    @NonNull
    private OngoingHabitsRecycleAdapter.OnOngoingHabitsCallback getOnCallbackOngoingHabit(List<Habit> habits, boolean isGroup) {
        return (position, isGroup1) -> {

        };
    }

    @Override
    public void onStart() {
        super.onStart();

        habitViewModel.getAllHabitCloud().observe(getViewLifecycleOwner(), habitResult -> {
            this.habits.clear();


            this.habits.addAll(habitResult);
            privateOngoingHabitListAdapter.notifyItemRangeChanged(0, habitResult.size());
        });
    }
}