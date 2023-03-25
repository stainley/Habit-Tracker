package ca.lambton.habittracker.view.settings;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ca.lambton.habittracker.R;
import ca.lambton.habittracker.databinding.FragmentSettingsBinding;
import ca.lambton.habittracker.view.fragment.habit.CollectionCardCategoryFragment;
import ca.lambton.habittracker.view.fragment.habit.first.FirstHabitFragment;

public class SettingsFragment extends Fragment {

    FragmentSettingsBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentSettingsBinding.inflate(inflater);
        View view = binding.getRoot();

        /*Fragment collectionCardCategoryFragment = new CollectionCardCategoryFragment();
        FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();

        fragmentTransaction.replace(R.id.stack_view, collectionCardCategoryFragment).commit();*/


        Fragment firstHabit = new FirstHabitFragment();
        return view;
    }
}