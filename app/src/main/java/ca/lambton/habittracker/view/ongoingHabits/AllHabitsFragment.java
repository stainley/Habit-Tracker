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

    private final List<Habit> privateHabits = new ArrayList<>();
    private final List<Habit> groupHabits = new ArrayList<>();

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
        binding = FragmentAllHabitsBinding.inflate(LayoutInflater.from(requireContext()));
        habitViewModel = new ViewModelProvider(this, new HabitViewModelFactory(getActivity().getApplication())).get(HabitViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        RecyclerView recyclerView = binding.privateOngoingHabitsList;
        //RecyclerView groupRecyclerView = binding.groupOngoingHabitsList;

        privateOngoingHabitListAdapter = new OngoingHabitsRecycleAdapter(privateHabits, getOnCallbackOngoingHabit(privateHabits, false), this.getContext(), false);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerView.setAdapter(privateOngoingHabitListAdapter);

//        groupOngoingHabitListAdapter = new OngoingHabitsRecycleAdapter(groupHabits, getOnCallbackOngoingHabit(groupHabits, true), this.getContext(), true);
//        groupRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
//        groupRecyclerView.setAdapter(groupOngoingHabitListAdapter);

        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();

        habitViewModel.getAllPersonalHabit().observe(getViewLifecycleOwner(), result -> {
            this.privateHabits.clear();
            this.privateHabits.addAll(result);
            privateOngoingHabitListAdapter.notifyDataSetChanged();
        });

//        habitViewModel.getAllPublicHabits().observe(getViewLifecycleOwner(), result -> {
//            this.groupHabits.clear();
//            this.groupHabits.addAll(result);
//            groupOngoingHabitListAdapter.notifyDataSetChanged();
//        });
    }

    @NonNull
    private OngoingHabitsRecycleAdapter.OnOngoingHabitsCallback getOnCallbackOngoingHabit(List<Habit> habits, Boolean isGroup) {
        return (position, isGroup1) -> {
            if (isGroup1) {
                Navigation.findNavController(getView()).navigate(R.id.groupHabitDetailFragment);
            } else {
                NavDirections navDirections = AllHabitsFragmentDirections.actionNavAllHabitToNavPrivateHabitDetail(null).setHabit(habits.get(position));
                Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_main).navigate(navDirections);
            }
        };
    }
}
