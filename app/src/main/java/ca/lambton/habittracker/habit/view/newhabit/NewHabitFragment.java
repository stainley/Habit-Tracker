package ca.lambton.habittracker.habit.view.newhabit;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;
import java.util.stream.Collectors;

import ca.lambton.habittracker.R;
import ca.lambton.habittracker.databinding.FragmentNewHabitBinding;
import ca.lambton.habittracker.habit.model.Habit;
import ca.lambton.habittracker.habit.viewmodel.HabitViewModel;
import ca.lambton.habittracker.util.HabitType;

public class NewHabitFragment extends Fragment {

    private FragmentNewHabitBinding binding;
    private HabitViewModel habitViewModel;

    private FirebaseUser mUser;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = FragmentNewHabitBinding.inflate(LayoutInflater.from(requireContext()));

        habitViewModel = new ViewModelProvider(requireActivity()).get(HabitViewModel.class);
        mUser = FirebaseAuth.getInstance().getCurrentUser();

        binding.categoriesButton.setOnClickListener(this::exploreCategories);
        binding.nextButton.setOnClickListener(this::createHabit);


    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        return binding.getRoot();
    }

    // Navigate to explore pre-defined categories
    private void exploreCategories(View view) {

        NavDirections navDirections = NewHabitFragmentDirections.actionNewHabitFragmentToNavDefinedHabit();
        Navigation.findNavController(view).navigate(navDirections);
    }

    private void createHabit(View view) {
        Navigation.findNavController(requireView()).navigate(R.id.createHabitFragment);
    }

    @Override
    public void onStart() {
        super.onStart();

        if (mUser != null) {
            habitViewModel.getAllHabit().observe(requireActivity(), habits -> {

                List<Habit> fetchAllMyPublicHabit = habits.stream()
                        .filter(uid -> uid.getUserId().equals(mUser.getUid()))
                        .filter(type -> type.getHabitType().equalsIgnoreCase(HabitType.PUBLIC.getName()))
                        .collect(Collectors.toList());

                for (Habit habit : fetchAllMyPublicHabit) {
                    System.out.println(habit);
                }

            });
        }
    }
}