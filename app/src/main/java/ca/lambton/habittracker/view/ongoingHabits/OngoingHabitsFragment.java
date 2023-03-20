package ca.lambton.habittracker.view.ongoingHabits;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import ca.lambton.habittracker.R;
import ca.lambton.habittracker.databinding.FragmentCreateHabitLayoutBinding;
import ca.lambton.habittracker.databinding.FragmentOngoingHabitsBinding;

public class OngoingHabitsFragment extends Fragment {

    FragmentOngoingHabitsBinding binding;

    public OngoingHabitsFragment() {
        // Required empty public constructor
    }

    public static OngoingHabitsFragment newInstance(String param1, String param2) {
        OngoingHabitsFragment fragment = new OngoingHabitsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ongoing_habits, container, false);
        binding = FragmentOngoingHabitsBinding.inflate(inflater, container, false);

        binding.allHabitsCard.setOnClickListener(this::allHabitsCardClicked);
        binding.privateHabitsCard.setOnClickListener(this::privateHabitsCardClicked);
        binding.groupHabitsCard.setOnClickListener(this::groupHabitsCardClicked);
        //binding.publicChallengesCard.setOnClickListener(this::publicChallengesCardClicked);

        return binding.getRoot();
    }

    private void allHabitsCardClicked(View view) {
        Navigation.findNavController(getView()).navigate(R.id.allHabitsFragment);
    }

    private void privateHabitsCardClicked(View view) {
        Navigation.findNavController(getView()).navigate(R.id.privateHabitsFragment);
    }

    private void groupHabitsCardClicked(View view) {
        Navigation.findNavController(getView()).navigate(R.id.groupHabitsFragment);
    }

    private void publicChallengesCardClicked(View view) {
        Navigation.findNavController(getView()).navigate(R.id.publicChallengesFragment);
    }
}