package ca.lambton.habittracker.view.ongoingHabits;

import android.net.wifi.aware.PublishDiscoverySession;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import ca.lambton.habittracker.R;
import ca.lambton.habittracker.databinding.FragmentOngoingHabitsBinding;

public class OngoingHabitsFragment extends Fragment {

    private FragmentOngoingHabitsBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = FragmentOngoingHabitsBinding.inflate(LayoutInflater.from(requireContext()));

        binding.allHabitsCard.setOnClickListener(this::allHabitsCardClicked);
        binding.privateHabitsCard.setOnClickListener(this::privateHabitsCardClicked);
        //binding.groupHabitsCard.setOnClickListener(this::groupHabitsCardClicked);
        binding.publicHabitCard.setOnClickListener(this::publicChallengesCardClicked);

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
}