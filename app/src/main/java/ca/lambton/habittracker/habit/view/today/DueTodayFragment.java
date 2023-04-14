package ca.lambton.habittracker.habit.view.today;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import ca.lambton.habittracker.R;
import ca.lambton.habittracker.databinding.FragmentDueForDayScreenBinding;
import ca.lambton.habittracker.habit.view.progress.TodayReportFragment;

public class DueTodayFragment extends Fragment {

    FragmentDueForDayScreenBinding binding;
    private final Handler handler = new Handler();
    private final Runnable runnable = () -> {

        handler.post(() -> {
            // Update main thread if needed
            //DUE TODAY CONTAINER
            Fragment dueTodayProgress = new DueTodayProgressFragment();
            FragmentManager fragmentManager = getChildFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.progress_button_container, dueTodayProgress).commit();

            //GRAPH CONTAINER
            Fragment todayGraphReport = new TodayReportFragment();
            fragmentManager.beginTransaction().replace(R.id.graph_report_container, todayGraphReport).commit();

        });
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = FragmentDueForDayScreenBinding.inflate(LayoutInflater.from(requireContext()));


    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Thread startProcess = new Thread(runnable);
        startProcess.start();
    }
}
