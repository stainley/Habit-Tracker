package ca.lambton.habittracker.view.ongoingHabits;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import ca.lambton.habittracker.R;
import ca.lambton.habittracker.databinding.FragmentAllHabitsBinding;
import ca.lambton.habittracker.habit.model.Habit;
import ca.lambton.habittracker.habit.model.HabitProgress;
import ca.lambton.habittracker.habit.model.Progress;
import ca.lambton.habittracker.habit.viewmodel.HabitViewModel;
import ca.lambton.habittracker.habit.viewmodel.HabitViewModelFactory;

public class AllHabitsFragment extends Fragment {

    FragmentAllHabitsBinding binding;

    private OngoingHabitsRecycleAdapter privateOngoingHabitListAdapter;
    private OngoingHabitsRecycleAdapter groupOngoingHabitListAdapter;

    HabitViewModel habitViewModel;

    private final List<Habit> privateHabits = new ArrayList<>();
    private final List<Habit> groupHabits = new ArrayList<>();
    private FirebaseUser mUser;
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
        mUser = FirebaseAuth.getInstance().getCurrentUser();
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

    private OngoingHabitsRecycleAdapter.OnOngoingHabitsCallback getOnCallbackOngoingHabit(List<Habit> habits, Boolean isGroup) {
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
            public int getProgressList(TextView habitPercentageNumText, int totalTimesToComplete, int position) {
                AtomicInteger totalProgress = new AtomicInteger();
                habitViewModel.getAllProgress().observe(requireActivity(), habitProgresses1 -> {
                    List<HabitProgress> myHabitProgressFiltered = habitProgresses1.stream().filter(dbUser -> dbUser.getHabit().getUserId().equals(mUser.getUid())).collect(Collectors.toList());

                    for (HabitProgress hp : myHabitProgressFiltered) {
                        totalProgress.addAndGet(hp.getProgressList().stream().filter(progress -> progress.getHabitId() == habits.get(position).getId()).map(Progress::getCounter).mapToInt(Integer::intValue).sum());

                        if (totalProgress.get() == 0) {
                            habitPercentageNumText.setText("0%");
                        } else {
                            habitPercentageNumText.setText((totalProgress.get() * 100 / totalTimesToComplete) + "%");
                        }
                    }
                });

                return totalProgress.get();
            }
        };
    }
}
