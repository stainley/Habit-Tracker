package ca.lambton.habittracker.habit.view.ongoingHabits;

import static ca.lambton.habittracker.habit.view.ongoingHabits.PublicChallengesFragmentDirections.actionPublicChallengesFragmentToNavHabitDetail;

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
import java.util.Calendar;
import java.util.Date;
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

    private OngoingHabitsRecycleAdapter publicOngoingHabitListAdapter;

    HabitViewModel habitViewModel;

    private final List<HabitProgress> habitProgresses = new ArrayList<>();
    private final List<HabitProgress> publicHabitProgresses = new ArrayList<>();
    private FirebaseUser mUser;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = FragmentAllHabitsBinding.inflate(LayoutInflater.from(requireContext()));
        habitViewModel = new ViewModelProvider(getViewModelStore(), new HabitViewModelFactory(requireActivity().getApplication())).get(HabitViewModel.class);
        mUser = FirebaseAuth.getInstance().getCurrentUser();

        RecyclerView recyclerView = binding.privateOngoingHabitsList;
        RecyclerView publicRecyclerView = binding.publicOngoingHabitsList;


        privateOngoingHabitListAdapter = new OngoingHabitsRecycleAdapter(habitProgresses, getOnCallbackOngoingHabit(habitProgresses, false), this.getContext(), false);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerView.setAdapter(privateOngoingHabitListAdapter);

        publicOngoingHabitListAdapter = new OngoingHabitsRecycleAdapter(publicHabitProgresses, getOnCallbackOngoingHabit(publicHabitProgresses, true), this.getContext(), true);
        publicRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        publicRecyclerView.setAdapter(publicOngoingHabitListAdapter);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();

        habitViewModel.getAllProgress().observe(this, habitProgressResult -> {
            clearAdapters();

            List<HabitProgress> resultData = habitProgressResult.stream()
                    .filter(progress -> progress.getHabit().getUserId().equals(mUser.getUid()) &&
                            progress.getHabit().getHabitType().equals("PERSONAL"))
                    .filter(date -> {
                        Calendar todayCalendar = Calendar.getInstance();
                        Date endDate = new Date(date.getHabit().getEndDate());
                        return endDate.after(todayCalendar.getTime());
                    })
                    .collect(Collectors.toList());
            habitProgresses.addAll(resultData);

            privateOngoingHabitListAdapter.notifyItemRangeChanged(0, resultData.size());

            List<HabitProgress> resultDataPublic = habitProgressResult.stream()
                    .filter(progress -> progress.getHabit().getUserId().equals(mUser.getUid()) &&
                            progress.getHabit().getHabitType().equals("PUBLIC"))
                    .filter(date -> {
                        Calendar todayCalendar = Calendar.getInstance();
                        Date endDate = new Date(date.getHabit().getEndDate());
                        return endDate.after(todayCalendar.getTime());
                    })
                    .collect(Collectors.toList());

            publicHabitProgresses.addAll(resultDataPublic);
            publicOngoingHabitListAdapter.notifyItemRangeChanged(0, resultDataPublic.size());

        });

    }

    private OngoingHabitsRecycleAdapter.OnOngoingHabitsCallback getOnCallbackOngoingHabit(List<HabitProgress> habits, Boolean isGroup) {
        return new OngoingHabitsRecycleAdapter.OnOngoingHabitsCallback() {
            @Override
            public void onRowClicked(int position, boolean isGroup) {
                if (isGroup) {
                    navigateToPublicHabitDetail(position);
                } else {
                    navigateToPrivateHabitDetail(position);
                }
            }

            private void navigateToPublicHabitDetail(int position) {
                Habit habit = publicHabitProgresses.get(position).getHabit();
                NavDirections navDirections = actionPublicChallengesFragmentToNavHabitDetail().setHabit(habit);
                Navigation.findNavController(requireView()).navigate(navDirections);
            }

            private void navigateToPrivateHabitDetail(int position) {
                HabitProgress habitProgress = habitProgresses.get(position);
                NavDirections navDirections = AllHabitsFragmentDirections.actionAllHabitsFragmentToPrivateHabitDetailFragment(null).setHabitProgress(habitProgress);
                Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_main).navigate(navDirections);
            }

            @Override
            public void getProgressList(TextView habitPercentageNumText, CircularProgressIndicator habitProgressbar, int totalTimesToComplete, int position, boolean isGroup) {
                AtomicInteger totalProgress = new AtomicInteger();
                String habitType = isGroup ? "PUBLIC" : "PERSONAL";
                habitViewModel.getAllProgress().observe(requireActivity(), habitProgresses1 -> {
                    List<HabitProgress> myHabitProgressFiltered = filterMyHabitProgresses(habitProgresses1, habitType);

                    for (HabitProgress hp : myHabitProgressFiltered) {
                        totalProgress.addAndGet(getHabitProgressTotal(hp, position));

                        if (totalProgress.get() == 0) {
                            habitPercentageNumText.setText("0%");
                        } else {
                            habitPercentageNumText.setText((totalProgress.get() * 100 / ((totalTimesToComplete == 0) ? 1 : totalTimesToComplete)) + "%");
                            habitProgressbar.setProgress(totalProgress.get() * 100 / ((totalTimesToComplete == 0) ? 1 : totalTimesToComplete));
                        }
                    }
                });
            }

            private List<HabitProgress> filterMyHabitProgresses(List<HabitProgress> habitProgresses, String habitType) {
                return habitProgresses.stream()
                        .filter(dbUser -> dbUser.getHabit().getUserId().equals(mUser.getUid()) && dbUser.getHabit().getHabitType().equals(habitType))
                        .filter(date -> {
                            Calendar todayCalendar = Calendar.getInstance();
                            Date endDate = new Date(date.getHabit().getEndDate());
                            return endDate.after(todayCalendar.getTime());
                        })
                        .collect(Collectors.toList());
            }

            private int getHabitProgressTotal(HabitProgress habitProgress, int position) {
                if (habitProgresses.size() == 0) {
                    return 0;
                }

                try {
                    long id = habitProgresses.get(position).getHabit().getId();
                    return habitProgress.getProgressList()
                            .stream()
                            .filter(progress -> progress.getHabitId() == id)
                            .map(Progress::getCounter)
                            .mapToInt(Integer::intValue)
                            .sum();
                } catch (IndexOutOfBoundsException ioe) {
                    System.err.println(ioe.getMessage());
                    return 0;
                }
            }
        };
    }

    /*private OngoingHabitsRecycleAdapter.OnOngoingHabitsCallback getOnCallbackOngoingHabit(List<HabitProgress> habits, Boolean isGroup) {
        return new OngoingHabitsRecycleAdapter.OnOngoingHabitsCallback() {
            @Override
            public void onRowClicked(int position, boolean isGroup) {
                if (isGroup) {
                    NavDirections navDirections = actionPublicChallengesFragmentToNavHabitDetail().setHabit(publicHabitProgresses.get(position).getHabit());
                    Navigation.findNavController(requireView()).navigate(navDirections);
                } else {
                    NavDirections navDirections = AllHabitsFragmentDirections.actionAllHabitsFragmentToPrivateHabitDetailFragment(null).setHabitProgress(habitProgresses.get(position));
                    Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_main).navigate(navDirections);
                }
            }

            @Override
            public void getProgressList(TextView habitPercentageNumText, CircularProgressIndicator habitProgressbar, int totalTimesToComplete, int position, boolean isGroup) {
                AtomicInteger totalProgress = new AtomicInteger();
                String habitType = isGroup ? "PUBLIC" : "PERSONAL";
                // TODO: load habit not expired
                habitViewModel.getAllProgress().observe(requireActivity(), habitProgresses1 -> {
                    List<HabitProgress> myHabitProgressFiltered = habitProgresses1.stream()
                            .filter(dbUser -> dbUser.getHabit().getUserId().equals(mUser.getUid()) && dbUser.getHabit().getHabitType().equals(habitType))
                            .filter(date -> {
                                Calendar todayCalendar = Calendar.getInstance();
                                Date endDate = new Date(date.getHabit().getEndDate());
                                return endDate.after(todayCalendar.getTime());
                            })
                            .collect(Collectors.toList());

                    for (HabitProgress hp : myHabitProgressFiltered) {

                        if (habitProgresses.size() > 0) {
                            totalProgress.addAndGet(hp.getProgressList()
                                    .stream()
                                    .filter(progress -> {
                                        try {
                                            long id = habitProgresses.get(position).getHabit().getId();
                                            if (progress.getHabitId() == id) {
                                                return true;
                                            }
                                        } catch (IndexOutOfBoundsException ioe) {
                                            System.err.println(ioe.getMessage());
                                            return false;
                                        }

                                        return false;
                                    })
                                    .map(Progress::getCounter)
                                    .mapToInt(Integer::intValue)
                                    .sum());
                            if (totalProgress.get() == 0) {
                                habitPercentageNumText.setText("0%");
                            } else {
                                habitPercentageNumText.setText((totalProgress.get() * 100 / ((totalTimesToComplete == 0) ? 1 : totalTimesToComplete)) + "%");
                                habitProgressbar.setProgress(totalProgress.get() * 100 / ((totalTimesToComplete == 0) ? 1 : totalTimesToComplete));
                            }
                        }

                    }
                });
            }
        };
    }
*/
    private void clearAdapters() {
        habitProgresses.clear();
        privateOngoingHabitListAdapter.notifyDataSetChanged();

        publicHabitProgresses.clear();
        publicOngoingHabitListAdapter.notifyDataSetChanged();
    }
}
