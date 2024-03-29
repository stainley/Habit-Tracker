package ca.lambton.habittracker.habit.view.fragment.fragment.habit.description;

import static ca.lambton.habittracker.habit.view.fragment.fragment.habit.DefinedHabitFragmentDirections.*;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.stream.Collectors;

import ca.lambton.habittracker.R;
import ca.lambton.habittracker.category.model.Category;
import ca.lambton.habittracker.databinding.FragmentCategoryHabitDescriptionBinding;
import ca.lambton.habittracker.habit.model.Habit;
import ca.lambton.habittracker.habit.view.fragment.fragment.habit.DefinedHabitFragmentDirections;
import ca.lambton.habittracker.habit.viewmodel.HabitViewModel;
import ca.lambton.habittracker.habit.viewmodel.HabitViewModelFactory;

public class HabitCategoryDescriptionFragment extends Fragment {
    private static final String TAG = HabitCategoryDescriptionFragment.class.getSimpleName();
    FragmentCategoryHabitDescriptionBinding binding;
    private TextView habitFoodTitleText;
    private TextView habitDurationHabitText;
    private TextView habitTimeDurationText;
    private TextView habitFrequencyTex;
    private RecyclerView collectionCustomHabitRv;
    private CategoryButtonRVAdapter categoryButtonRVAdapter;

    public HabitCategoryDescriptionFragment() {
    }

    @NonNull
    public static HabitCategoryDescriptionFragment newInstance(Category category) {
        HabitCategoryDescriptionFragment fragment = new HabitCategoryDescriptionFragment();
        Bundle args = new Bundle();
        args.putSerializable("category", category);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = FragmentCategoryHabitDescriptionBinding.inflate(LayoutInflater.from(requireContext()));

        collectionCustomHabitRv = binding.collectionCustomHabitRv;
        habitFoodTitleText = binding.foodHabitTitle;
        habitDurationHabitText = binding.dayDurationMessage;
        habitTimeDurationText = binding.timeDuration;
        habitFrequencyTex = binding.frequencyMessage;

        collectionCustomHabitRv.setLayoutManager(new GridLayoutManager(requireContext(), 1, RecyclerView.HORIZONTAL, false));

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {




        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            Category category = (Category) getArguments().getSerializable("category");

            if (category != null) {
                habitFoodTitleText.setText(category.getName());
                habitDurationHabitText.setText(String.valueOf(category.getDuration()));
                habitTimeDurationText.setText(String.valueOf(category.getInterval()));
                habitFrequencyTex.setText(category.getFrequencyUnit());

                HabitViewModel habitViewModel = new ViewModelProvider(requireActivity(), new HabitViewModelFactory(requireActivity().getApplication())).get(HabitViewModel.class);
                habitViewModel.getAllHabitByCategory(category.getId()).observe(requireActivity(), habits -> {
                    if (habits.size() > 0) {
                        List<String> habitsTitle = habits.stream()
                                .filter(Habit::isPredefined)
                                .map(Habit::getName)
                                .collect(Collectors.toList());

                        this.categoryButtonRVAdapter = new CategoryButtonRVAdapter(habitsTitle, (view1, position) -> view1.setOnClickListener(v -> {

                            NavDirections navDirections = DefinedHabitFragmentDirections.actionNavDefinedHabitToNavHabitDetail().setHabit(habits.get(position));
                            Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_main).navigate(navDirections);
                        }));
                        collectionCustomHabitRv.setAdapter(categoryButtonRVAdapter);
                    }
                });
            }
        }
    }
}
