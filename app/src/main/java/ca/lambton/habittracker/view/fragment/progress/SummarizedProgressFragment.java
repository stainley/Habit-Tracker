package ca.lambton.habittracker.view.fragment.progress;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.Arrays;

import ca.lambton.habittracker.R;
import ca.lambton.habittracker.databinding.FragmentTodayProgressBinding;

public class SummarizedProgressFragment extends Fragment {
    private FragmentTodayProgressBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = FragmentTodayProgressBinding.inflate(inflater);

        Fragment recycleDetailedProgress = new TodayDetailProgressFragment();
        getParentFragmentManager().beginTransaction().replace(R.id.fragment_detailed_progress, recycleDetailedProgress).commit();

        // pass the values
        calculateAvgProgress(65, 55, 25, 85);
        return binding.getRoot();
    }

    private void calculateAvgProgress(int... progress) {
        // TODO: pass the average of habit process integer
        int sum = Arrays.stream(progress).sum();

        binding.percentageNumText.setText((sum / progress.length) + "%");
        binding.totalDailyProgressbar.setProgress((sum / progress.length), true);
    }
}
