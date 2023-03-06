package ca.lambton.habittracker.view.newhabit;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import ca.lambton.habittracker.R;
import ca.lambton.habittracker.databinding.FragmentNewHabitBinding;

public class NewHabitFragment extends Fragment {

    FragmentNewHabitBinding binding;

    public NewHabitFragment() {
    }

    public static NewHabitFragment newInstance(String param1, String param2) {
        NewHabitFragment fragment = new NewHabitFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentNewHabitBinding.inflate(inflater, container, false);

        binding.categoriesButton.setOnClickListener(this::exploreCategories);
        return binding.getRoot();

    }

    // Navigate to explore pre-defined categories
    private void exploreCategories(View view) {

        NavDirections navDirections = NewHabitFragmentDirections.actionNewHabitFragmentToNavDefinedHabit();
        Navigation.findNavController(view).navigate(navDirections);
    }
}