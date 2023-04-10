package ca.lambton.habittracker.habit.view.fragment.predefined;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import ca.lambton.habittracker.R;
import ca.lambton.habittracker.category.model.Category;
import ca.lambton.habittracker.databinding.FragmentCategoryHabitBinding;
import ca.lambton.habittracker.habit.view.fragment.fragment.habit.CollectionCardCategoryFragment;
import ca.lambton.habittracker.habit.view.fragment.fragment.habit.description.HabitCategoryDescriptionFragment;

public class CategoryHabitPredefinedFragment extends Fragment {

    FragmentCategoryHabitBinding binding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = FragmentCategoryHabitBinding.inflate(LayoutInflater.from(requireContext()));


    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        if (getArguments() != null) {
            Category category = (Category) getArguments().getSerializable("category");
            FragmentManager parentFragmentManager = getParentFragmentManager();

            // Category info
            CategoryCardStackFragment collectionCardCategoryFragment =  CategoryCardStackFragment.newInstance(category);
            parentFragmentManager.beginTransaction().replace(R.id.stack_view, collectionCardCategoryFragment).addToBackStack(null).commit();

            // Habits options
            HabitCategoryDescriptionFragment habitCategoryDescriptionFragment = HabitCategoryDescriptionFragment.newInstance(category);
            parentFragmentManager.beginTransaction().replace(R.id.habit_category_desc_fragment, habitCategoryDescriptionFragment).addToBackStack(null).commit();

        }


        return binding.getRoot();
    }
}
