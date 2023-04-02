package ca.lambton.habittracker.view.ongoingHabits;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.progressindicator.CircularProgressIndicator;

import java.util.ArrayList;
import java.util.List;

import ca.lambton.habittracker.R;
import ca.lambton.habittracker.databinding.FragmentPrivateHabitsBinding;
import ca.lambton.habittracker.habit.model.Habit;
import ca.lambton.habittracker.habit.viewmodel.HabitViewModel;

public class PrivateHabitsFragment extends Fragment {

    FragmentPrivateHabitsBinding binding;
    private OngoingHabitsRecycleAdapter privateOngoingHabitListAdapter;

    HabitViewModel habitViewModel;

    private final List<Habit> habits = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = FragmentPrivateHabitsBinding.inflate(LayoutInflater.from(requireContext()));
        habitViewModel = new ViewModelProvider(requireActivity()).get(HabitViewModel.class);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        RecyclerView recyclerView = binding.privateOngoingHabitsList;

        privateOngoingHabitListAdapter = new OngoingHabitsRecycleAdapter(habits, getOnCallbackOngoingHabit(habits, false), this.getContext(), false);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerView.setAdapter(privateOngoingHabitListAdapter);

        return binding.getRoot();
    }

    @NonNull
    private OngoingHabitsRecycleAdapter.OnOngoingHabitsCallback getOnCallbackOngoingHabit(List<Habit> habits, boolean isGroup) {
        return new OngoingHabitsRecycleAdapter.OnOngoingHabitsCallback() {
            @Override
            public void onRowClicked(int position, boolean isGroup) {
                if (isGroup) {
                    Navigation.findNavController(getView()).navigate(R.id.groupHabitDetailFragment);
                } else {
                    NavDirections navDirections = AllHabitsFragmentDirections.actionNavAllHabitToNavPrivateHabitDetail(null).setHabit(habits.get(position));
                    Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_main).navigate(navDirections);
                }
            }

            @Override
            public void getProgressList(TextView habitPercentageNumText, CircularProgressIndicator habitProgressbar, int totalTimesToComplete, int position) {

            }
        };
    }


    @Override
    public void onStart() {
        super.onStart();

        habitViewModel.getAllPersonalHabit().observe(getViewLifecycleOwner(), result -> {
            this.habits.clear();
            this.habits.addAll(result);
            privateOngoingHabitListAdapter.notifyDataSetChanged();
        });
    }
}
