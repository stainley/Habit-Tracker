package ca.lambton.habittracker.habit.view.complete;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import java.util.concurrent.atomic.AtomicInteger;

import ca.lambton.habittracker.databinding.FragmentCollectScoreBinding;
import ca.lambton.habittracker.habit.model.HabitProgress;
import ca.lambton.habittracker.habit.model.Progress;
import ca.lambton.habittracker.habit.viewmodel.HabitViewModel;
import ca.lambton.habittracker.habit.viewmodel.HabitViewModelFactory;
import ca.lambton.habittracker.util.Utils;

public class CollectScoreFragment extends Fragment {

    private FragmentCollectScoreBinding binding;

    private HabitViewModel habitViewModel;

    private HabitProgress habitProgress;

    private int score = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = FragmentCollectScoreBinding.inflate(LayoutInflater.from(requireContext()));

        habitViewModel = new ViewModelProvider(this, new HabitViewModelFactory(getActivity().getApplication())).get(HabitViewModel.class);
        habitProgress = CollectScoreFragmentArgs.fromBundle(requireArguments()).getHabitProgress();

        if (habitProgress != null) {
            AtomicInteger totalProgress = new AtomicInteger();

            totalProgress.addAndGet(habitProgress.getProgressList().stream().filter(progress -> progress.getHabitId() != -1).mapToInt(Progress::getCounter).sum());

            if (habitProgress.getProgressList().size() == habitProgress.getHabit().getFrequency()) {
                score = 30;
                binding.onCompletionTextView.setText("On completing the first day of your habit.");
                binding.youEarnedTextView.setText("You earned 30 points.");
            } else {
                int totalTimesToComplete;
                if (habitProgress.getHabit().getFrequencyUnit().equals("DAILY")) {
                    totalTimesToComplete = habitProgress.getHabit().getFrequency() * (int) Utils.getTotalDays(habitProgress.getHabit());
                } else if (habitProgress.getHabit().getFrequencyUnit().equals("WEEKLY")) {
                    totalTimesToComplete = habitProgress.getHabit().getFrequency() * (int) Utils.getTotalOfWeeks(habitProgress.getHabit());
                } else {
                    if (Utils.getTotalDays(habitProgress.getHabit()) < 30 || Utils.getTotalDays(habitProgress.getHabit()) == 30) {
                        totalTimesToComplete = habitProgress.getHabit().getFrequency() * 1;
                    } else {
                        totalTimesToComplete = habitProgress.getHabit().getFrequency() * (int) Utils.getTotalOfMonths(habitProgress.getHabit());
                    }
                }

                int percentage = (totalProgress.get() * 100) / totalTimesToComplete;

                if (percentage >= 15 && percentage < 25) {
                    score = 50;
                    binding.onCompletionTextView.setText("On completion of " + percentage + "% of you habit duration");
                    binding.youEarnedTextView.setText("You earned 50 points.");
                } else if (percentage >= 25 && percentage < 50) {
                    score = 100;
                    binding.onCompletionTextView.setText("On completion of " + percentage + "% of you habit duration");
                    binding.youEarnedTextView.setText("You earned 100 points.");
                } else if (percentage >= 50 && percentage < 75) {
                    score = 150;
                    binding.onCompletionTextView.setText("On completion of " + percentage + "% of you habit duration");
                    binding.youEarnedTextView.setText("You earned 150 points.");
                } else if (percentage >= 75 && percentage < 100) {
                    score = 200;
                    binding.onCompletionTextView.setText("On completion of " + percentage + "% of you habit duration");
                    binding.youEarnedTextView.setText("You earned 200 points.");
                } else if (percentage == 100) {
                    score = 300;
                    binding.onCompletionTextView.setText("On completion of " + percentage + "% of you habit duration");
                    binding.youEarnedTextView.setText("You earned 300 points.");
                }
                else {
                    score = 30;
                    binding.onCompletionTextView.setText("On completion of " + percentage + "% of you habit duration");
                    binding.youEarnedTextView.setText("You earned 30 points.");
                }
            }
        }

        binding.collectButton.setOnClickListener(this::collectScore);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return binding.getRoot();
    }

    private void collectScore(View view) {
        habitProgress.getHabit().setScore(habitProgress.getHabit().getScore() + score);
        habitViewModel.updateHabit(habitProgress.getHabit());

        Navigation.findNavController(view).popBackStack();
    }
}
