package ca.lambton.habittracker.view.fragment.habit;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.squareup.picasso.Picasso;

import java.util.List;

import ca.lambton.habittracker.databinding.FragmentHabitDetailBinding;
import ca.lambton.habittracker.habit.model.Habit;
import ca.lambton.habittracker.habit.viewmodel.HabitViewModel;
import ca.lambton.habittracker.habit.viewmodel.HabitViewModelFactory;

public class HabitDetailFragment extends Fragment {
    FragmentHabitDetailBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHabitDetailBinding.inflate(inflater, container, false);

        assert getArguments() != null;
        Habit habit = (Habit) getArguments().getSerializable("habit");

        if (habit != null && habit.getImagePath() != null) {
            int drawable = requireContext().getResources().getIdentifier(habit.getImagePath(), "drawable", requireContext().getPackageName());
            if (drawable > 0) {
                Picasso.get().load(drawable).into(binding.detailImage);
            }
        }

        if (habit != null) {
            String frequency = String.valueOf(habit.getFrequency());

            binding.frequencyValue.setText(frequency);
            binding.durationValue.setText(habit.getDuration());
            binding.daysValue.setText(habit.getFrequency());
            binding.titleHabit.setText(habit.getName());
            binding.messageValue.setText(habit.getDescription());
        }


        return binding.getRoot();
    }
}
