package ca.lambton.habittracker.habit.view.ongoingHabits;


import static ca.lambton.habittracker.habit.view.ongoingHabits.PublicChallengesFragmentDirections.actionPublicChallengesFragmentToNavHabitDetail;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.search.SearchView;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import ca.lambton.habittracker.databinding.FragmentPublicChallengesBinding;
import ca.lambton.habittracker.habit.model.Habit;
import ca.lambton.habittracker.habit.view.ongoingHabits.adapter.OngoingHabitPublicAdapter;
import ca.lambton.habittracker.habit.viewmodel.HabitViewModel;
import ca.lambton.habittracker.habit.viewmodel.HabitViewModelFactory;

public class PublicChallengesFragment extends Fragment {
    private FragmentPublicChallengesBinding binding;
    private OngoingHabitPublicAdapter ongoingHabitPublicAdapter;
    private final List<Habit> habits = new ArrayList<>();
    private final List<Habit> habitsFiltered = new ArrayList<>();

    private SearchView searchViewHabit;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = FragmentPublicChallengesBinding.inflate(LayoutInflater.from(requireContext()));
        RecyclerView recyclerView = binding.publicOngoingHabitsList;
        searchViewHabit = binding.searchView;

        HabitViewModel habitViewModel = new ViewModelProvider(this, new HabitViewModelFactory(requireActivity().getApplication())).get(HabitViewModel.class);

        ongoingHabitPublicAdapter = new OngoingHabitPublicAdapter(habits, position -> {
            NavDirections navDirections = actionPublicChallengesFragmentToNavHabitDetail().setHabit(habits.get(position));
            Navigation.findNavController(requireView()).navigate(navDirections);
        });

        habitViewModel.getAllHabitCloud().observe(this, habitResult -> {
            this.habits.clear();
            this.habits.addAll(habitResult);
            ongoingHabitPublicAdapter.notifyItemRangeChanged(0, habitResult.size());
        });

        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        recyclerView.setAdapter(ongoingHabitPublicAdapter);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        searchViewHabit.getEditText().addTextChangedListener(getTextWatcherSupplier().get());
    }

    // SEARCH BAR
    @NonNull
    private Supplier<TextWatcher> getTextWatcherSupplier() {

        RecyclerView habitFilterRecycle = binding.publicOngoingHabitFiltered;
        return () -> new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence letter, int start, int before, int count) {
                habitsFiltered.clear();
                habitFilterRecycle.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));

                // filter category that contain x value
                List<Habit> postResultFiltered = habits.stream().filter(habits -> {

                    if (letter.length() == 0) return false;
                    return habits.getName().toLowerCase().contains(letter.toString().toLowerCase());
                }).collect(Collectors.toList());

                habitsFiltered.addAll(postResultFiltered);

                OngoingHabitPublicAdapter ongoingHabitPublicAdapter = new OngoingHabitPublicAdapter(habitsFiltered, position -> {
                    NavDirections navDirections = actionPublicChallengesFragmentToNavHabitDetail().setHabit(habitsFiltered.get(position));
                    Navigation.findNavController(requireView()).navigate(navDirections);
                });

                habitFilterRecycle.setAdapter(ongoingHabitPublicAdapter);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();

    }
}