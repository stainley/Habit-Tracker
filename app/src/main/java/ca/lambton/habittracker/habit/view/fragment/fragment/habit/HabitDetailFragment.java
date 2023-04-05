package ca.lambton.habittracker.habit.view.fragment.fragment.habit;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import ca.lambton.habittracker.databinding.FragmentHabitDetailBinding;
import ca.lambton.habittracker.habit.model.Habit;
import ca.lambton.habittracker.habit.viewmodel.HabitViewModel;
import ca.lambton.habittracker.util.Duration;
import ca.lambton.habittracker.util.Frequency;
import ca.lambton.habittracker.util.HabitType;

public class HabitDetailFragment extends Fragment {
    private FragmentHabitDetailBinding binding;
    private HabitViewModel habitViewModel;

    private FirebaseUser mUser;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = FragmentHabitDetailBinding.inflate(LayoutInflater.from(requireContext()));

        habitViewModel = new ViewModelProvider(requireActivity()).get(HabitViewModel.class);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if (getArguments() != null) {
            Habit habit = (Habit) getArguments().getSerializable("habit");
            String packageName = requireContext().getPackageName();

            if (habit != null && habit.getImagePath() != null) {
                int drawable = requireContext().getResources().getIdentifier(habit.getImagePath(), "drawable", packageName);
                if (drawable > 0) {
                    Picasso.get().load(drawable).into(binding.detailImage);
                } else {
                    Picasso.get().load(habit.getImagePath()).into(binding.detailImage);
                }
            }

            if (habit != null) {
                String frequency = String.valueOf(habit.getFrequency());
                binding.frequencyValue.setText(frequency);
                binding.durationValue.setText(String.valueOf(habit.getDuration()));
                binding.daysValue.setText(String.valueOf(habit.getFrequency()));
                binding.titleHabit.setText(habit.getName());
                binding.messageValue.setText(habit.getDescription());

                String frequencyTitle = habit.getFrequencyUnit().substring(0, 1).toUpperCase() + habit.getFrequencyUnit().substring(1).toLowerCase();

                binding.daysLabel.setText(frequencyTitle);

                binding.takeHabit.setOnClickListener(v -> {
                    if (mUser != null)
                        habit.setUserId(mUser.getUid());
                    habit.setPredefined(false);

                    habit.setDurationUnit(Duration.HOURS.name());

                    habit.setStartDate(new Date().getTime());
                    // set the id to 0 to generate a new Habit ID
                    habit.setId(0);

                    habit.setHabitType(HabitType.PERSONAL.name());

                    Calendar calendar = Calendar.getInstance(Locale.CANADA);

                    if (habit.getFrequencyUnit().equalsIgnoreCase(Frequency.DAILY.name())) {
                        calendar.add(Calendar.DAY_OF_MONTH, habit.getFrequency());
                        habit.setFrequencyUnit(Frequency.DAILY.name());
                    } else if (habit.getFrequencyUnit().equalsIgnoreCase(Frequency.WEEKLY.getName())) {
                        habit.setFrequencyUnit(Frequency.WEEKLY.name());
                        calendar.add(Calendar.WEEK_OF_MONTH, habit.getFrequency());
                    } else if (habit.getFrequencyUnit().equalsIgnoreCase(Frequency.MONTHLY.getName())) {
                        habit.setFrequencyUnit(Frequency.MONTHLY.name());
                        calendar.add(Calendar.MONTH, habit.getFrequency());
                    }

                    habit.setEndDate(calendar.getTime().getTime());
                    habitViewModel.saveHabit(habit);

                    //int fragmentBackCount = getParentFragmentManager().getBackStackEntryCount();

                    requireActivity().finish();
                    Toast.makeText(requireContext(), "Habit has been added", Toast.LENGTH_SHORT).show();
                });
            }
        }

        return binding.getRoot();
    }

}
