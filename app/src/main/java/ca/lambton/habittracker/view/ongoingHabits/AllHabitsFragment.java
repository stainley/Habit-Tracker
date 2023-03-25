package ca.lambton.habittracker.view.ongoingHabits;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ca.lambton.habittracker.R;
import ca.lambton.habittracker.databinding.FragmentAllHabitsBinding;
import ca.lambton.habittracker.habit.model.Habit;
import ca.lambton.habittracker.habit.viewmodel.HabitViewModel;
import ca.lambton.habittracker.habit.viewmodel.HabitViewModelFactory;

public class AllHabitsFragment extends Fragment {

    FragmentAllHabitsBinding binding;

    private OngoingHabitsRecycleAdapter privateOngoingHabitListAdapter;
    private OngoingHabitsRecycleAdapter groupOngoingHabitListAdapter;

    HabitViewModel habitViewModel;

    private final List<Habit> habits = new ArrayList<>();

    public AllHabitsFragment() {
        // Required empty public constructor
    }

    public static AllHabitsFragment newInstance(String param1, String param2) {
        AllHabitsFragment fragment = new AllHabitsFragment();
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
        binding = FragmentAllHabitsBinding.inflate(inflater, container, false);
        RecyclerView recyclerView = binding.privateOngoingHabitsList;
        RecyclerView groupRecyclerView = binding.groupOngoingHabitsList;

        habitViewModel = new ViewModelProvider(this, new HabitViewModelFactory(getActivity().getApplication())).get(HabitViewModel.class);
        habitViewModel.getAllHabit().observe(getViewLifecycleOwner(), result -> {
            this.habits.clear();
            this.habits.addAll(result);
            privateOngoingHabitListAdapter.notifyDataSetChanged();
            groupOngoingHabitListAdapter.notifyDataSetChanged();
        });

        privateOngoingHabitListAdapter = new OngoingHabitsRecycleAdapter(habits, getOnCallbackOngoingHabit(habits, false), this.getContext(), false);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerView.setAdapter(privateOngoingHabitListAdapter);

        groupOngoingHabitListAdapter = new OngoingHabitsRecycleAdapter(habits, getOnCallbackOngoingHabit(habits, true), this.getContext(), true);
        groupRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        groupRecyclerView.setAdapter(groupOngoingHabitListAdapter);

        return binding.getRoot();
    }

    @NonNull
    private OngoingHabitsRecycleAdapter.OnOngoingHabitsCallback getOnCallbackOngoingHabit(List<Habit> habits, Boolean isGroup) {
        return new OngoingHabitsRecycleAdapter.OnOngoingHabitsCallback() {

            @Override
            public void onRowClicked(int position, boolean isGroup) {
                if (isGroup) {
                    Navigation.findNavController(getView()).navigate(R.id.groupHabitDetailFragment);
                } else {
                    Navigation.findNavController(getView()).navigate(R.id.privateHabitDetailFragment);
                }
            }
        };
    }
}
