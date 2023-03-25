package ca.lambton.habittracker.view.fragment.progress;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import ca.lambton.habittracker.databinding.FragmentDailyHabitProgressBinding;

public class DailyProgressFragment extends Fragment {

    private FragmentDailyHabitProgressBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentDailyHabitProgressBinding.inflate(inflater);


        return binding.getRoot();
    }
}
