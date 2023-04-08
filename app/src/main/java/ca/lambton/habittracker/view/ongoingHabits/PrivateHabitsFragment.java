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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import ca.lambton.habittracker.R;
import ca.lambton.habittracker.databinding.FragmentPrivateHabitsBinding;
import ca.lambton.habittracker.habit.model.HabitProgress;
import ca.lambton.habittracker.habit.model.Progress;
import ca.lambton.habittracker.habit.viewmodel.HabitViewModel;
import ca.lambton.habittracker.util.Utils;

public class PrivateHabitsFragment extends Fragment {

    FragmentPrivateHabitsBinding binding;
    private OngoingHabitsRecycleAdapter privateOngoingHabitListAdapter;

    HabitViewModel habitViewModel;

    private FirebaseUser mUser;

    private final List<HabitProgress> habitProgresses = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = FragmentPrivateHabitsBinding.inflate(LayoutInflater.from(requireContext()));
        habitViewModel = new ViewModelProvider(requireActivity()).get(HabitViewModel.class);
        mUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        RecyclerView recyclerView = binding.privateOngoingHabitsList;

        privateOngoingHabitListAdapter = new OngoingHabitsRecycleAdapter(habitProgresses, getOnCallbackOngoingHabit(habitProgresses, false), this.getContext(), false);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerView.setAdapter(privateOngoingHabitListAdapter);

        return binding.getRoot();
    }

    @NonNull
    private OngoingHabitsRecycleAdapter.OnOngoingHabitsCallback getOnCallbackOngoingHabit(List<HabitProgress> habits, boolean isGroup) {
        return new OngoingHabitsRecycleAdapter.OnOngoingHabitsCallback() {
            @Override
            public void onRowClicked(int position, boolean isGroup) {
                if (isGroup) {
                    Navigation.findNavController(requireView()).navigate(R.id.groupHabitDetailFragment);
                } else {
                    NavDirections navDirections = PrivateHabitsFragmentDirections.actionPrivateHabitsFragmentToPrivateHabitDetailFragment(null).setHabitProgress(habitProgresses.get(position));
                    Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_main).navigate(navDirections);
                }
            }

            @Override
            public void getProgressList(TextView habitPercentageNumText, CircularProgressIndicator habitProgressbar, int totalTimesToComplete, int position) {
                AtomicInteger totalProgress = new AtomicInteger();
                habitViewModel.getAllProgress().observe(requireActivity(), habitProgresses1 -> {
                    List<HabitProgress> myHabitProgressFiltered = habitProgresses1.stream()
                            .filter(dbUser -> dbUser.getHabit().getUserId().equals(mUser.getUid()))
                            .collect(Collectors.toList());

                    for (HabitProgress hp : myHabitProgressFiltered) {
                        //FIXME: error when deleting record java.lang.IndexOutOfBoundsException: Index: 3, Size: 0
                        totalProgress.addAndGet(hp.getProgressList().stream().filter(progress -> progress.getHabitId() == habitProgresses.get(position).getHabit().getId()).map(Progress::getCounter).mapToInt(Integer::intValue).sum());

                        if (totalProgress.get() == 0) {
                            habitPercentageNumText.setText("0%");
                        } else {
                            habitPercentageNumText.setText((totalProgress.get() * 100 / totalTimesToComplete) + "%");
                            habitProgressbar.setProgress(totalProgress.get() * 100 / totalTimesToComplete);
                        }
                    }
                });
            }
        };
    }


    @Override
    public void onStart() {
        super.onStart();
        this.habitProgresses.clear();

        habitViewModel.getAllProgress().observe(this, habitProgressResult -> {
            LocalDate today = LocalDate.now();

            List<HabitProgress> resultData = habitProgressResult.stream()
                    .filter(progress -> progress.getHabit().getUserId().equals(mUser.getUid()))
                    .filter(date -> {

                        LocalDate endDate = Instant.ofEpochMilli(date.getHabit().getEndDate()).atZone(ZoneId.systemDefault()).toLocalDate();
                        return today.isBefore(endDate);
                    })
                    .collect(Collectors.toList());

            habitProgresses.addAll(resultData);
            privateOngoingHabitListAdapter.notifyDataSetChanged();
        });
    }
}
