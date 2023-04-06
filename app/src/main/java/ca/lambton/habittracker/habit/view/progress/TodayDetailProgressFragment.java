package ca.lambton.habittracker.habit.view.progress;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import ca.lambton.habittracker.R;
import ca.lambton.habittracker.databinding.FragmentTodayDetailProgressBinding;
import ca.lambton.habittracker.habit.model.HabitProgress;
import ca.lambton.habittracker.habit.model.Progress;
import ca.lambton.habittracker.habit.viewmodel.HabitViewModel;


public class TodayDetailProgressFragment extends Fragment {
    FragmentTodayDetailProgressBinding binding;
    private HabitViewModel habitViewModel;
    private TodayDetailProgressAdapter progressAdapter;
    private final List<DailyProgressData> progressDataList = new ArrayList<>();
    private FirebaseUser mUser;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = FragmentTodayDetailProgressBinding.inflate(LayoutInflater.from(requireContext()));
        mUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        RecyclerView recycleDetailedProgress = binding.recycleDetailedProgress;

        habitViewModel = new ViewModelProvider(requireActivity()).get(HabitViewModel.class);
        progressAdapter = new TodayDetailProgressAdapter(progressDataList);

        recycleDetailedProgress.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));

        recycleDetailedProgress.setAdapter(progressAdapter);

        return binding.getRoot();
    }

    public void obtainProgressByHabit() {
        LocalDate today = LocalDate.now();

        habitViewModel.getAllProgress().observe(getViewLifecycleOwner(), habitProgressResult -> {

            List<HabitProgress> myHabitProgressFiltered = habitProgressResult.stream()
                    .filter(dbUser -> dbUser.getHabit().getUserId().equals(mUser.getUid()))
                    .collect(Collectors.toList());

            myHabitProgressFiltered.forEach(habitProgress -> {

                Map<Long, Integer> progressGroupBy = habitProgress.getProgressList().stream()
                        .filter(oldDate -> LocalDate.parse(oldDate.getDate()).isEqual(today) || LocalDate.parse(oldDate.getDate()).isAfter(today))
                        .collect(Collectors.groupingBy(Progress::getHabitId, Collectors.summingInt(Progress::getCounter)));

                float frequency = habitProgress.getHabit().getFrequency();

                if (habitProgress.getProgressList().size() == 0) {
                    progressDataList.add(new DailyProgressData(requireContext(), habitProgress.getHabit().getName(), getProgressIcon(0)));
                }

                progressGroupBy.forEach((habitId, total) -> {

                    float result = (total / frequency) * 100;

                    progressDataList.add(new DailyProgressData(requireContext(), habitProgress.getHabit().getName(), getProgressIcon((int) result)));
                });
            });

            progressAdapter.notifyItemRangeChanged(0, myHabitProgressFiltered.size());
        });
    }

    private Drawable getProgressIcon(int percentage) {
        Drawable icon = null;

        if (percentage == 0) {
            icon = AppCompatResources.getDrawable(requireContext(), R.drawable.percent_0);
        } else if (percentage > 0 && percentage <= 49) {
            icon = AppCompatResources.getDrawable(requireContext(), R.drawable.percent_25);
        } else if (percentage >= 50 && percentage <= 74) {
            icon = AppCompatResources.getDrawable(requireContext(), R.drawable.percent_50);
        } else if (percentage >= 75 && percentage <= 95) {
            icon = AppCompatResources.getDrawable(requireContext(), R.drawable.percent_75);
        } else if (percentage > 95) {
            icon = AppCompatResources.getDrawable(requireContext(), R.drawable.percent_100);
        }

        return icon;
    }

    @Override
    public void onStart() {
        super.onStart();
        obtainProgressByHabit();
    }

    static class DailyProgressData {

        private final String habitName;
        private final Drawable imageDrawable;
        private final Context context;

        public DailyProgressData(Context context, String habitName, Drawable imageDrawable) {
            this.habitName = habitName;
            this.imageDrawable = imageDrawable;
            this.context = context;
        }

        public String getHabitName() {
            return habitName;
        }

        public Drawable getProgressValue() {
            if (imageDrawable == null) {
                return AppCompatResources.getDrawable(context, R.drawable.percent_0);
            }
            return imageDrawable;
        }
    }
}
