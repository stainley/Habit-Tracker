package ca.lambton.habittracker.view.ongoingHabits;

import static ca.lambton.habittracker.view.ongoingHabits.PublicChallengesFragmentDirections.actionPublicChallengesFragmentToNavHabitDetail;

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

import ca.lambton.habittracker.databinding.FragmentPublicChallengesBinding;
import ca.lambton.habittracker.habit.model.Habit;
import ca.lambton.habittracker.habit.viewmodel.HabitViewModel;
import ca.lambton.habittracker.habit.viewmodel.HabitViewModelFactory;
import ca.lambton.habittracker.view.ongoingHabits.adapter.OngoingHabitPublicAdapter;

public class PublicChallengesFragment extends Fragment {
    private FragmentPublicChallengesBinding binding;
    private HabitViewModel habitViewModel;
    private OngoingHabitPublicAdapter ongoingHabitPublicAdapter;
    private final List<Habit> habits = new ArrayList<>();
    private RecyclerView recyclerView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = FragmentPublicChallengesBinding.inflate(LayoutInflater.from(requireContext()));
        recyclerView = binding.publicOngoingHabitsList;

        habitViewModel = new ViewModelProvider(this, new HabitViewModelFactory(requireActivity().getApplication())).get(HabitViewModel.class);

        ongoingHabitPublicAdapter = new OngoingHabitPublicAdapter(habits, position -> {
            NavDirections navDirections = actionPublicChallengesFragmentToNavHabitDetail().setHabit(habits.get(position));
            Navigation.findNavController(requireView()).navigate(navDirections);
        });

        habitViewModel.getAllHabitCloud().observe(this, habitResult -> {
            this.habits.clear();
            this.habits.addAll(habitResult);
            ongoingHabitPublicAdapter.notifyItemRangeChanged(0, habitResult.size());
        });

        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        recyclerView.setAdapter(ongoingHabitPublicAdapter);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();

    }
}