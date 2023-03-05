package ca.lambton.habittracker.view.fragment.habit.description;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import ca.lambton.habittracker.databinding.FragmentCategoryHabitDescriptionBinding;
import ca.lambton.habittracker.util.CategoryType;

public class HabitCategoryDescriptionFragment extends Fragment {

    FragmentCategoryHabitDescriptionBinding binding;
    private CategoryType categoryType;

    public HabitCategoryDescriptionFragment() {
    }

    public static HabitCategoryDescriptionFragment newInstance(CategoryType categoryType) {
        HabitCategoryDescriptionFragment fragment = new HabitCategoryDescriptionFragment();
        Bundle args = new Bundle();
        args.putSerializable("category", categoryType);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = FragmentCategoryHabitDescriptionBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            categoryType = (CategoryType) getArguments().getSerializable("category");

            // TODO: obtain value from the Database to populate the fields
            switch (categoryType) {
                case RUNNING:
                    binding.foodHabitTitle.setText("Running");
                    binding.dayDurationMessage.setText("15 days");
                    break;
                case FOOD:
                    binding.foodHabitTitle.setText("Food");
                    binding.dayDurationMessage.setText("15 days");
                    break;
                case YOGA:
                    binding.foodHabitTitle.setText("Yoga");
                    binding.dayDurationMessage.setText("15 days");
                    break;
                case EXERCISE:
                    binding.foodHabitTitle.setText("Exercise");
                    binding.dayDurationMessage.setText("15 days");
                    break;
                case READING:
                    binding.foodHabitTitle.setText("Reading");
                    binding.dayDurationMessage.setText("15 days");
                    break;
                case STRETCHING:
                    binding.foodHabitTitle.setText("Streching");
                    binding.dayDurationMessage.setText("15 days");
                    break;
            }
        }

    }
}
