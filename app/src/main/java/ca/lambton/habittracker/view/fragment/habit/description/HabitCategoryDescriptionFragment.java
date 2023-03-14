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
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.navigation.NavigationView;

import java.util.Arrays;
import java.util.List;

import ca.lambton.habittracker.R;
import ca.lambton.habittracker.databinding.FragmentCategoryHabitDescriptionBinding;
import ca.lambton.habittracker.util.CategoryType;
import ca.lambton.habittracker.view.fragment.habit.HabitDetailFragment;

public class HabitCategoryDescriptionFragment extends Fragment {

    FragmentCategoryHabitDescriptionBinding binding;
    private String category;
    private TextView habitFoodTitleText;
    private TextView habitDurationHabitText;
    private TextView habitTimeDurationText;
    private TextView habitFrequencyTex;
    private RecyclerView collectionCustomHabitRv;

    public HabitCategoryDescriptionFragment() {
    }

    public static HabitCategoryDescriptionFragment newInstance(String category) {
        HabitCategoryDescriptionFragment fragment = new HabitCategoryDescriptionFragment();
        Bundle args = new Bundle();
        args.putSerializable("category", category);
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
            category = (String) getArguments().getSerializable("category");
            CategoryButtonRVAdapter categoryButtonRVAdapter;

            // TODO: obtain value from the Database to populate the fields
            switch (category) {
                case "Outdoor Activities":
                    habitFoodTitleText.setText("Running");
                    habitDurationHabitText.setText("10 days");
                    habitFrequencyTex.setText("Weekly");
                    habitTimeDurationText.setText("0 day");
                    break;
                case "Food Habits":
                    habitFoodTitleText.setText("Food");
                    habitTimeDurationText.setText("10 - 30 mins");
                    habitFrequencyTex.setText("Daily");
                    habitDurationHabitText.setText("45 - 70");
                    List<String> foodButtons = Arrays.asList("Drink fruit juice", "Eat Raisins", "Add Veggies");
                    categoryButtonRVAdapter = new CategoryButtonRVAdapter(foodButtons, (view1, position) -> view1.setOnClickListener(v -> {
                        switch (position) {
                            case 0:

                                Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_main).navigate(R.id.nav_habit_detail);
                                break;
                            case 2:
                                // TODO: Invoke second button. Pass the info.
                                break;
                            case 3:
                                // TODO: invoke third button
                                break;
                            default:
                                // TODO: Invalid option
                        }
                    }));
                    collectionCustomHabitRv.setAdapter(categoryButtonRVAdapter);
                    break;
                case "Mental Health":
                    habitFoodTitleText.setText("Yoga");
                    habitFrequencyTex.setText("Weekly/Monthly");
                    habitDurationHabitText.setText("0 days");
                    habitTimeDurationText.setText("0 day");
                    break;
                case "Physical Health":
                    habitFoodTitleText.setText("Exercise");
                    habitFrequencyTex.setText("Daily/Weekly");
                    habitDurationHabitText.setText("0 days");
                    habitTimeDurationText.setText("0 day");
                    break;
                case "Daily":
                    habitFoodTitleText.setText("Reading");
                    habitFrequencyTex.setText("Daily");
                    habitDurationHabitText.setText("0 days");
                    break;
                case "Self Care":
                    habitFoodTitleText.setText("Streching");
                    habitFrequencyTex.setText("Weekly/Monthly");
                    habitDurationHabitText.setText("0 days");
                    break;
            }
        }
    }
}
