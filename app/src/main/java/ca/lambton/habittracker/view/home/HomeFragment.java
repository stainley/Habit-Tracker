package ca.lambton.habittracker.view.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import ca.lambton.habittracker.R;
import ca.lambton.habittracker.databinding.FragmentHomeBinding;
import ca.lambton.habittracker.view.fragment.calendar.ProgressCalendarFragment;
import ca.lambton.habittracker.view.fragment.progress.SummarizedProgressFragment;
import ca.lambton.habittracker.view.fragment.quote.QuoteFragment;

public class HomeFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragmentHomeBinding binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        FragmentManager supportFragmentManager = getParentFragmentManager();

        Fragment calendarFragment = new ProgressCalendarFragment();
        supportFragmentManager.beginTransaction().replace(R.id.homeCalendarView, calendarFragment).commit();


        Fragment quoteDayFragment = new QuoteFragment();
        supportFragmentManager.beginTransaction().replace(R.id.quoteDayFragmentView, quoteDayFragment).commit();


        Fragment summarizedProgress = new SummarizedProgressFragment();
        supportFragmentManager.beginTransaction().replace(R.id.summarizedProgressView, summarizedProgress).commit();

        return root;
    }
}
