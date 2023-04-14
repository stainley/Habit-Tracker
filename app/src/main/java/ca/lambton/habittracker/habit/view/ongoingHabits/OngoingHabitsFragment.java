package ca.lambton.habittracker.habit.view.ongoingHabits;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import java.util.ArrayList;
import java.util.List;

import ca.lambton.habittracker.R;
import ca.lambton.habittracker.category.model.Category;
import ca.lambton.habittracker.category.viewmodel.CategoryViewModel;
import ca.lambton.habittracker.category.viewmodel.CategoryViewModelFactory;
import ca.lambton.habittracker.databinding.FragmentOngoingHabitsBinding;

public class OngoingHabitsFragment extends Fragment {

    private FragmentOngoingHabitsBinding binding;
    private CategoryViewModel categoryViewModel;
    private final List<Category> categories = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = FragmentOngoingHabitsBinding.inflate(LayoutInflater.from(requireContext()));

        binding.allHabitsCard.setOnClickListener(this::allHabitsCardClicked);
        binding.privateHabitsCard.setOnClickListener(this::privateHabitsCardClicked);
        //binding.groupHabitsCard.setOnClickListener(this::groupHabitsCardClicked);
        binding.publicHabitCard.setOnClickListener(this::publicChallengesCardClicked);

        binding.explorerCategoryButton.setOnClickListener(this::showMoreCategory);

        categoryViewModel = new ViewModelProvider(getViewModelStore(), new CategoryViewModelFactory(requireActivity().getApplication())).get(CategoryViewModel.class);

    }

    private void showMoreCategory(View view) {

        NavDirections navDirections = OngoingHabitsFragmentDirections.actionOngoingHabitFragmentToNavDefinedHabit();
        Navigation.findNavController(view).navigate(navDirections);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        return binding.getRoot();
    }

    private void allHabitsCardClicked(View view) {
        Navigation.findNavController(requireView()).navigate(R.id.allHabitsFragment);
    }

    private void privateHabitsCardClicked(View view) {
        Navigation.findNavController(requireView()).navigate(R.id.privateHabitsFragment);
    }

    private void groupHabitsCardClicked(View view) {
        Navigation.findNavController(requireView()).navigate(R.id.groupHabitsFragment);
    }

    private void publicChallengesCardClicked(View view) {
        NavDirections navPublicChallenge = OngoingHabitsFragmentDirections.actionOngoingHabitFragmentToPublicChallengesFragment();

        Navigation.findNavController(requireView()).navigate(navPublicChallenge);
    }

    @Override
    public void onStart() {
        super.onStart();

        categoryViewModel.getAllCategories().observe(getViewLifecycleOwner(), this.categories::addAll);
    }
}