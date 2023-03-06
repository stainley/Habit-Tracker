package ca.lambton.habittracker.view.fragment.habit.description;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.util.Arrays;
import java.util.List;

import ca.lambton.habittracker.databinding.FragmentCategoryHabitDescriptionBinding;
import ca.lambton.habittracker.util.CategoryType;

public class HabitCategoryDescriptionFragment extends Fragment {

    FragmentCategoryHabitDescriptionBinding binding;
    private CategoryType categoryType;
    private TextView habitFoodTitleText;
    private TextView habitDurationHabitText;
    private TextView habitTimeDurationText;
    private TextView habitFrequencyTex;
    private RecyclerView collectionCustomHabitRv;

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
        collectionCustomHabitRv = binding.collectionCustomHabitRv;
        habitFoodTitleText = binding.foodHabitTitle;
        habitDurationHabitText = binding.dayDurationMessage;
        habitTimeDurationText = binding.timeDuration;
        habitFrequencyTex = binding.frequencyMessage;

        collectionCustomHabitRv.setLayoutManager(new GridLayoutManager(requireContext(), 1, RecyclerView.HORIZONTAL, false));

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            categoryType = (CategoryType) getArguments().getSerializable("category");
            CategoryButtonRVAdapter categoryButtonRVAdapter;

            // TODO: obtain value from the Database to populate the fields
            switch (categoryType) {
                case RUNNING:
                    habitFoodTitleText.setText("Running");
                    habitDurationHabitText.setText("10 days");
                    habitFrequencyTex.setText("Weekly");
                    habitTimeDurationText.setText("0 day");
                    break;
                case FOOD:
                    habitFoodTitleText.setText("Food");
                    habitTimeDurationText.setText("10 - 30 mins");
                    habitFrequencyTex.setText("Daily");
                    habitDurationHabitText.setText("0 days");
                    List<String> foodButtons = Arrays.asList("Drink fruit juice", "Eat Raisins", "Add Veggies");
                    categoryButtonRVAdapter = new CategoryButtonRVAdapter(foodButtons, (view1, position) -> view1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(requireContext(), "Button pressed: " + foodButtons.get(position), Toast.LENGTH_SHORT).show();
                        }
                    }));
                    collectionCustomHabitRv.setAdapter(categoryButtonRVAdapter);
                    break;
                case YOGA:
                    habitFoodTitleText.setText("Yoga");
                    habitFrequencyTex.setText("Weekly/Monthly");
                    habitDurationHabitText.setText("0 days");
                    habitTimeDurationText.setText("0 day");
                    break;
                case EXERCISE:
                    habitFoodTitleText.setText("Exercise");
                    habitFrequencyTex.setText("Daily/Weekly");
                    habitDurationHabitText.setText("0 days");
                    habitTimeDurationText.setText("0 day");
                    break;
                case READING:
                    habitFoodTitleText.setText("Reading");
                    habitFrequencyTex.setText("Daily");
                    habitDurationHabitText.setText("0 days");
                    break;
                case STRETCHING:
                    habitFoodTitleText.setText("Streching");
                    habitFrequencyTex.setText("Weekly/Monthly");
                    habitDurationHabitText.setText("0 days");
                    break;
            }
        }
    }
}
