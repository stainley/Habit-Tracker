package ca.lambton.habittracker.view.fragment.progress;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ca.lambton.habittracker.R;
import ca.lambton.habittracker.databinding.FragmentTodayDetailProgressBinding;


public class TodayDetailProgressFragment extends Fragment {

    FragmentTodayDetailProgressBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentTodayDetailProgressBinding.inflate(inflater);

        RecyclerView recycleDetailedProgress = binding.recycleDetailedProgress;

        List<DailyProgressData> progressDataList = new ArrayList<>();
        // TODO: Dummy data. pass the value the percentage integer base on the image

        progressDataList.add(new DailyProgressData(requireContext(), "Do Yoga", getProgressIcon(0)));
        progressDataList.add(new DailyProgressData(requireContext(), "Enjoy Outdoors", getProgressIcon(18)));
        progressDataList.add(new DailyProgressData(requireContext(), "Meditation", getProgressIcon(35)));
        progressDataList.add(new DailyProgressData(requireContext(), "Drink Water", getProgressIcon(78)));
        progressDataList.add(new DailyProgressData(requireContext(), "Exercise", null));
        progressDataList.add(new DailyProgressData(requireContext(), "Study", getProgressIcon(65)));

        TodayDetailProgressAdapter progressAdapter = new TodayDetailProgressAdapter(progressDataList);

        recycleDetailedProgress.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
        recycleDetailedProgress.setAdapter(progressAdapter);


        return binding.getRoot();
    }

    private Drawable getProgressIcon(int percentage) {
        Drawable icon = null;

        if (percentage == 0) {
            icon = AppCompatResources.getDrawable(requireContext(), R.drawable.percent_0);
        } else if (percentage > 0 && percentage <= 25) {
            icon = AppCompatResources.getDrawable(requireContext(), R.drawable.percent_25);
        } else if (percentage > 25 && percentage <= 75) {
            icon = AppCompatResources.getDrawable(requireContext(), R.drawable.percent_75);
        } else if (percentage > 75 && percentage <= 100) {
            icon = AppCompatResources.getDrawable(requireContext(), R.drawable.percent_100);
        }

        return icon;
    }


    static class DailyProgressData {

        private final String habitName;
        private final Drawable imageDrawable;
        private final Context context;

        public DailyProgressData(Context context, String habitName, Drawable imageDrawable) {
            this.habitName = habitName;
            this.imageDrawable = imageDrawable;
            this.context = context;
        }


        public String getHabitName() {
            return habitName;
        }

        public Drawable getProgressValue() {
            if (imageDrawable == null) {
                return AppCompatResources.getDrawable(context, R.drawable.percent_0);
            }
            return imageDrawable;
        }
    }
}
