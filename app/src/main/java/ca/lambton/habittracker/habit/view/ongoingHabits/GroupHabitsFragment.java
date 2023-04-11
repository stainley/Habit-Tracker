package ca.lambton.habittracker.habit.view.ongoingHabits;

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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import ca.lambton.habittracker.R;
import ca.lambton.habittracker.databinding.FragmentGroupHabitsBinding;
import ca.lambton.habittracker.habit.model.HabitProgress;
import ca.lambton.habittracker.habit.viewmodel.HabitViewModel;

public class GroupHabitsFragment extends Fragment {

    FragmentGroupHabitsBinding binding;
    private OngoingHabitsRecycleAdapter groupOngoingHabitListAdapter;

    HabitViewModel habitViewModel;

    private FirebaseUser mUser;

    private final List<HabitProgress> habits = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = FragmentGroupHabitsBinding.inflate(LayoutInflater.from(requireContext()));
        habitViewModel = new ViewModelProvider(requireActivity()).get(HabitViewModel.class);
        mUser = FirebaseAuth.getInstance().getCurrentUser();
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
    private OngoingHabitsRecycleAdapter.OnOngoingHabitsCallback getOnCallbackOngoingHabit(List<HabitProgress> habits, boolean isGroup) {
        return new OngoingHabitsRecycleAdapter.OnOngoingHabitsCallback() {
            @Override
            public void onRowClicked(int position, boolean isGroup) {
                if (isGroup) {
                    Navigation.findNavController(getView()).navigate(R.id.groupHabitDetailFragment);
                } else {
                    NavDirections navDirections = AllHabitsFragmentDirections.actionAllHabitsFragmentToPrivateHabitDetailFragment(null).setHabitProgress(habits.get(position));
                    Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_main).navigate(navDirections);
                }
            }

            @Override
            public void getProgressList(TextView habitPercentageNumText, CircularProgressIndicator habitProgressbar, int totalTimesToComplete, int position, boolean isGroup) {

            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        this.habits.clear();

        habitViewModel.getAllProgress().observe(this, habitProgressResult -> {

            List<HabitProgress> resultData = habitProgressResult.stream()
                    .filter(progress -> progress.getHabit().getUserId().equals(mUser.getUid()))
                    .collect(Collectors.toList());

            habits.addAll(resultData);
            groupOngoingHabitListAdapter.notifyDataSetChanged();
        });
    }
}